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

val MSG_END = "the end"
val MSG_CONTINUED = "to be continued"

fun storyline() = buildStroyline {
    msg = "Start"
    dialog { userType = 0; msg = "【今天是个特别的日子】" }
    system { msg = "我喜欢你" }
    option {
        msg = "选项"
        branch {
            msg = "你是好人..."
            system { msg = "谢谢你的拒绝" }
            end { msg = MSG_END }
        }

        branch {
            msg = "让我考虑考虑"
            system { msg = "嗯" }
            system { msg = "呃，那个有个礼物给你🎁" }
            dialog { msg = "【项链】" }
            option {
                branch {
                    msg = "太贵重了我不能收"
                    end { msg = MSG_CONTINUED }
                }

                branch {
                    msg = "我很喜欢谢谢"
                    end { msg = MSG_CONTINUED }
                }
            }
        }

        branch {
            msg = "我也喜欢你"
            system { msg = "呃，那个有个礼物给你🎁" }
            dialog { msg = "【项链】" }
            option {
                branch {
                    msg = "太贵重了我不能收"
                    end { msg = MSG_CONTINUED }
                }

                branch {
                    msg = "我很喜欢谢谢"
                    end { msg = MSG_CONTINUED }
                }

                branch {
                    msg = "我很喜欢帮我带上"
                    end { msg = MSG_CONTINUED }
                }
            }
        }
    }
}