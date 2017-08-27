package cn.wittyneko.lovegame.view

import android.app.Dialog
import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import cn.wittyneko.live2d.app.LAppView

import cn.wittyneko.lovegame.R
import cn.wittyneko.lovegame.databinding.ActivityMainBinding
import cn.wittyneko.lovegame.databinding.DialogOptionBinding
import cn.wittyneko.lovegame.databinding.OptionItemBinding
import cn.wittyneko.lovegame.live2d.L2DAppDefine
import cn.wittyneko.lovegame.live2d.L2DAppManager
import cn.wittyneko.lovegame.model.DialogMsg
import cn.wittyneko.lovegame.model.storyline
import cn.wittyneko.lovegame.net.Posts
import cn.wittyneko.lovegame.utils.LogUtil
import cn.wittyneko.lovegame.utils.ThreadUtil
import rx.android.schedulers.AndroidSchedulers

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var l2dMgr: L2DAppManager
    lateinit var l2dView: LAppView

    val story = storyline()
    var curStory = story
    var storyResult = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        l2dMgr = L2DAppManager(this)
        l2dView = l2dMgr.createView(this)
        binding.live2dLa.addView(l2dView)
        l2dMgr.addModelPath(L2DAppDefine.MODEL_A, 0)
        l2dMgr.addModelPath(L2DAppDefine.MODEL_B, 1)
        //l2dMgr.addBgPath(L2DAppDefine.BACK_IMAGE_NAME)
        initEvent()
    }

    override fun onResume() {
        super.onResume()
        l2dMgr.onResume()
    }

    override fun onPause() {
        super.onPause()
        l2dMgr.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        l2dMgr.onDestroy()
    }

    fun initEvent() {
        Log.e("story", "size: ${story.list.size}, 2: ${story.list[2].list.size}")
        val list = story.list[2]
        Log.e("story", "size: ${list.list.size}, type: ${list.type}, msg:${list.msg}")
        binding.msgTv.setOnClickListener {
            if (l2dMgr.modelNum == 2) {
                start(curStory)
            }
        }
    }

    fun start(dialog: DialogMsg) {
        when (dialog.type) {
            0 -> {
                showMsg(dialog)
            }
            1 -> {
                showOption(dialog)
            }
            2 -> {
                if (dialog.index < dialog.list.size) {
                    start(dialog.list[dialog.index])
                    dialog.index += 1
                }
            }
        }
    }

    fun showMsg(msg: DialogMsg) {
        binding.msgTv.text = msg.msg
        if (msg.userType > 3) {
            storyResult.append("${msg.user}：${msg.msg}")
                    .append("\n")
        }
        if (msg.userType == 2) {
            sendResult()
        }
    }

    fun showOption(msg: DialogMsg) {
        val dialog = OptionDialog(this)
        dialog.setData(msg)
        dialog.show()
    }

    fun sendResult() {
        val posts = Posts()
        posts.user = "LU"
        posts.title = "LoveGame"
        posts.content = storyResult.toString()
        posts.version = "${getVersion()}"
        Log.e("content", "${posts.content}")
        posts.saveObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                    ThreadUtil.runOnUiThread({
                        binding.msgTv.text = "重新开始"
                        storyResult = StringBuilder()
                        curStory = storyline()
                    }, 1000)
                }, {
                    LogUtil.e(it.message)
                })
    }

    fun getBoy() = l2dMgr.getModel(0)

    fun getGirl() = l2dMgr.getModel(1)

    fun getVersion() = packageManager.getPackageInfo(packageName, 0).versionName

    inner class OptionDialog(context: Context) : Dialog(context) {

        var binding: DialogOptionBinding
        var adapter: Adapter

        init {
            binding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_option, null, false)
            setCancelable(false)
            window.setBackgroundDrawableResource(R.drawable.transparent)
            window.addFlags(Window.FEATURE_NO_TITLE)

            setContentView(binding.root)
            adapter = Adapter()
            binding.listLv.adapter = adapter
            binding.listLv.layoutManager = LinearLayoutManager(context)
        }

        fun setData(msg: DialogMsg) {
            if (msg.list.isNotEmpty()) {
                adapter.list = msg.list
                adapter.notifyDataSetChanged()
            }
        }

        inner class Adapter : RecyclerView.Adapter<Adapter.ViewHolder>() {
            var list: MutableList<DialogMsg> = mutableListOf()

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                holder.binding.optionBtn.text = list[position].msg
            }

            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
                val binding = DataBindingUtil.inflate<OptionItemBinding>(layoutInflater, R.layout.option_item, parent, false)
                return ViewHolder(binding.root, binding)
            }

            override fun getItemCount(): Int {
                return list.size
            }

            inner class ViewHolder(view: View, val binding: OptionItemBinding) : RecyclerView.ViewHolder(view) {
                init {
                    binding.optionBtn.setOnClickListener {
                        curStory = list[adapterPosition]
                        val msg = curStory
                        if (msg.userType > 3) {
                            storyResult.append("${msg.user}：${msg.msg}")
                                    .append("\n")
                        }
                        dismiss()
                    }
                }
            }
        }
    }
}
