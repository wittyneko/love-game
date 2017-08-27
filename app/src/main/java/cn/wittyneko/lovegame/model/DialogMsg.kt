package cn.wittyneko.lovegame.model

/**
 * Created by wittyneko on 2017/8/26.
 */
class DialogMsg {
    var type: Int = 0 // 0:系统流程, 1:用户选项, 2:分支
    var userType: Int = 0 // 0:引导, 1:开始, 2:结束, 3:系统提示, 4:系统角色, 5: 用户角色
    var user: String = "Tourist"
    var msg: String = ""
    var list: MutableList<DialogMsg> = mutableListOf()
    var index = 0
}

fun DialogMsg.dialog(init: DialogMsg.() -> Unit): DialogMsg {
    val msg = DialogMsg()
    msg.init()
    list.add(msg)
    return msg
}

fun DialogMsg.end(init: DialogMsg.() -> Unit) = dialog(init).apply {
    type = 0
    userType = 2
}

fun DialogMsg.system(init: DialogMsg.() -> Unit) = dialog(init).apply {
    type = 0
    userType = 4
    user = "A"
}

fun DialogMsg.option(init: DialogMsg.() -> Unit) = dialog(init).apply {
    type = 1
    userType = 5
    user = "B"
}

fun DialogMsg.branch(init: DialogMsg.() -> Unit) = dialog(init).apply {
    type = 2
    userType = 5
    user = "B"
}

fun buildStroyline(init: DialogMsg.() -> Unit) = DialogMsg().apply(init).apply { type = 2 }