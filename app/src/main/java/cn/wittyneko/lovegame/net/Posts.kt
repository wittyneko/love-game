package cn.wittyneko.lovegame.net

import cn.bmob.v3.BmobObject

/**
 * Created by wittyneko on 2017/8/25.
 */

class Posts : BmobObject() {
    var user: String? = null
    var title: String? = null
    var content: String? = null
    var version: String? = null
}
