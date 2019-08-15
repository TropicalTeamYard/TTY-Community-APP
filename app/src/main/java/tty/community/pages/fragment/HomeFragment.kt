package tty.community.pages.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.fragment_home.*
import tty.community.R
import tty.community.adapter.BlogListAdapter
import tty.community.model.Shortcut
import tty.community.model.blog.Blog.Outline
import tty.community.model.blog.Blog.Outline.Companion.initBlogList
import tty.community.network.AsyncNetUtils
import tty.community.pages.activity.CreateBlogActivity
import tty.community.values.Const
import java.util.*
import kotlin.collections.ArrayList


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
        fab_add_blog.setOnClickListener {
            startActivity(Intent(this.context, CreateBlogActivity::class.java))
        }

        setAdapter()

        home_refreshLayout.autoRefresh()

        home_refreshLayout.setOnRefreshListener(this)
        home_refreshLayout.setOnLoadMoreListener(this)
    }

    private fun refresh(tag: String = "") {
        initBlogList(Date(), 10, tag, object : AsyncNetUtils.Callback {

            override fun onFailure(msg: String) {
                onFail(msg, UpdateMode.INIT)
            }

            override fun onResponse(result: String?) {
                result?.let {
                    Log.d(TAG, it)
                    val element = JsonParser().parse(it)
                    if (element.isJsonObject) {
                        val obj = element.asJsonObject
                        when (Shortcut.parse(obj["shortcut"].asString)) {
                            Shortcut.OK -> {
                                val list = obj["data"].asJsonArray
                                if (list != null) {
                                    val blogs = getBlogs(list)
                                    onSuccess(blogs, UpdateMode.INIT)
                                }
                            }

                            else -> {
                                onFail("刷新失败，未知错误1", UpdateMode.INIT)
                            }
                        }
                    }

                    return
                }

                onFail("刷新失败，网络异常2", UpdateMode.INIT)
            }

        })
    }

    private fun loadMore(tag: String = "") {
        blogListAdapter.getLastBlogId()?.let { id ->
            Outline.loadMore(id, 10, tag, object : AsyncNetUtils.Callback {
                override fun onResponse(result: String?) {
                    result?.let { it ->
                        Log.d(TAG, it)
                        val element = JsonParser().parse(it)
                        if (element.isJsonObject) {
                            val obj = element.asJsonObject
                            when (Shortcut.parse(obj["shortcut"].asString)) {
                                Shortcut.OK -> {
                                    val list = obj["data"].asJsonArray
                                    if (list != null) {
                                        val blogs = getBlogs(list)
                                        onSuccess(blogs, UpdateMode.ADD)
                                    }
                                }

                                else -> {
                                    onFail("加载失败，未知错误1", UpdateMode.ADD)
                                }
                            }
                        }

                        return
                    }

                    onFail("加载失败，网络异常2", UpdateMode.ADD)
                }


                override fun onFailure(msg: String) {
                    onFail(msg, UpdateMode.ADD)
                }
            })

            return
        }

        onFail("网络异常3", UpdateMode.ADD)
    }

    fun onFail(msg: String, mode: UpdateMode) {
        Log.e(TAG, msg)
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        when(mode) {
            UpdateMode.ADD -> home_refreshLayout.finishLoadMore(false)
            UpdateMode.INIT -> home_refreshLayout.finishRefresh(false)
        }

    }

    fun onSuccess(blogs: ArrayList<Outline>, mode: UpdateMode) {
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


    }

    private fun getBlogs(list: JsonArray): ArrayList<Outline> {
        val blogs = ArrayList<Outline>()
        for (i in 0 until list.size()) {
            val item = list[i].asJsonObject
            val author = item["author"].asString
            val nickname = item["nickname"].asString
            val blogId = item["blogId"].asString
            val type = item["type"].asString
            val title = item["title"].asString
            val introduction = item["introduction"].asString
            val tag = item["tag"].asString
            val lastActiveTime = Date(item["lastActiveTime"].asLong)
            val portrait = Const.api[Const.Route.PublicUser] + "/portrait?target=$author"

            val blog = Outline(blogId, type, author, title, introduction, tag, portrait, lastActiveTime, nickname)

            blogs.add(blog)
        }

        return blogs
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
