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

    //已经开始构建的任务(往前查十个任务)
    for (i in 1..< 2) {
        def build = currentJob.getBuildByNumber(buildNumber - i)
        def actions = build.getActions(ParametersAction.class)
        if (actions && build) {
            ParametersAction action = actions.get(0)
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

    //未开始构建的任务(因为资源不足或并发限制等待执行的任务)
    //任务队列实例
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
    def currEnv = params.get("microEnv").toString()
    def currBranch = params.get("branch").toString()
    def currService = params.get("services").toString()
    return Objects.equals(currEnv, buildEnv) && Objects.equals(currBranch, buildBranch) && (Objects.equals(currService, buildMicroServices) || Objects.equals(currService, 'all'))
}

@NonCPS
def dockerLogin() {
    // withCredentials([usernamePassword(credentialsId: '772f1c22-d061-4e36-beb5-32749c2a903c', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
    sh "docker login --username=2020刘先生 --password=liu1069823632 registry.cn-heyuan.aliyuncs.com"
    // }
}

pipeline {
    //不限制执行者
    agent any
    options {
        timeout(time: 1, unit: 'HOURS')
    }
    parameters {
        choice choices: ['false', 'true'], description: '构建所有', name: 'buildAll'
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
                        git branch: "${branch}", credentialsId: 'd04587eb-e120-422b-9107-c8be728bc3ab', url: 'https://gitlab.com/21_Java/cloudscaffold.git'
                        sh 'chmod +x ./gradlew'
                    }
                }
                stage('Docker Login') {
                    steps {
                        dockerLogin()
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
                sh '''#!/bin/bash
# 该命令设置了脚本的错误处理，当任何命令返回非零退出状态时，脚本会立即退出。
set -e 
echo "push to docker depository start."
profile=$microEnv
services=$services
current=`date "+$microEnv-%Y%m%d%H%M"`
echo "microEnv1 : $microEnv --- microEnv2 : ${microEnv} --- profile1 : $profile --- profile2 : ${profile} --- services1 : $services --- services2 : ${services} --- current1 : $current --- current2 : ${current}"
if [[ $buildAll == "true" ]]; then
    echo "build all docker."
    # 运行Gradle命令，执行clean任务、docker任务，排除test任务，设置系统属性dockerVersion和profile。使用--no-daemon避免启动Gradle守护进程，--parallel允许并行构建。
    ./gradlew clean -PdockerVersion=${microEnv} -Pprofile=$profile docker -x test --no-daemon --parallel
    for name in ${services[@]}
    do
        echo "push $name start"
        docker push registry.cn-heyuan.aliyuncs.com/21cloud/$name:${microEnv}
        # 打回滚版本标签
        docker tag registry.cn-heyuan.aliyuncs.com/21cloud/$name:${microEnv} registry.cn-heyuan.aliyuncs.com/21cloud/$name:$current
        docker push registry.cn-heyuan.aliyuncs.com/21cloud/$name:$current
        echo "push $name end"
    done
else
    if [[ $services == "all" ]]; then
        ./gradlew clean -PdockerVersion=${microEnv} -Pprofile=$profile docker -x test --no-daemon --parallel
        for name in ${services[@]}
        do
            echo "push $name start"
            docker push registry.cn-heyuan.aliyuncs.com/21cloud/$name:${microEnv}
            docker tag registry.cn-heyuan.aliyuncs.com/21cloud/$name:${microEnv} registry.cn-heyuan.aliyuncs.com/21cloud/$name:$current
            docker push registry.cn-heyuan.aliyuncs.com/21cloud/$name:$current
            echo "push $name end"
        done
    elif [ $services = "gateway" ]; then
        # 运行子项目docker任务
        ./gradlew clean -PdockerVersion=${microEnv} -Pprofile=$profile :$services:docker -x test --no-daemon
        docker push registry.cn-heyuan.aliyuncs.com/21cloud/$services:${microEnv}
        docker tag registry.cn-heyuan.aliyuncs.com/21cloud/$services:${microEnv} registry.cn-heyuan.aliyuncs.com/21cloud/$services:$current
        docker push registry.cn-heyuan.aliyuncs.com/21cloud/$services:$current
    else
        ./gradlew clean -PdockerVersion=${microEnv} -Pprofile=$profile :provider:$services:docker -x test --no-daemon
        docker images | grep ${services}
        echo "推送命令：docker push registry.cn-heyuan.aliyuncs.com/21cloud/$services:${microEnv}"
        docker push registry.cn-heyuan.aliyuncs.com/21cloud/$services:${microEnv}
        docker tag registry.cn-heyuan.aliyuncs.com/21cloud/$services:${microEnv} registry.cn-heyuan.aliyuncs.com/21cloud/$services:$current
        docker push registry.cn-heyuan.aliyuncs.com/21cloud/$services:$current    
    fi
fi
echo "push to docker depository end (latest)."'''
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
                            "- 构建环境：${microEnv}",
                            "- 构建服务：${services}",
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
                            "- 构建环境：${microEnv}",
                            "- 构建服务：${services}",
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
                            "- 构建环境：${microEnv}",
                            "- 构建服务：${services}",
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
                            "- 构建环境：${microEnv}",
                            "- 构建服务：${services}",
                            "",
                            "微服务流水线构建失败，请开发人员进行排查。"
                    ],
                    singleTitle: '查看更多',
                    singleUrl: "${JOB_URL}"
            )
        }
    }

}