package cn.wittyneko.lovegame.live2d;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

import javax.microedition.khronos.opengles.GL10;

import cn.wittyneko.live2d.app.LAppDefine;
import cn.wittyneko.live2d.app.LAppLive2DManager;
import cn.wittyneko.live2d.app.LAppModel;

/**
 * Live2d 管理器
 * Created by wittytutu on 17-3-22.
 */

public class L2DAppManager extends LAppLive2DManager {
    // 配置文件状态
    private String preferenceState;

    private AppModelListener.LoadListener mLoadListener; //模型载入监听
    private AppModelListener.UpdateListener mUpdateListener; //模型刷新监听

    SimpleDateFormat mFormat =new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒SSS毫秒");

    public L2DAppManager(Context context) {
        super(context);
    }

    @Override
    public void update(GL10 gl) {
        //Log.e("load update",  "-> " + mFormat.format(System.currentTimeMillis()) + ", " + System.currentTimeMillis());
        super.update(gl);
    }

    // 加载模型
    @Override
    public void loadModels(GL10 gl, String path, int position, int index) throws Throwable {

        L2DAppModel appModel = new L2DAppModel(position, index);
        appModel.setLoadListener(mLoadListener);
        appModel.setUpdateListener(mUpdateListener);
        //Log.e("load begin",  "-> " + mFormat.format(System.currentTimeMillis()) + ", " + System.currentTimeMillis());
        appModel.load(gl, path);
        //Log.e("load end",  "-> " + mFormat.format(System.currentTimeMillis()) + ", " + System.currentTimeMillis());
        appModel.feedIn();
        models.add(appModel);
        updatePreference();
    }

    @Override
    public boolean tapEvent(float x, float y) {
        if (LAppDefine.DEBUG_LOG) Log.d(TAG, "tapEvent view x:" + x + " y:" + y);

        for (int i = 0; i < models.size(); i++) {
            L2DAppModel model = (L2DAppModel) models.get(i);
            String hitAreaName = hitTest(model, x, y);
            if (!TextUtils.isEmpty(hitAreaName)) {
                hitAreaName = getAreaName(hitAreaName);
                String motionGroup = LAppDefine.EVENT_TAP + hitAreaName;

                if (LAppDefine.DEBUG_LOG)
                    Log.e(TAG, "Tap Event." + motionGroup);

                // 模型动作是否存在
                //int exist = models.get(i).getModelSetting().getMotionNum(motionGroup);
                //int exist = models.get(i).getModelSetting().getMotionNum(LAppDefine.MOTION_GROUP_INDEX);
                //if (exist != 0 && hasNextMotion(models.get(i).getMainMotionManager(), LAppDefine.PRIORITY_NORMAL)) {
                if (hasNextMotion(model.getMainMotionManager(), LAppDefine.PRIORITY_NORMAL)) {
                    model.startPreferenceMotionExpression(motionGroup, LAppDefine.PRIORITY_NORMAL);
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public void flickEvent(float x, float y) {
        if (LAppDefine.DEBUG_LOG) Log.d(TAG, "flick x:" + x + " y:" + y);

        for (int i = 0; i < models.size(); i++) {
            L2DAppModel model = (L2DAppModel) models.get(i);
            String hitAreaName = hitTest(model, x, y);
            if (!TextUtils.isEmpty(hitAreaName)) {
                hitAreaName = getAreaName(hitAreaName);
                String motionGroup = LAppDefine.EVENT_FLICK + hitAreaName;

                if (LAppDefine.DEBUG_LOG)
                    Log.e(TAG, "Flick Event." + motionGroup);

                // 模型动作是否存在
                //int exist = models.get(i).getModelSetting().getMotionNum(motionGroup);
                //int exist = models.get(i).getModelSetting().getMotionNum(LAppDefine.MOTION_GROUP_INDEX);
                //if (exist != 0 && hasNextMotion(models.get(i).getMainMotionManager(), LAppDefine.PRIORITY_NORMAL)) {
                if (hasNextMotion(model.getMainMotionManager(), LAppDefine.PRIORITY_NORMAL)) {
                    model.startPreferenceMotionExpression(motionGroup, LAppDefine.PRIORITY_NORMAL);
                    break;
                }
            }
        }

    }

    @Override
    public void longPress(float x, float y) {
        if (LAppDefine.DEBUG_LOG) Log.d(TAG, "longPress x:" + x + " y:" + y);

        for (int i = 0; i < models.size(); i++) {
            L2DAppModel model = (L2DAppModel) models.get(i);
            String hitAreaName = hitTest(model, x, y);
            if (!TextUtils.isEmpty(hitAreaName)) {
                hitAreaName = getAreaName(hitAreaName);
                String motionGroup = LAppDefine.EVENT_LONG_PRESS + hitAreaName;

                if (LAppDefine.DEBUG_LOG)
                    Log.e(TAG, "LongPress Event." + motionGroup);

                // TODO: 2017/8/25 长按事件
            }
        }

    }

    // 点击区域名称转换
    private String getAreaName(String modelArea) {
        String areaName = modelArea;
        if (true && (modelArea.endsWith("_l") || modelArea.endsWith("_r"))) {
            areaName = modelArea.substring(0, modelArea.length() - 2);
        }
        return areaName;
    }

    // 更新模型动作配置
    public void updatePreference() {

        for (LAppModel appModel : models) {
            L2DAppModel model = (L2DAppModel) appModel;
            try {
                InputStreamReader isr = null;
                //读取下载Json
                File file = new File(mContext.getFilesDir() + "/" + model.getModelNameDir() + ".json");
                if (file.exists()) {
                    //读取更新Json配置
                    isr = new InputStreamReader(new FileInputStream(file));
                }
                if (isr == null) {
                    file = new File(mContext.getFilesDir() + "/" + model.getModelNameDir());
                    if (file.exists()) {
                        isr = new InputStreamReader(new FileInputStream(file));
                    }
                }

                //字节流转字符流
                BufferedReader bfr = new BufferedReader(isr);
                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = bfr.readLine()) != null) {
                    stringBuilder.append(line);
                }
                //Log.e("json", "-> " + stringBuilder.toString());

                //将JSON数据转化为字符串
                //根据键名获取键值信息
                // TODO: 2017/8/25 解析配置文件
                //JsonObject jsonObject = GsonUtil.form(stringBuilder.toString(), JsonObject.class);
                //model.setPreferenceJson(jsonObject);
                //model.setPreferenceState(preferenceState);
                //MainActivity activity = (MainActivity) getActivity();
                //activity.live2DMgr.setPreferenceJson(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getPreferenceState() {
        return preferenceState;
    }

    // 更新模型状态配置
    public void setPreferenceState(String state) {
        this.preferenceState = state;
        for (LAppModel appModel : models) {
            L2DAppModel model = (L2DAppModel) appModel;
            model.setPreferenceState(state);
        }
    }

    public AppModelListener.LoadListener getLoadListener() {
        return mLoadListener;
    }

    public void setLoadListener(AppModelListener.LoadListener loadListener) {
        this.mLoadListener = loadListener;

        for (LAppModel appModel : models) {
            L2DAppModel model = (L2DAppModel) appModel;
            model.setLoadListener(mLoadListener);
        }
    }

    public AppModelListener.UpdateListener getUpdateListener() {
        return mUpdateListener;
    }

    public void setUpdateListener(AppModelListener.UpdateListener updateListener) {
        this.mUpdateListener = updateListener;

        for (LAppModel appModel : models) {
            L2DAppModel model = (L2DAppModel) appModel;
            model.setUpdateListener(mUpdateListener);
        }
    }
}
