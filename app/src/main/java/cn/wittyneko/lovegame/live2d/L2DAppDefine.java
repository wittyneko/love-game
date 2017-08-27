package cn.wittyneko.lovegame.live2d;

import cn.wittyneko.live2d.app.LAppDefine;

/**
 * 模型配置
 * Created by wittytutu on 17-3-22.
 */

public class L2DAppDefine extends LAppDefine {

    //================================================//

    //模型 文件配置
    public static final String MODEL_A = "live2d/KAEDE/A.model.json"; // 2.0
    public static final String MODEL_B = "live2d/RENK/renko_nn.model.json"; // 2.0
    // 背景图片
    public static final String BACK_IMAGE_NAME = "image/bg_img.jpg";
    // 声音文件夹路径
    public static final String SOURCES_DIR = "sources"; // ==> live2d/{name}/sources


    //===================================================//

    // 动作组 key
    public static final String MOTION_GROUP_SING_ON = "sign_on"; // 签到
    public static final String MOTION_GROUP_BURDEN = "burden"; // 表白
    public static final String MOTION_GROUP_UNBURDEN = "unburden"; // 分手
    public static final String MOTION_GROUP_SOUND_ON = "sound_on"; // 声音开
    public static final String MOTION_GROUP_SOUND_OFF = "sound_off"; // 声音关

    // 可操作区域
    public static final String HIT_AREA_HEAD = "head"; // 头
    public static final String HIT_AREA_FACE = "face"; // 脸
    public static final String HIT_AREA_BODY = "body"; //身体
    public static final String HIT_AREA_HAND = "hand"; //手
    public static final String HIT_AREA_LEG = "leg"; //腿
    public static final String HIT_AREA_SKIRT = "skirt"; //裙

    // 状态
    public static final String STATE_DEFAULT = "default"; //默认状态


    //===============================================//

    //闲置动作和表情
//    public static final String MOTION_IDLE = "[{\"motion\":{\"index\":4},\"expression\":{\"index\":0}}" +
//            ",{\"motion\":{\"index\":3},\"expression\":{\"index\":0}}" +
//            "]";
    public static final String MOTION_IDLE = "[{\"motion\":{\"index\":0},\"expression\":{\"index\":0}}" +
            ",{\"motion\":{\"index\":1},\"expression\":{\"index\":0}}" +
            ",{\"motion\":{\"index\":2},\"expression\":{\"index\":0}}" +
            ",{\"motion\":{\"index\":3},\"expression\":{\"index\":0}}" +
            ",{\"motion\":{\"index\":4},\"expression\":{\"index\":0}}" +
            ",{\"motion\":{\"index\":5},\"expression\":{\"index\":0}}" +
            ",{\"motion\":{\"index\":6},\"expression\":{\"index\":0}}" +
            ",{\"motion\":{\"index\":7},\"expression\":{\"index\":0}}" +
            ",{\"motion\":{\"index\":8},\"expression\":{\"index\":0}}" +
            ",{\"motion\":{\"index\":9},\"expression\":{\"index\":0}}" +
            ",{\"motion\":{\"index\":10},\"expression\":{\"index\":0}}" +
            "]";
    // 声音开
    public static final String MOTION_SOUND_ON = "[" +
            "{\"motion\":{\"index\":1}," +
            "\"expression\":{\"index\":1}," +
            "\"buildStroyline\":[{\"content\":\"哼，我都不想说话了。\",\"delay\":2,\"audio\":\"sound_on/body-led-skirt-tap-01.mp3\",\"type\":0,\"name\":\"PPD\"}]}" +
            "]";
    // 声音关
    public static final String MOTION_SOUND_OFF = "[{" +
            "\"motion\":{\"index\":1}," +
            "\"expression\":{\"index\":1}," +
            "\"buildStroyline\":[{\"content\":\"那好吧，我不出声就是了。。\",\"delay\":2,\"type\":0,\"name\":\"PPD\"}]}" +
            "]";
}
