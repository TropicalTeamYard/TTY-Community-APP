package tty.community.pages.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.reflect.TypeToken
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.fragment_home.*
import tty.community.R
import tty.community.adapter.BlogListAdapter
import tty.community.adapter.TopicListAdapter
import tty.community.model.Blog
import tty.community.model.Blog.Outline
import tty.community.model.Shortcut
import tty.community.model.Topic
import tty.community.model.User
import tty.community.network.AsyncNetUtils
import tty.community.util.CONF
import tty.community.util.Message
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment(), BlogListAdapter.OnBlogItemClickListener, OnRefreshListener, OnLoadMoreListener,
    TopicListAdapter.OnTopicClickListener {
    override fun onTopicClick(v: View?, topic: Topic.Outline) {
        this.blogTopic = topic
        refreshList(topic)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        loadMore(blogTopic)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        refreshList(blogTopic)
    }

    override fun onBlogItemClick(v: View?, position: Int, blog: Outline) {
        Log.d(TAG, "pos: $position")
        when (v?.id) {
            R.id.blog_author_portrait -> {
                Log.d(TAG, "item: portrait")
            }

            R.id.blog_outline_more -> {
                Log.d(TAG, "item: more")
            }

            R.id.blog_outline_card -> {
                Log.d(TAG, "item: item")
            }

            R.id.blog_tag -> {
                Toast.makeText(context, blog.topic.introduction, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "item: tag")
            }

            R.id.blog_picture -> {
                Log.d(TAG, "item: picture")
            }

            else -> {
                Log.d(TAG, "item: unknown")
            }
        }
    }

    override fun onClick(p0: View?) {}

    private var blogTopic = Topic.Outline("", "ALL", "00000", "")
    private lateinit var blogListAdapter: BlogListAdapter
    private lateinit var topicListAdapter: TopicListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()

        home_refreshLayout.autoRefresh()
        home_refreshLayout.setOnRefreshListener(this)
        home_refreshLayout.setOnLoadMoreListener(this)

        updateTopicList()

    }

    override fun onResume() {
        super.onResume()
        updateTopicList()
    }

    private fun refreshList(topic: Topic.Outline) {
        Blog.initBlogList(Date(), 10, topic, object : AsyncNetUtils.Callback {
            override fun onFailure(msg: String): Int {
                return onBlogListFail(msg, UpdateMode.INIT)
            }

            override fun onResponse(result: String?): Int {
                val message: Message.MsgData<ArrayList<Outline>>? = Message.MsgData.parse(result, object : TypeToken<Message.MsgData<ArrayList<Outline>>>() {})
                return if (message != null) {
                    when (message.shortcut) {
                        Shortcut.OK -> onBlogListSuccess(message.data, UpdateMode.INIT)
                        else -> onBlogListFail("刷新失败，未知错误1", UpdateMode.INIT)
                    }
                } else {
                    onBlogListFail("刷新失败，数据异常2", UpdateMode.INIT)
                }
            }
        })
    }
    private fun loadMore(topic: Topic.Outline) {
        blogListAdapter.getLastBlogId()?.let { id ->
            Blog.loadMore(id, 10, topic, object : AsyncNetUtils.Callback {
                override fun onResponse(result: String?): Int {
                    val message: Message.MsgData<ArrayList<Outline>>? = Message.MsgData.parse(result, object : TypeToken<Message.MsgData<ArrayList<Outline>>>() {})
                    return if (message != null) {
                        when(message.shortcut) {
                            Shortcut.OK -> onBlogListSuccess(message.data, UpdateMode.ADD)
                            else -> onBlogListFail("加载失败，未知错误1", UpdateMode.ADD)
                        }
                    } else {
                        onBlogListFail("解析错误", UpdateMode.ADD)
                    }
                }

                override fun onFailure(msg: String): Int {
                    return onBlogListFail(msg, UpdateMode.ADD)
                }

            })
        }
    }

    fun onBlogListFail(msg: String, mode: UpdateMode): Int {
        Log.e(TAG, msg)
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        when(mode) {
            UpdateMode.ADD -> home_refreshLayout.finishLoadMore(false)
            UpdateMode.INIT -> home_refreshLayout.finishRefresh(false)
        }
        return 1
    }
    fun onBlogListSuccess(blogs: ArrayList<Outline>, mode: UpdateMode): Int {
        when(mode) {
            UpdateMode.ADD -> {
                blogListAdapter.add(blogs)
                home_refreshLayout.finishLoadMore(true)
            }
            UpdateMode.INIT -> {
                blogListAdapter.init(blogs)
                home_refreshLayout.finishRefresh(true)
            }
        }
        return 0
    }

    private fun updateTopicList() {
        context?.let { User.find(it)?.let { user ->
                AsyncNetUtils.post(CONF.API.topic.list, hashMapOf(Pair("id", user.id)), object : AsyncNetUtils.Callback {
                    override fun onResponse(result: String?): Int {
                        val message: Message.MsgData<ArrayList<Topic.Outline>>? = Message.MsgData.parse(result, object : TypeToken<Message.MsgData<ArrayList<Topic.Outline>>>(){})
                        return if (message != null) {
                            when(message.shortcut) {
                                Shortcut.OK -> onSuccess(message.data)
                                else -> onFail("shortcut异常")
                            }
                        } else {
                            onFail("解析异常")
                        }
                    }

                    override fun onFailure(msg: String): Int {
                        return onFail()
                    }

                    fun onSuccess(topics: ArrayList<Topic.Outline>): Int {
                        Log.d(TAG, "update topics success")
                        topicListAdapter.updateTopics(topics)
                        return 0
                    }

                    fun onFail(msg: String = "网络异常"): Int {
                        Log.e(TAG, msg)
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        return 1
                    }

                })
            }
        }
    }

    private fun setAdapter() {
        blogListAdapter = BlogListAdapter()
        val blogListLayoutManager = LinearLayoutManager(this.context)
        blogListLayoutManager.orientation = LinearLayoutManager.VERTICAL
        home_blog_list.adapter = blogListAdapter
        home_blog_list.layoutManager = blogListLayoutManager
        blogListAdapter.setOnBlogItemClickListener(this)

        topicListAdapter = TopicListAdapter()
        val topicListLayoutManager = LinearLayoutManager(this.context)
        topicListLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        home_topic_bar.adapter = topicListAdapter
        home_topic_bar.layoutManager = topicListLayoutManager
        topicListAdapter.setOnItemClickListener(this)
    }

    enum class UpdateMode {
        ADD, INIT
    }

    companion object {
        const val TAG = "HomeFragment"
    }

}
