package cn.wittyneko.lovegame.live2d;

import android.util.Log;
import android.view.animation.AnimationUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.microedition.khronos.opengles.GL10;

import cn.wittyneko.live2d.app.LAppDefine;
import cn.wittyneko.live2d.app.LAppModel;
import cn.wittyneko.live2d.utils.SoundManager;
import jp.live2d.framework.L2DStandardID;
import jp.live2d.param.ParamDefFloat;

/**
 * 模型实例
 * Created by wittytutu on 17-3-22.
 */

public class L2DAppModel extends LAppModel {

    private int listPosition; // 第几个列表
    private int listIndex; // 第几个模型
    private AppModelListener.LoadListener mLoadListener; //模型载入监听
    private AppModelListener.UpdateListener mUpdateListener; //模型刷新监听

    protected String modelSourceDir; // 模型声音文件目录
    private JsonObject preferenceJson; //配置json
    private String preferenceState; // 配置状态

    // 嘴型列表
    private String[] mouthArray;
    private int mouthIndex;
    private long mouthStartTime;

    // 调整参数
    public ConcurrentHashMap<String, Float> customParam = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Float> mouthChartletParam = new ConcurrentHashMap<>(); //贴图嘴形

    public L2DAppModel() {
        super();
    }

    public L2DAppModel(int position, int index) {
        super();
        this.listPosition = position;
        this.listIndex = index;
    }

    @Override
    public void load(GL10 gl, String modelSettingPath) throws Exception {
        super.load(gl, modelSettingPath);

        //Sound 加载声音列表
        //String[] soundPaths = modelSetting.getSoundPaths();
        modelSourceDir = modelHomeDir + L2DAppDefine.SOURCES_DIR;
//        String[] soundPaths = SoundManager.getSoundList(modelSourceDir);
//        for (int i = 0; i < soundPaths.length; i++) {
//            String path = soundPaths[i];
//            //Log.e("sound", "-> " + path);
//            SoundManager.load(path);
//        }

        // 获取动作参数列表
        List<ParamDefFloat> paramList = getLive2DModel().getModelImpl().getParamDefSet().getParamDefFloatList();
        for (final ParamDefFloat param : paramList) {
            //Log.e(TAG + "param", "-> " + param.getParamID().toString() + ", " + param.getMinValue() + ", " + param.getMaxValue() + ", " + param.getDefaultValue());
            // 添加贴图嘴形到过滤列表
            if (param.getParamID().toString().startsWith(L2DAppStandardID.PARAM_MOUTH_CHARTLET)) {
                // 其它嘴型
                mouthChartletParam.put(param.getParamID().toString(), param.getDefaultValue());
            } else if (param.getParamID().toString().startsWith(L2DAppStandardID.PARAM_MOUTH_SIZE)) {
                // 嘴大小
                mouthChartletParam.put(param.getParamID().toString(), param.getDefaultValue());
            } else if (param.getParamID().toString().startsWith(L2DStandardID.PARAM_MOUTH_FORM)) {
                // 对话嘴形状
                mouthChartletParam.put(param.getParamID().toString(), 0.4f);
            }
        }

        if (mLoadListener != null) {
            mLoadListener.load(this);
        }
    }

    @Override
    public void update() {
        super.update();

        // 嘴型解析
        if (mouthArray != null && mouthIndex < mouthArray.length) {
            // 停止表情
//            if (!expressionManager.isFinished()) {
//                expressionManager.stopAllMotions();
//            }
            long timePassed = AnimationUtils.currentAnimationTimeMillis() - mouthStartTime;
            mouthIndex = (int) (timePassed * (60f / 1000f));
            //Log.e("vol", "-> " + mouthIndex + " , " + timePassed);

            if (mouthIndex < mouthArray.length) {
                resetMouth();
                // 播放张嘴大小
                //Log.e("vol", "-> " + mouthArray[mouthIndex]);
                float mouth = Float.valueOf(mouthArray[mouthIndex]);
                live2DModel.setParamFloat(L2DStandardID.PARAM_MOUTH_OPEN_Y, mouth);
            } else {
                // 嘴型解析结束
                //Log.e("expressions", "-> " + expressionManager.isFinished());
                if (mainMotionManager.getCurrentPriority() <= LAppDefine.PRIORITY_IDLE) {
                    startIdleRandomMotion();
                }
            }
        }

        if (mUpdateListener != null) {
            mUpdateListener.update(this);
        }

        // 调整自定义参数
        Iterator<String> iterator = customParam.keySet().iterator();
        while (iterator.hasNext()) {
            String name = iterator.next();
            float value = customParam.get(name);
            live2DModel.setParamFloat(name, value);
        }

        // 刷新显示
        live2DModel.update();
    }

