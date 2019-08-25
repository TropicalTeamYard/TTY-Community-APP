package tty.community.pages.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_create_blog.*
import tty.community.R
import tty.community.adapter.CreateBlogFragmentAdapter
import tty.community.model.*
import tty.community.network.AsyncNetUtils
import tty.community.pages.fragment.ChooseTopicFragment
import tty.community.util.CONF
import tty.community.util.Message

class CreateBlogActivity : AppCompatActivity(), View.OnClickListener, ChooseTopicFragment.OnTopicChangeListener {
    override fun onTopicChange(topic: Topic.Outline) {
        this.topic = topic
        create_blog_choose_topic.text = topic.name
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_submit -> submit()
            R.id.create_blog_choose_topic -> ChooseTopicFragment().show(supportFragmentManager, "CTF")
        }
    }

    override var topic = Topic.Outline("000000", "ALL", "000000", "TTY Community")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_blog)

        var mode = "text"
        if (intent != null && intent.hasExtra("mode")){
            Log.d(TAG, "mode=${intent.getStringExtra("mode")}")
            mode = intent.getStringExtra("mode")
        }

        setAdapter(mode)
        btn_submit.setOnClickListener(this)
        create_blog_choose_topic.setOnClickListener(this)
    }

    private fun setAdapter(mode:String) {
        val adapter = CreateBlogFragmentAdapter(supportFragmentManager)
        create_blog_viewPager.adapter = adapter
        create_blog_viewPager.currentItem = when(mode){
            "richText" -> {
                label_edit_type.text = "高级"
                1
            }
            "markdown" -> {
                label_edit_type.text = "Markdown"
                2
            }
            else -> {
                label_edit_type.text = "普通"
                0
            }
        }
    }

    private fun submit(){
        when(create_blog_viewPager.currentItem) {
             0 -> {

                val user = User.find(this)

                if (user != null){
                    val blogData = ((create_blog_viewPager.adapter as CreateBlogFragmentAdapter).getItem(create_blog_viewPager.currentItem) as BlogData.IGetBlogData).getBlogData()
                    blogData.title = edit_title.text.toString()
                    if (blogData.title.isEmpty()){
                        blogData.title = "####nickname####的动态"
                    }

                    if (blogData.content.isEmpty()) {
                        if (blogData.pics.isEmpty()) {
                            Toast.makeText(this, "图片或内容必须至少存在一项", Toast.LENGTH_SHORT).show()
                            return
                        } else {
                            blogData.content = "分享图片"
                        }
                    }
                    Toast.makeText(this, "上传中...", Toast.LENGTH_SHORT).show()
                    val json = CONF.gson.toJson(blogData.introduction, object : TypeToken<BlogData.Introduction>(){}.type)
                    Log.d(TAG, json)

                    btn_submit.isClickable = false

                    // TODO 后台service上传
                    AsyncNetUtils.postMultipleForm(CONF.API.blog.create, Params.createBlog(user, blogData.title,
                        when(create_blog_viewPager.currentItem){
                            0-> Blog.Companion.BlogType.Short
                            else-> Blog.Companion.BlogType.Pro
                        }, CONF.gson.toJson(blogData.introduction, object : TypeToken<BlogData.Introduction>(){}.type), blogData.content, topic), blogData.pics, object : AsyncNetUtils.Callback {
                        fun onFail(msg: String): Int {
                            Log.e(TAG, msg)
                            //TODO 备份编辑项目
                            Toast.makeText(this@CreateBlogActivity, msg, Toast.LENGTH_SHORT).show()
                            btn_submit.isClickable = true
                            return 1
                        }

                        fun onSuccess(): Int {
                            Toast.makeText(this@CreateBlogActivity, "上传成功", Toast.LENGTH_SHORT).show()
                            finish()
                            return 0
                        }

                        override fun onFailure(msg: String): Int {
                            return onFail(msg)
                        }

                        override fun onResponse(result: String?): Int {
                            val message: Message.MsgData<Blog.Outline>? = Message.MsgData.parse(result, object : TypeToken<Message.MsgData<Blog.Outline>>(){})
                            return if (message != null) {
                                when (message.shortcut) {
                                    Shortcut.OK -> onSuccess()
                                    Shortcut.TE -> onFail("账号信息已过期，请重新登陆")
                                    else -> onFail("shortcut异常")
                                }
                            } else {
                                onFail("解析异常")
                            }
                        }
                    })
                } else {
                    Toast.makeText(this, "您还未登录，请先登录", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        private const val TAG = "CreateBlogActivity"
    }
}
