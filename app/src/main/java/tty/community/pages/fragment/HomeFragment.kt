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
import tty.community.model.Blog
import tty.community.model.Blog.Outline
import tty.community.model.Shortcut
import tty.community.network.AsyncNetUtils
import tty.community.util.Message
import java.util.*


class HomeFragment : Fragment(), BlogListAdapter.OnItemClickListener, OnRefreshListener, OnLoadMoreListener {

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        loadMore(blogTag)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        refresh(blogTag)
    }

    override fun onItemClick(v: View?, position: Int, blog: Outline) {
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

    override fun onClick(p0: View?) {

    }

    private var blogTag = ""
    private lateinit var blogListAdapter: BlogListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()

        home_refreshLayout.autoRefresh()

        home_refreshLayout.setOnRefreshListener(this)
        home_refreshLayout.setOnLoadMoreListener(this)
    }

    private fun refresh(tag: String = "") {
        Blog.initBlogList(Date(), 10, tag, object : AsyncNetUtils.Callback {
            override fun onFailure(msg: String): Int {
                return onFail(msg, UpdateMode.INIT)
            }

            override fun onResponse(result: String?): Int {
                val message: Message.MsgData<ArrayList<Outline>>? = Message.MsgData.parse(result, object : TypeToken<Message.MsgData<ArrayList<Outline>>>() {})
                return if (message != null) {
                    when (message.shortcut) {
                        Shortcut.OK -> onSuccess(message.data, UpdateMode.INIT)
                        else -> onFail("刷新失败，未知错误1", UpdateMode.INIT)
                    }
                } else {
                    onFail("刷新失败，数据异常2", UpdateMode.INIT)
                }
            }
        })
    }

    private fun loadMore(tag: String = "") {
        blogListAdapter.getLastBlogId()?.let { id ->
            Blog.loadMore(id, 10, tag, object : AsyncNetUtils.Callback {
                override fun onResponse(result: String?): Int {
                    val message: Message.MsgData<ArrayList<Outline>>? = Message.MsgData.parse(result, object : TypeToken<Message.MsgData<ArrayList<Outline>>>() {})
                    return if (message != null) {
                        when(message.shortcut) {
                            Shortcut.OK -> onSuccess(message.data, UpdateMode.ADD)
                            else -> onFail("加载失败，未知错误1", UpdateMode.ADD)
                        }
                    } else {
                        onFail("解析错误", UpdateMode.ADD)
                    }
                }

                override fun onFailure(msg: String): Int {
                    return onFail(msg, UpdateMode.ADD)
                }

            })
        }
    }

    fun onFail(msg: String, mode: UpdateMode): Int {
        Log.e(TAG, msg)
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        when(mode) {
            UpdateMode.ADD -> home_refreshLayout.finishLoadMore(false)
            UpdateMode.INIT -> home_refreshLayout.finishRefresh(false)
        }
        return 1
    }

    fun onSuccess(blogs: ArrayList<Outline>, mode: UpdateMode): Int {
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

    private fun setAdapter() {
        blogListAdapter = BlogListAdapter()
        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        home_blog_list.adapter = blogListAdapter
        home_blog_list.layoutManager = layoutManager
        blogListAdapter.setOnItemClickListener(this)
    }

    enum class UpdateMode {
        ADD, INIT
    }

    companion object {
        const val TAG = "HomeFragment"
    }

}
