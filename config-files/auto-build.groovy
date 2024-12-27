import groovy.json.JsonSlurper
//自动部署


//防止重复构建
@NonCPS
def cancelPreviousBuilds() {
    //任务名称(一般来说 一个服务一个任务名称)
    def jobName = env.JOB_NAME
    //构建版本号
    def buildNumber = env.BUILD_NUMBER.toInteger()
    //构建任务实例
    def currentJob = Jenkins.instance.getItemByFullName(jobName)

    //获取已经开始构建的任务(往前查十个任务)
    for (i in 1..< 11) {
        def build = currentJob.getBuildByNumber(buildNumber - i)
        def actions = build.getActions(ParametersAction.class)
        if (actions && build) {
            ParametersAction action = actions.get(0)
            //拿构建参数
            def buildEnv = action.getParameter("microEnv").getValue().toString()
            def buildBranch = action.getParameter("branch").getValue().toString()
            def buildMicroServices = action.getParameter("services").getValue().toString()
            //1：如果之前的单服务构建任务，已经开始构建，并且构建参数一致，则停止之前构建
            //2：如果本次构建是all，则停止之前与本次分支、环境一致的所有单服务构建任务
            if (build.isBuilding() && build.number.toInteger() != buildNumber && isRepeatBuild(buildEnv, buildBranch, buildMicroServices)) {
                build.doStop()
            }
        }
    }

    //从任务队列实例获取 未开始构建的任务(因为资源不足或并发限制等待执行的任务)
    def jenkinsQueue = Jenkins.instance.queue
    jenkinsQueue.items.findAll { it.task.name.equals(jobName) }.each {
        Map<String, String> paramMap = new HashMap<>()
        String[] paramArray = it.getParams().split("\n")
        paramArray.each { String its ->
            if (its.contains("=")) {
                String[] params = its.split("=")
                paramMap.put(params[0], params[1])
            }
        }

        def buildEnv = paramMap.get("microEnv")
        def buildBranch = paramMap.get("branch")
        def buildServices = paramMap.get("services")
        if (isRepeatBuild(buildEnv, buildBranch, buildServices)) {
            //从队列中移出
            jenkinsQueue.cancel(it.task)
        }
    }
}

@NonCPS
boolean isRepeatBuild(String buildEnv, String buildBranch, String buildMicroServices) {
    def currEnv = getBuildEnv(params.get("microEnv")).toString()
    def currBranch = getBuildBranch(params.get("branch")).toString()
    def currService = getBuildServices([params.get("services")])
    return Objects.equals(currEnv, buildEnv) && Objects.equals(currBranch, buildBranch) && (currService.contains(buildMicroServices) || currService.contains('all'))
}

@NonCPS
//判断选项参数和gitlab传入参数，选择构建分支
def getBuildBranch(String buildBranch) {
    if (!env.gitbranch) {
        return buildBranch
    }
    def gitBranch = env.gitbranch.replaceAll("refs/heads/", "")
    return (gitBranch && !gitBranch.trim().isEmpty()) ? gitBranch : buildBranch
}

@NonCPS
//判断选项参数和gitlab传入参数，选择构建环境
def getBuildEnv(String buildEnv) {
    if (!env.gitbranch) {
        return buildEnv
    }
    def gitBranch = env.gitbranch.replaceAll("refs/heads/", "")
    return (gitBranch == 'master') ? 'release' : (gitBranch && !gitBranch.trim().isEmpty()) ? gitBranch : buildEnv
}

