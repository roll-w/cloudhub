
const userRules = {
    username: [
        {
            required: true,
            message: "请输入用户名",
            trigger: ["input"]
        },
        {
            min: 3,
            max: 20,
            message: "用户名长度在3-20之间",
            trigger: ['input', 'blur']
        },
        {
            pattern: /^[a-zA-Z_\-][\w\-]{3,20}$/,
            message: "用户名只能包含字母、数字、下划线和横线，且不能以数字开头",
            trigger: ['input', 'blur']
        }
    ],
    email: [
        {
            required: true,
            message: "请输入邮箱",
            trigger: ["input", "blur"]
        },
        {
            type: "email",
            message: "请输入正确的邮箱",
            trigger: ["input", "blur"]
        }
    ],
    password: [
        {
            required: true,
            message: "请输入密码",
            trigger: ["input"]
        },
        {
            min: 4, max: 20,
            message: '密码长度在 4 到 20 个字符',
            trigger: ["input", "blur"]
        },
        {
            pattern: /^[a-zA-Z0-9!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]{4,20}$/,
            message: '密码只能包含字母、数字和特殊字符，长度在 4 到 20 个字符',
            trigger: ["input", "blur"]
        }
    ],
    nickname: [
        {
            min: 1,
            max: 20,
            message: "昵称长度在1-20个字符之间",
            trigger: ["blur", "input"]
        },
    ],
    role: [
        {
            required: true,
            message: "请选择角色",
        }
    ],
}

export const useUserRules = () => userRules

export const useUserRulesOf = (keys) => {
    const rules = {}
    keys.forEach(key => {
        rules[key] = userRules[key]
    })
    return rules
}
