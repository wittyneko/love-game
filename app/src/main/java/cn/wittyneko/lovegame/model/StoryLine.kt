package cn.wittyneko.lovegame.model

/**
 * Created by wittyneko on 2017/8/27.
 */

val MSG_END = "the end"
val MSG_CONTINUED = "to be continued"

fun storyline() = buildStroyline {
    msg = "Start"
    dialog { userType = 0; msg = "【今天是个特别的日子】" }
    system { msg = "我喜欢你" }
    option {
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