@NonCPS
//判断选项参数和gitlab传入参数，选择构建服务
def getBuildServices(List buildMicroServices) {
    def jsonSlurper = new JsonSlurper()
    def changedFiles = env.serviceChanged ? jsonSlurper.parseText(env.serviceChanged) : []
    def removedFiles = env.serviceRemoved ? jsonSlurper.parseText(env.serviceRemoved) : []
    def addedFiles = env.serviceAdded ? jsonSlurper.parseText(env.serviceAdded) : []

    if (changedFiles.isEmpty() && removedFiles.isEmpty() && addedFiles.isEmpty()) {
        return buildMicroServices
    }
    Set<String> servicesSet = new HashSet<>()
    def allFiles = (changedFiles + removedFiles + addedFiles).findAll { it?.trim() && it.trim() != '' }
    //没有修改的时候 自己把自己的构建停止
    if (allFiles.isEmpty()) {
        def buildNumber = env.BUILD_NUMBER.toInteger()
        def build = currentJob.getBuildByNumber(buildNumber)
        if (build.isBuilding()) {
            build.doStop()
        }
    }
    // 遍历文件列表进行判断
    boolean shouldReturnAll = allFiles.any { filePath ->
        if (!filePath.contains('/')) {
            return true
        } else if (filePath.contains('provider-')) {
            int providerIndex = filePath.indexOf('provider-')
            def path = filePath.substring(providerIndex).split('/')[0]
            //排除api和starter
            if (path.contains("-api")) {
                servicesSet.add(path.replaceAll("provider-api-", ""))
            } else if (path.endsWith("-starter")) {
                return true
            } else {
                servicesSet.add(path)
            }
        }
        return false
    }
    return shouldReturnAll ? ['all'] : servicesSet.toList()
}

@NonCPS
def dockerLogin() {
    sh "docker login --username=2020刘先生 --password=liu1069823632 registry.cn-heyuan.aliyuncs.com"
}

@NonCPS
def getJavaOptions(serviceName, shortName, environment, serviceTag) {
    return """
        -e JAVA_OPTS='
        -Dserver.environment=${environment}
        -Dserver.name=${shortName}
        -Djava.security.properties=/opt/java/tls-allowed.java.security
        -Djava.security.egd=file:/dev/./urandom
        -Duser.timezone=Asia/Shanghai
        -Djasypt.encryptor.password=d2FueHVlLXJlbGVhc2U=
        -Dlogging.config=classpath:log4j2-local.xml
        -server
        -Xms1g
        -Xmx1g
        -XX:MetaspaceSize=256m
        -XX:MaxMetaspaceSize=256m
        -XX:+HeapDumpOnOutOfMemoryError
        -XX:HeapDumpPath=/dumps/${environment}/${serviceName}
        -XX:+UseG1GC'
        ${serviceTag}
    """.trim().replaceAll("\\s+", " ")
}

