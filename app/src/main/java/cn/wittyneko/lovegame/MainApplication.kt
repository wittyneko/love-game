package cn.wittyneko.lovegame

import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import cn.bmob.v3.Bmob

/**
 * Created by wittyneko on 2017/8/26.
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val appInfo = packageManager.getApplicationInfo(packageName,PackageManager.GET_META_DATA)
        val bmobId = appInfo.metaData.getString("BMOB_APPLICATION_ID")
        Bmob.initialize(this, bmobId)
    }
}