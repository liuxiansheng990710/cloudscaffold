# -*- coding: utf-8 -*-
from flask import Flask, request, jsonify, Response
import requests
from dateutil import parser
import pytz  # 导入pytz处理时区转换
import json

app = Flask(__name__)

#监听webhook的路由
@app.route('/webhook', methods=['POST'])
def webhook_handler():
    data = request.json  # 获取Webhook发送的数据

    # 确保请求体不为空
    if not data:
        return jsonify({"status": "error", "message": "Empty request"}), 400
    #根据类型区分逻辑
    object_kind = data.get('object_kind')
    object_state = data.get('object_attributes', {}).get('state')
    title = data.get('object_attributes', {}).get('title')
    action = data.get('object_attributes',{}).get('action')

    #创建请求
    if object_kind == 'merge_request' and object_state == 'opened':
        #跳过自动合并发送创建MR
        if title == 'Auto Merge':
            return Response(json.dumps({"status": "success", "message": "跳过自动合并创建的合并请求"}, ensure_ascii=False), content_type='application/json; charset=utf-8')
        if action != 'open':
            return Response(json.dumps({"status": "success", "message": "跳过修改合并信息创建的合并请求"}, ensure_ascii=False), content_type='application/json; charset=utf-8')
        else:
            return handle_merge_request(data)
    #关闭请求（忽略）
    elif object_kind == 'merge_request' and object_state == 'closed':
        return Response(json.dumps({"status": "success", "message": "跳过关闭合并请求"}, ensure_ascii=False), content_type='application/json; charset=utf-8')
    #合并请求（忽略 使用push）
    elif object_kind == 'merge_request' and object_state == 'merged':
        return Response(json.dumps({"status": "success", "message": "跳过分支合并"}, ensure_ascii=False), content_type='application/json; charset=utf-8')
    elif object_kind == 'push':
        return handle_push(data)
    elif object_kind == 'note':
        return handle_comment(data)
    else:
        return jsonify({"status": "error", "message": f"Unsupported object kind: {object_kind}"}), 400

# 监听合并请求
def handle_merge_request(data):

    # 提取信息
    author_name = data.get('user', {}).get('name', 'Unknown')
    source_branch = data.get('object_attributes', {}).get('source_branch', 'Unknown')
    merge_time_str = data.get('object_attributes', {}).get('created_at', 'Unknown')
    target_branch = data.get('object_attributes', {}).get('target_branch', 'Unknown')
    title = data.get('object_attributes', {}).get('title', 'No Title')
    url = data.get('object_attributes', {}).get('url', 'No URL')
    project_name = data.get('project', {}).get('name', 'Unknown')
    assignee_names = [assignee.get('username', 'Unknown') for assignee in data.get('assignees', [])]
    reviewer_names = [reviewer.get('username', 'Unknown') for reviewer in data.get('reviewers', [])]

    # 使用逗号连接用户名列表，只包括非“Unknown”的用户名
    assignees_str = ', '.join([f"@{name}" for name in assignee_names if name != 'Unknown'])
    reviewers_str = ', '.join([f"@{name}" for name in reviewer_names if name != 'Unknown'])

    if assignees_str and reviewers_str:
        all_participants = f"{assignees_str}, {reviewers_str}"
    elif assignees_str:  # 如果只有assignees_str非空
        all_participants = assignees_str
    else:  # 否则，使用reviewers_str（可能为空）
        all_participants = reviewers_str

    # 处理时间字符串，从UTC转换为CST，并格式化输出
    if merge_time_str != 'Unknown':
        merge_time_utc = parser.parse(merge_time_str)
        merge_time_cst = merge_time_utc.astimezone(pytz.timezone('Asia/Shanghai'))
        merge_time = merge_time_cst.strftime('%Y-%m-%d %H:%M:%S,%f')[:-3]
    else:
        merge_time = 'Unknown'

    # 格式化消息
    markdown_message = f"""### 事件: 合并请求\n- **项目名称**: {project_name}\n- **时间**: {merge_time}\n- **姓名**: {author_name}\n- **源分支名**: {source_branch}\n- **目标分支名**: {target_branch}\n- **标题**: {title}\n- **审核人**:{all_participants}\n- **链接**: [点击查看]({url})"""

    # 构造发送到钉钉的消息体为Markdown类型
    ding_message = {
        "msgtype": "markdown",
        "markdown": {
            "title": "GitLab合并请求通知",
            "text": markdown_message
        }
    }

    ding_webhook_url = 'https://oapi.dingtalk.com/robot/send?access_token=4dcc65aabbd20231921992543528dcba10fb9b3bcfaae99288216f8b26c42c96'
    response = requests.post(ding_webhook_url, json=ding_message)

    return jsonify({"status": "success", "ding_response": response.text}), 200