    @Override
    public void resetMouth() {
        // 过滤贴图表情
        //live2DModel.setParamFloat(L2DAppStandardID.PARAM_MOUTH_CHARTLET, 0);
        Iterator<String> iterator = mouthChartletParam.keySet().iterator();
        while (iterator.hasNext()) {
            String name = iterator.next();
            float value = mouthChartletParam.get(name);
            live2DModel.setParamFloat(name, value);
            //live2DModel.setParamFloat(name, 0);
        }
    }

    @Override
    public void startIdleRandomMotion() {
        startPreferenceMotionExpression(L2DAppDefine.MOTION_GROUP_IDLE, L2DAppDefine.PRIORITY_IDLE);
    }

    /**
     * 随机当前状态配置文件 的 肢体+表情动作
     *
     * @param name     动作名称
     * @param priority 优先级
     */
    public void startPreferenceMotionExpression(String name, int priority) {
        startPreferenceMotionExpression(preferenceState, name, priority);
    }

    /**
     * 随机指定状态配置文件 的 肢体+表情动作
     *
     * @param state    状态
     * @param name     动作名称
     * @param priority 优先级
     */
    public void startPreferenceMotionExpression(String state, String name, int priority) {
        // 配置文件存在，并且当前状态有数据
        if (preferenceJson != null && state != null) {
            //Log.e(getClass().getSimpleName(), "preference -> " + state + ", " + name + ", " + priority);
            if (preferenceJson.has(state)) {
                JsonObject stateJson = preferenceJson.getAsJsonObject(state);
                startRandomMotionExpression(stateJson, state, name, priority);
            } else if (!L2DAppDefine.STATE_DEFAULT.equals(state)) {
                // 无当前状态，读取默认状态
                startPreferenceMotionExpression(L2DAppDefine.STATE_DEFAULT, name, priority);
            }
        }
    }

    /**
     * 随机肢体+表情动作（有状态：登录、表白、分手）
     *
     * @param stateJson 状态配置信息
     * @param state     状态
     * @param name      动作名称
     * @param priority  优先级
     */
    public void startRandomMotionExpression(JsonObject stateJson, String state, String name, int priority) {
        // 动作存在
        if (stateJson.has(name) && stateJson.get(name).isJsonArray()) {
            // 动作配置信息列表
            JsonArray array = stateJson.get(name).getAsJsonArray();
            // 获取随机组合
            int no = (int) (Math.random() * array.size());
            JsonObject action = array.get(no).getAsJsonObject();
            startMotionExpression(action, name, priority);
        } else if (!L2DAppDefine.STATE_DEFAULT.equals(state)) {
            // 当前状态无动作，调用默认状态动作
            startPreferenceMotionExpression(L2DAppDefine.STATE_DEFAULT, name, priority);
        }
    }

