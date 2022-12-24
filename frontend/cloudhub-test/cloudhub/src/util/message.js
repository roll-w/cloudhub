// 消息提示框的四种类型
let typeMap = {
    info: "info",
    warning: "warning",
    success: "success",
    danger: "danger"
}

let messageOption = {
    type: "info",
    closeTime: 400,
    center: false,
    showClose: false,
    content: "默认内容"
}

function Message(option) {
    this.init(option);
}

Message.prototype.init = function (option) {
    document.body.appendChild(this.create(option));
    this.setTop(document.querySelectorAll('.message'));
    if (option.closeTime > 0) {
        this.close(option.container, option.closeTime);
    }
    if (option.close) {
        option.close.onclick = (e) => {
            this.close(e.currentTarget.parentElement, 0);
        }
    }
}

Message.prototype.create = function (option) {
    console.log(option);
    if (!option.showClose && option.closeTime <= 0) {
        option.showClose = true;
        console.log('showClose');
    }
    let element = document.createElement('div');
    element.className = `message alert alert-${option.type}`;
    if (option.center) element.classList.add('alert-center');
    let closeBtn = document.createElement('i');
    closeBtn.className = 'btn btn-outline-secondary';
    closeBtn.innerHTML = '&times;';
    let contentElement = document.createElement('p');
    contentElement.innerHTML = option.content;
    if (closeBtn && option.showClose) element.appendChild(closeBtn);
    element.appendChild(contentElement);
    option.container = element;
    option.close = closeBtn;
    return element;
}

Message.prototype.close = function (messageElement, time) {
    setTimeout(() => {
        if (messageElement && messageElement.length) {
            for (let i = 0; i < messageElement.length; i++) {
                if (messageElement[i].parentElement) {
                    messageElement[i].parentElement.removeChild(messageElement[i]);
                }
            }
        } else if (messageElement) {
            if (messageElement.parentElement) {
                messageElement.parentElement.removeChild(messageElement);
            }
        }
        this.setTop(document.querySelectorAll('.message'));
    }, time * 10);
}

Message.prototype.setTop = function (messageElement) {
    if (!messageElement || !messageElement.length) return;
    const height = messageElement[0].offsetHeight;
    for (let i = 0; i < messageElement.length; i++) {
        messageElement[i].style.top = (25 * (i + 1) + height * i) + 'px';
    }
}

export function mes() {
    let $message;
    window['$message'] = $message = function (option) {
        let newMessageOption = null;
        if (typeof option === 'string') {
            newMessageOption = Object.assign(messageOption, {content: option});
        } else if (typeof option === 'object' && !!option) {
            newMessageOption = Object.assign(messageOption, option);
        }
        return new Message(newMessageOption);
    }
    for (let key in typeMap) {
        window['$message'][key] = function (option) {
            let newMessageOption = null;
            if (typeof option === 'string') {
                console.log(option)
                newMessageOption = Object.assign(messageOption, {content: option, type: typeMap[key]});
            } else if (typeof option === 'object' && !!option) {
                newMessageOption = Object.assign(JSON.parse(JSON.stringify(messageOption)), option, {type: typeMap[key]});
            }
            return new Message(newMessageOption);
        }
    }

}