pipeline {
    //不限制执行者
    agent any
    options {
        timeout(time: 1, unit: 'HOURS')
    }
    parameters {
        choice choices: ['true', 'false'], description: '重新编译', name: 'reBuild'
        choice choices: ['alpha', 'beta', 'release'], description: '环境', name: 'microEnv'
        choice choices: ['alpha', 'beta', 'master'], description: '分支', name: 'branch'
        choice choices: ['all', 'gateway', 'provider-auth', 'provider-base', 'provider-quartz'], description: '服务', name: 'services'
    }
    stages {
        stage('Preparation') {
            //异步执行多个阶段
            parallel {
                stage('Cancel Repeat Builds') {
                    steps {
                        cancelPreviousBuilds()
                    }
                }
                stage('Checkout SCM') {
                    steps {
                        script {
                            def buildBranch = getBuildBranch(params.get("branch"))
                            git branch: "${buildBranch}", credentialsId: 'd04587eb-e120-422b-9107-c8be728bc3ab', url: 'https://gitlab.com/21_Java/cloudscaffold.git'
                            sh 'chmod +x ./gradlew'
                        }
                    }
                }
                stage('Docker Login') {
                    steps {
                        dockerLogin()
                    }
                }
                stage('Check Database') {
                    steps {
                        script {
                            def buildEnv = getBuildEnv(params.get("microEnv"))
                            def servicesList = getBuildServices([params.get("services")])
                            def allServices = ['provider-auth', 'provider-base', 'provider-quartz']
                            env.BUILD_ENV = buildEnv
                            env.BUILD_SER = servicesList.join(' ')
                            env.BUILD_SER_FIRST = servicesList[0].toString()
                            env.BUILD_ALL_SERVICES = allServices.join(' ')
                        }
                        sh '''
#!/bin/bash -ex
set -e
echo "数据库结构完整性检查开始，环境：[${BUILD_ENV}]，检查服务：[${BUILD_SER}]。"
if [ "${BUILD_SER_FIRST}" = "all" ]; then
    for serviceName in $BUILD_ALL_SERVICES
    do
        ./gradlew -Dcheck.env=${BUILD_ENV} -Dchecked.service.name=$serviceName :database-check:run --no-daemon;
    done
else 
    for serviceName in $BUILD_SER
    do
        ./gradlew -Dcheck.env=${BUILD_ENV} -Dchecked.service.name=$serviceName :database-check:run --no-daemon;
    done
fi
echo \'数据库结构完整性检查结束\'
'''
                    }
                }
            }
        }
        stage('Build') {
            when {
                allOf {
                    environment name: 'reBuild', value: 'true'
                }
            }
            steps {
                script {
                    def buildEnv = getBuildEnv(params.get("microEnv"))
                    def servicesList = getBuildServices([params.get("services")])
                    def allServices = ['gateway', 'provider-auth', 'provider-base', 'provider-quartz']
                    // 设置环境变量
                    env.BUILD_ENV = buildEnv
                    env.BUILD_SER = servicesList.join(' ')
                    env.BUILD_ALL_SERVICES = allServices.join(' ')
                    env.BUILD_SER_SIZE = servicesList.size()
                    env.BUILD_SER_FIRST = servicesList[0].toString()
                    env.BUILD_DATE = new Date().format("yyyyMMddHHmm", TimeZone.getTimeZone('Asia/Shanghai'))
                    env.CURRENT_TAG = "${buildEnv}-${env.BUILD_DATE}"
                }
                sh '''
#!/bin/bash -ex
# 该命令设置了脚本的错误处理，当任何命令返回非零退出状态时，脚本会立即退出。
set -e
echo "build start"
echo "BUILD_ENV : ${BUILD_ENV}"
echo "BUILD_SER : ${BUILD_SER}"
echo "BUILD_SER_SIZE : ${BUILD_SER_SIZE}"
echo "BUILD_SER_FIRST : ${BUILD_SER_FIRST}"
echo "BUILD_DATE : ${BUILD_DATE}"
echo "CURRENT_TAG : ${CURRENT_TAG}"

#如果长度是一并且是all那就部署全部
if [ "${BUILD_SER_SIZE}" -eq 1 ]; then
    if [ "${BUILD_SER_FIRST}" = "all" ]; then
        for name in $BUILD_ALL_SERVICES
        do
            if [ "$name" = "gateway" ]; then
                ./gradlew clean -PdockerVersion=${BUILD_ENV} -Pprofile=${BUILD_ENV} :$name:docker -x test --no-daemon
                echo "push  $name start"
                docker push registry.cn-heyuan.aliyuncs.com/21cloud/$name:${BUILD_ENV}
                docker tag registry.cn-heyuan.aliyuncs.com/21cloud/$name:${BUILD_ENV} registry.cn-heyuan.aliyuncs.com/21cloud/$name:${CURRENT_TAG}
                docker push registry.cn-heyuan.aliyuncs.com/21cloud/$name:${CURRENT_TAG}
                echo "push  $name end"
            else 
                ./gradlew clean -PdockerVersion=${BUILD_ENV} -Pprofile=${BUILD_ENV} :provider:$name:docker -x test --no-daemon
                docker push registry.cn-heyuan.aliyuncs.com/21cloud/$name:${BUILD_ENV}
                docker tag registry.cn-heyuan.aliyuncs.com/21cloud/$name:${BUILD_ENV} registry.cn-heyuan.aliyuncs.com/21cloud/$name:${CURRENT_TAG}
                docker push registry.cn-heyuan.aliyuncs.com/21cloud/$name:${CURRENT_TAG}   
            fi  
        done
    elif [ "${BUILD_SER_FIRST}" = "gateway" ]; then
        ./gradlew clean -PdockerVersion=${BUILD_ENV} -Pprofile=${BUILD_ENV} :${BUILD_SER_FIRST}:docker -x test --no-daemon        
        docker push registry.cn-heyuan.aliyuncs.com/21cloud/${BUILD_SER_FIRST}:${BUILD_ENV}
        docker tag registry.cn-heyuan.aliyuncs.com/21cloud/${BUILD_SER_FIRST}:${BUILD_ENV} registry.cn-heyuan.aliyuncs.com/21cloud/${BUILD_SER_FIRST}:${CURRENT_TAG}
        docker push registry.cn-heyuan.aliyuncs.com/21cloud/${BUILD_SER_FIRST}:${CURRENT_TAG}
    else       
        ./gradlew clean -PdockerVersion=${BUILD_ENV} -Pprofile=${BUILD_ENV} :provider:${BUILD_SER_FIRST}:docker -x test --no-daemon
        docker push registry.cn-heyuan.aliyuncs.com/21cloud/${BUILD_SER_FIRST}:${BUILD_ENV}
        docker tag registry.cn-heyuan.aliyuncs.com/21cloud/${BUILD_SER_FIRST}:${BUILD_ENV} registry.cn-heyuan.aliyuncs.com/21cloud/${BUILD_SER_FIRST}:${CURRENT_TAG}
        docker push registry.cn-heyuan.aliyuncs.com/21cloud/${BUILD_SER_FIRST}:${CURRENT_TAG}
    fi
else
    for serviceName in $BUILD_SER
    do
        if [ "$serviceName" = "gateway" ]; then
            ./gradlew clean -PdockerVersion=${BUILD_ENV} -Pprofile=${BUILD_ENV} :$serviceName:docker -x test --no-daemon
            docker push registry.cn-heyuan.aliyuncs.com/21cloud/$serviceName:${BUILD_ENV}
            docker tag registry.cn-heyuan.aliyuncs.com/21cloud/$serviceName:${BUILD_ENV} registry.cn-heyuan.aliyuncs.com/21cloud/$serviceName:${CURRENT_TAG}
            docker push registry.cn-heyuan.aliyuncs.com/21cloud/$serviceName:${CURRENT_TAG}
        else 
            ./gradlew clean -PdockerVersion=${BUILD_ENV} -Pprofile=${BUILD_ENV} :provider:$serviceName:docker -x test --no-daemon
            docker push registry.cn-heyuan.aliyuncs.com/21cloud/$serviceName:${BUILD_ENV}
            docker tag registry.cn-heyuan.aliyuncs.com/21cloud/$serviceName:${BUILD_ENV} registry.cn-heyuan.aliyuncs.com/21cloud/$serviceName:${CURRENT_TAG}
            docker push registry.cn-heyuan.aliyuncs.com/21cloud/$serviceName:${CURRENT_TAG}   
        fi
    done
fi

echo "push to docker depository end (latest)."
'''
            }
        }
        stage('Deploy') {
            steps {
                script {
                    def portMappings = [
                            'gateway': 2100,
                            'provider-auth': 2101,
                            'provider-base': 2102,
                            'provider-quartz': 2103
                    ]
                    def mirrorContainerAddress = "registry.cn-heyuan.aliyuncs.com/21cloud/"
                    def servicesToDeploy = env.BUILD_SER_FIRST == "all" ? env.BUILD_ALL_SERVICES.split(' ') : env.BUILD_SER.split(' ')
                    //遍历服务 拉取镜像 启动镜像
                    servicesToDeploy.each { name ->
                        def imageNameWithTag  = "${mirrorContainerAddress}${name}:${env.BUILD_ENV}"
                        def javaOpts = getJavaOptions(name, name.replaceAll("provider-", ""), env.BUILD_ENV, imageNameWithTag)
                        def port = portMappings[name]
                        sh "docker pull ${imageNameWithTag}"
                        sh "docker run -d --name ${name}-${env.BUILD_ENV} -p${port}:${port} ${javaOpts} ${imageNameWithTag}"
                    }
                }
            }
        }
    }
    post {
        success {
            dingtalk(
                    robot: 'jenkins_build',
                    type: 'ACTION_CARD',
                    at: [],
                    atAll: false,
                    title: '你有新的消息，请注意查收',
                    text: [
                            "# [${JOB_NAME}](${JENKINS_URL}blue/organizations/jenkins/${JOB_NAME}/detail/${JOB_NAME}/${BUILD_NUMBER}/pipeline)",
                            "---",
                            "- 任务：[${BUILD_DISPLAY_NAME}](${BUILD_URL}/console)",
                            "- 状态：\u003cfont color\u003d#52c41a\u003e成功\u003c/font\u003e",
                            "- 构建环境：${BUILD_ENV}",
                            "- 构建服务：${BUILD_SER}",
                            "",
                            "微服务流水线构建成功，请测试人员进行测试。"
                    ],
                    singleTitle: '查看更多',
                    singleUrl: "${JOB_URL}"
            )
        }
        unstable {
            dingtalk(
                    robot: 'jenkins_build',
                    type: 'ACTION_CARD',
                    at: [],
                    atAll: false,
                    title: '你有新的消息，请注意查收',
                    text: [
                            "# [${JOB_NAME}](${JENKINS_URL}blue/organizations/jenkins/${JOB_NAME}/detail/${JOB_NAME}/${BUILD_NUMBER}/pipeline)",
                            "---",
                            "- 任务：[${BUILD_DISPLAY_NAME}](${BUILD_URL}/console)",
                            "- 状态：\u003cfont color\u003d#13c2c2\u003e不稳定\u003c/font\u003e",
                            "- 构建环境：${BUILD_ENV}",
                            "- 构建服务：${BUILD_SER}",
                            "",
                            "微服务流水线构建失败，请开发人员进行排查。"
                    ],
                    singleTitle: '查看更多',
                    singleUrl: "${JOB_URL}"
            )
        }
        aborted {
            dingtalk(
                    robot: 'jenkins_build',
                    type: 'ACTION_CARD',
                    at: [],
                    atAll: false,
                    title: '你有新的消息，请注意查收',
                    text: [
                            "# [${JOB_NAME}](${JENKINS_URL}blue/organizations/jenkins/${JOB_NAME}/detail/${JOB_NAME}/${BUILD_NUMBER}/pipeline)",
                            "---",
                            "- 任务：[${BUILD_DISPLAY_NAME}](${BUILD_URL}/console)",
                            "- 状态：<font color=#C0C0C0>取消</font>",
                            "- 构建环境：${BUILD_ENV}",
                            "- 构建服务：${BUILD_SER}",
                            "",
                            "微服务流水线构建被取消，请相关人员进行处理。"
                    ],
                    singleTitle: '查看更多',
                    singleUrl: "${JOB_URL}"
            )
        }
        failure {
            dingtalk(
                    robot: 'jenkins_build',
                    type: 'ACTION_CARD',
                    at: [],
                    atAll: false,
                    title: '你有新的消息，请注意查收',
                    text: [
                            "# [${JOB_NAME}](${JENKINS_URL}blue/organizations/jenkins/${JOB_NAME}/detail/${JOB_NAME}/${BUILD_NUMBER}/pipeline)",
                            "---",
                            "- 任务：[${BUILD_DISPLAY_NAME}](${BUILD_URL}/console)",
                            "- 状态：<font color=#FF0000>失败</font>",
                            "- 构建环境：${BUILD_ENV}",
                            "- 构建服务：${BUILD_SER}",
                            "",
                            "微服务流水线构建失败，请开发人员进行排查。"
                    ],
                    singleTitle: '查看更多',
                    singleUrl: "${JOB_URL}"
            )
        }
    }

}
