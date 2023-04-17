const files = [
    {
        id: 1,
        name: "测试文件.txt",
        type: "file",
        time: "2023/03/22 14:12",
        tags: [{
            name: "类型",
            value: "其他"
        }]
    },
    {
        id: 2,
        name: "项目文档.pdf",
        type: "file",
        time: "2023/03/23 15:23",
        tags: [{
            name: "类型",
            value: "其他"
        }]
    },
    {
        id: 3,
        name: "测试文件夹",
        type: "folder",
        time: "2023/03/22 14:11",
        tags: [{
            name: "类型",
            value: "文件夹"
        }]
    },
    {
        id: 4,
        name: "长沙米拓信息技术有限公司河南天一航天科技有限公司民事一审民事判决书.doc",
        type: "file",
        time: "2023/04/02 12:46",
        tags: [{
            name: "类型",
            value: "民事诉讼"
        }, {
            name: "审理阶段",
            value: "一审"
        }, {
            name: "文书类型",
            value: "判决书"
        }]
    },
    {
        id: 5,
        name: "河南省优悠商贸有限公司河南福汇泽置业有限公司等民事二审民事判决书.doc",
        type: "file",
        time: "2023/04/02 12:47",
        tags: [{
            name: "类型",
            value: "民事诉讼"
        }, {
            name: "审理阶段",
            value: "二审"
        }, {
            name: "文书类型",
            value: "判决书"
        }]
    },
    {
        id: 6,
        name:
            "武汉富兴通达电子商务有限公司句容利威尔电器有限公司侵害实用新型专利权纠纷民事二审民事判决书.doc",
        type: "file",
        time: "2023/04/12 10:48",
        tags: [{
            name: "类型",
            value: "民事诉讼"
        }, {
            name: "审理阶段",
            value: "二审"
        }, {
            name: "文书类型",
            value: "判决书"
        }]
    },
    {
        id: 7,
        name: "张波故意伤害二审刑事裁定书.doc",
        type: "file",
        time: "2023/04/12 10:50",
        tags: [{
            name: "类型",
            value: "刑事诉讼"
        }, {
            name: "审理阶段",
            value: "二审"
        }, {
            name: "文书类型",
            value: "裁定书"
        }]
    },
    {
        id: 8,
        name: "中华人民共和国国家知识产权局原田工业株式会社等专利行政管理(专利)行政二审行政判决书.doc",
        type: "file",
        time: "2023/04/12 10:50",
        tags: [{
            name: "类型",
            value: "行政诉讼"
        }, {
            name: "审理阶段",
            value: "二审"
        }, {
            name: "文书类型",
            value: "判决书"
        }]
    }
]

export const requestFiles = () => {
    return files
}

export const requestFile = (id) => {
    const idInNum = parseInt(id, 10)

    for (let file of files) {
        if (file.id === idInNum) {
            return file
        }
    }
    return {}
}