    /**
     * 执行动作 + 表情( 无状态：活动)
     *
     * @param action   动作配置信息
     * @param name     动作名称
     * @param priority 优先级
     */
    public void startMotionExpression(JsonObject action, String name, int priority) {

        // 肢体
        if (action.has("motion") && action.get("motion").isJsonObject()) {
            int motion = action.get("motion").getAsJsonObject().get("index").getAsInt();
            //startMotion(name, motion, priority);
            // 部位动作存在调用，否则调用统一动作
            if (getModelSetting().getMotionNum(name) != 0) {
                startMotion(name, motion, priority);
            } else {
                // 调用统一动作
                startMotion(L2DAppDefine.MOTION_GROUP_INDEX, motion, priority);
            }
        }

        // 表情
        if (action.has("expression") && action.get("expression").isJsonObject()) {
            int expression = action.get("expression").getAsJsonObject().get("index").getAsInt();
            setExpression(expression);
        }

        //  对话消息
        if (action.has("buildStroyline") && action.get("buildStroyline").isJsonArray()) {
            JsonArray jsonArray = action.get("buildStroyline").getAsJsonArray();
            int size = jsonArray.size();

            // 解析播放声音文件
            for (int i = 0; i < size; i++) {

                // 存在音频
                boolean hasAudio = jsonArray.get(i).getAsJsonObject().has("audio");
                if (hasAudio) {
                    // 计算播放时间
                    float delay = 0f;
                    if (i == 0) {
                        delay = 0f;
                    } else {
                        boolean hasDelay = jsonArray.get(i - 1).getAsJsonObject().has("delay");
                        if (hasDelay) {
                            delay = jsonArray.get(i - 1).getAsJsonObject().get("delay").getAsFloat();
                        } else {
                            delay = 0f;
                        }
                    }

                    // 播放音频
                    final String audio = jsonArray.get(i).getAsJsonObject().get("audio").getAsString();
                    final String audioPath = modelSourceDir + "/" + audio;
                    final String volPath = audioPath + ".vol";
                    Log.e("audio", "-> " + audioPath);
                    Log.e("vol", "-> " + volPath);
                    SoundManager.play(audioPath);
                    loadMouthSync(volPath);
                }
            }

            // mini弹窗
            // 空闲动作、无对话，则不发送弹窗消息
            // 方法不一定在主线程，需要注意线程切换
            if (!L2DAppDefine.MOTION_GROUP_IDLE.equals(name) && jsonArray != null && jsonArray.size() != 0) {
                // TODO: 2017/8/25 对话消息
            }
            //Log.e("live2d buildStroyline", "-> " + GsonUtil.toJson(action.get("buildStroyline")));
        }

        //  数值变动
        if (action.has("value") && action.get("value").isJsonObject()) {
            int min = action.get("value").getAsJsonObject().get("min").getAsInt();
            int max = action.get("value").getAsJsonObject().get("max").getAsInt();
            int len = max - min;
            int value = (int) (Math.random() * len) + min;
            // TODO: 2017/8/25 数值变得
        }
    }

    // 添加默认动作和表情
    private void addDefaultMotionExpression(JsonObject preferenceJson) {
//        for (int i = 1; i <= 10; i++) {
//            String state = String.valueOf(i);
//            addMotionExpression(preferenceJson, state, L2DAppDefine.MOTION_GROUP_IDLE, GsonUtil.form(L2DAppDefine.MOTION_IDLE, JsonArray.class));
//        }
    }

    /**
     * 添加动作和表情
     *
     * @param preferenceJson 配置文件
     * @param state          状态
     * @param name           动作名称
     * @param action         动作列表
     */
    private void addMotionExpression(JsonObject preferenceJson, String state, String name, JsonElement action) {

        JsonObject stateJson = new JsonObject();
        if (preferenceJson.has(state)) {
            stateJson = preferenceJson.get(state).getAsJsonObject();
        } else {
            preferenceJson.add(state, stateJson);
        }
        // 动作不存在、或为空列表
        if (!stateJson.has(name)
                || (stateJson.get(name).isJsonArray() && stateJson.getAsJsonArray(name).size() == 0)) {
            stateJson.add(name, action);
            //Log.e("state",  state + " -> " + GsonUtil.toJson(stateJson));
        }
    }

    public JsonObject getPreferenceJson() {
        return preferenceJson;
    }

    // 更新模型动作配置
    public void setPreferenceJson(JsonObject preferenceJson) {
        this.preferenceJson = preferenceJson;
        addDefaultMotionExpression(this.preferenceJson);
    }

    public String getPreferenceState() {
        return preferenceState;
    }

    // 更新状态
    public void setPreferenceState(String preferenceState) {
        //preferenceState = "1";
        this.preferenceState = preferenceState;
    }

    /**
     * 加载嘴型文件
     *
     * @param volPath
     */
    public void loadMouthSync(String volPath) {
        mouthIndex = 0;
        mouthStartTime = AnimationUtils.currentAnimationTimeMillis();
        mouthArray = SoundManager.loadMouthOpen(volPath);
    }

    public int getListPosition() {
        return listPosition;
    }

    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }

    public int getListIndex() {
        return listIndex;
    }

    public void setListIndex(int listIndex) {
        this.listIndex = listIndex;
    }

    public AppModelListener.LoadListener getLoadListener() {
        return mLoadListener;
    }

    public void setLoadListener(AppModelListener.LoadListener loadListener) {
        this.mLoadListener = loadListener;
    }

    public AppModelListener.UpdateListener getUpdateListener() {
        return mUpdateListener;
    }

    public void setUpdateListener(AppModelListener.UpdateListener updateListener) {
        this.mUpdateListener = updateListener;
    }
}