# 监听分支合并
def handle_push(data):

    # 提取基本信息
    event_name = "分支合并" if data.get('object_kind') == "push" else "Unknown Event"
    ref = data.get('ref', 'Unknown').split('/')[-1]  # 仅获取分支名
    project_name = data.get('project', {}).get('name', 'Unknown')
    user_name = data.get('user_name', 'Unknown')
    commits = data.get('commits', [])

    markdown_messages = [
        f"### 事件: {event_name}",
        f"- **项目名称**: {project_name}",
        f"- **分支**: {ref}",
        f"- **姓名**: {user_name}",
    ]

    # 构造提交内容列表
    for i, commit in enumerate(commits, start=1):  # 使用enumerate来获取每个提交的索引（从1开始）
        title = commit.get('title', 'No Title')
        message = commit.get('message', 'No Message').split('\n')[0]  # 取第一行作为简介
        url = commit.get('url', 'No URL')

        # 构造每条提交信息的Markdown字符串
        commit_markdown = (
            f"- **提交内容{i}:**\n"
            f"   - **标题**: [{title}]({url})\n"
            f"   - **内容**: {message}"
        )
        markdown_messages.append(commit_markdown)

    markdown_message = "\n".join(markdown_messages)

    ding_message = {
        "msgtype": "markdown",
        "markdown": {
            "title": "GitLab分支合并通知",
            "text": markdown_message
        }
    }

    ding_webhook_url = 'https://oapi.dingtalk.com/robot/send?access_token=4dcc65aabbd20231921992543528dcba10fb9b3bcfaae99288216f8b26c42c96'
    response = requests.post(ding_webhook_url, json=ding_message)

    return jsonify({"status": "success", "ding_response": response.text}), 200

#监听评论
def handle_comment(data):
    # 提取基础信息
    event_name = "评论"
    comment_time_str = data.get('object_attributes', {}).get('created_at', 'Unknown')
    source_branch = data.get('merge_request', {}).get('source_branch', 'Unknown')
    author_name = data.get('user', {}).get('name', 'Unknown')
    note = data.get('object_attributes', {}).get('note', 'No Comment')
    url = data.get('object_attributes', {}).get('url', 'No URL')
    project_name = data.get('project', {}).get('name', 'Unknown')

    # 处理时间为中国标准时间
    if comment_time_str != 'Unknown':
        comment_time_utc = parser.parse(comment_time_str)
        comment_time_cst = comment_time_utc.astimezone(pytz.timezone('Asia/Shanghai'))
        comment_time = comment_time_cst.strftime('%Y-%m-%d %H:%M:%S,%f')[:-3]
    else:
        comment_time = 'Unknown'

    # 构造超链接的评论内容
    comment_content = f"[{note}]({url})"

    markdown_message = f"""### 事件: {event_name}\n- **项目名称**: {project_name}\n- **时间**: {comment_time}\n- **分支**: {source_branch}\n- **评论人**: {author_name}\n- **评论内容**: {comment_content}"""

    ding_message = {
        "msgtype": "markdown",
        "markdown": {
            "title": "GitLab评论通知",
            "text": markdown_message
        }
    }

    ding_webhook_url = 'https://oapi.dingtalk.com/robot/send?access_token=4dcc65aabbd20231921992543528dcba10fb9b3bcfaae99288216f8b26c42c96'
    response = requests.post(ding_webhook_url, json=ding_message)

    return jsonify({"status": "success", "ding_response": response.text}), 200

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)