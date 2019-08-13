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
import com.google.gson.Gson
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.fragment_square.*
import org.json.JSONObject
import tty.community.R
import tty.community.adapter.BlogListAdapter
import tty.community.model.Shortcut
import tty.community.model.blog.Outline
import tty.community.network.AsyncTaskUtil
import tty.community.pages.activity.CreateBlogActivity
import tty.community.values.Const
import java.util.*
import kotlin.collections.ArrayList

class SquareFragment : Fragment(), BlogListAdapter.OnItemClickListener, OnRefreshListener,
    OnLoadMoreListener {
    override fun onLoadMore(refreshLayout: RefreshLayout) {
        loadMore(blogTag)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        refresh(blogTag)
    }

    override fun onItemClick(v: View?, position: Int) {
        Log.d(TAG, "pos: $position")
    }

    override fun onClick(p0: View?) {

    }

    private var blogTag = ""
    private lateinit var blogListAdapter: BlogListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_square, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab_add_blog.setOnClickListener {
            startActivity(Intent(this.context, CreateBlogActivity::class.java))
        }

        setAdapter()
        prepareBlog()

        square_refreshLayout.setOnRefreshListener(this)
        square_refreshLayout.setOnLoadMoreListener(this)
    }

    private fun refresh(tag: String = "") {
        Outline.initBlogList(Date(), 10, tag, object : AsyncTaskUtil.AsyncNetUtils.Callback {
            override fun onResponse(response: String) {
                Log.d(TAG, response)
                val result = JSONObject(response)
                val msg = result.optString("msg", "unknown error")
                when(val shortcut = Shortcut.phrase(result.optString("shortcut", "UNKNOWN"))) {
                    Shortcut.OK -> {
                        Toast.makeText(this@SquareFragment.context, "刷新成功", Toast.LENGTH_SHORT).show()
                        square_refreshLayout.finishRefresh(true)
                        val list = result.optJSONArray("data")
                        if (list != null) {
                            val blogs = ArrayList<Outline>()
                            for (i in 0 until list.length()) {
                                val item = list.getJSONObject(i)
                                val author = item.optString("author", "null")
                                val nickname = item.optString("nickname", "null")
                                val blogId = item.optString("blogId", "null")
                                val title = item.optString("title", "null")
                                val introduction = item.optString("introduction", "null")
                                val allTag = item.optString("tag", "null")
                                val lastActiveTime = Date(item.optLong("lastActiveTime"))
                                // http://localhost:8080/community/api/public/user/portrait?target=2008153477
                                val portrait = Const.api["public_user"] + "/portrait?target=$author"
                                val blog = Outline(blogId, title, author, nickname, portrait, introduction, lastActiveTime, allTag)
                                blogs.add(blog)
                            }
                            blogListAdapter.initData(blogs)

                        }
                    }

                    else -> {
                        Toast.makeText(this@SquareFragment.context, "shortcut: ${shortcut.name}, error: $msg", Toast.LENGTH_SHORT).show()
                        square_refreshLayout.finishLoadMore(false)
                    }
                }
            }

        })
    }

    private fun loadMore(tag: String = "") {
        Outline.loadMore(blogListAdapter.getLastBlogId(), 10, tag, object : AsyncTaskUtil.AsyncNetUtils.Callback {
            override fun onResponse(response: String) {
                Log.d(TAG, response)
                val result = JSONObject(response)
                val msg = result.optString("msg", "unknown error")
                when(val shortcut = Shortcut.phrase(result.optString("shortcut", "UNKNOWN"))) {
                    Shortcut.OK -> {
                        Toast.makeText(this@SquareFragment.context, "加载成功", Toast.LENGTH_SHORT).show()
                        square_refreshLayout.finishLoadMore(true)
                        val list = result.optJSONArray("data")
                        if (list != null) {
                            val blogs = ArrayList<Outline>()
                            for (i in 0 until list.length()) {
                                val item = list.getJSONObject(i)
                                val author = item.optString("author", "null")
                                val nickname = item.optString("nickname", "null")
                                val blogId = item.optString("blogId", "null")
                                val title = item.optString("title", "null")
                                val introduction = item.optString("introduction", "null")
                                val allTag = item.optString("tag", "null")
                                val lastActiveTime = Date(item.optLong("lastActiveTime", 0L))
                                // http://localhost:8080/community/api/public/user/portrait?target=2008153477
                                val portrait = Const.api["public_user"] + "/portrait?target=$author"
                                val blog = Outline(blogId, title, author, nickname, portrait, introduction, lastActiveTime, allTag)
                                blogs.add(blog)
                            }
                            blogListAdapter.add(blogs)

                        }
                    }

                    else -> {
                        Toast.makeText(this@SquareFragment.context, "shortcut: ${shortcut.name}, error: $msg", Toast.LENGTH_SHORT).show()
                        square_refreshLayout.finishLoadMore(false)
                    }
                }
            }

        })
    }


    private fun prepareBlog(tag: String = "") {
        Outline.initBlogList(Date(), 10, tag, object : AsyncTaskUtil.AsyncNetUtils.Callback {
            override fun onResponse(response: String) {
                Log.d(TAG, response)
                val result = JSONObject(response)
                val msg = result.optString("msg", "unknown error")
                when(val shortcut = Shortcut.phrase(result.optString("shortcut", "UNKNOWN"))) {
                    Shortcut.OK -> {
//                        Toast.makeText(this@SquareFragment.context, msg, Toast.LENGTH_SHORT).show()
                        val list = result.optJSONArray("data")
                        if (list != null) {
                            val blogs = ArrayList<Outline>()
                            for (i in 0 until list.length()) {
                                val item = list.getJSONObject(i)
                                val author = item.optString("author", "null")
                                val nickname = item.optString("nickname", "null")
                                val blogId = item.optString("blogId", "null")
                                val title = item.optString("title", "null")
                                val introduction = item.optString("introduction", "null")
                                val allTag = item.optString("tag", "null")
                                val lastActiveTime = Date(item.optLong("lastActiveTime"))
                                // http://localhost:8080/community/api/public/user/portrait?target=2008153477
                                val portrait = Const.api["public_user"] + "/portrait?target=$author"
                                val blog = Outline(blogId, title, author, nickname, portrait, introduction, lastActiveTime, allTag)
                                blogs.add(blog)
                            }
                            blogListAdapter.initData(blogs)

                        }
                    }

                    else -> {
                        Toast.makeText(this@SquareFragment.context, "shortcut: ${shortcut.name}, error: $msg", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        })
    }

    private fun setAdapter() {
        blogListAdapter = BlogListAdapter()
        val layoutManager= LinearLayoutManager(this.context)
        layoutManager.orientation= LinearLayoutManager.VERTICAL
        square_blog_list.adapter = blogListAdapter
        square_blog_list.layoutManager = layoutManager
        blogListAdapter.setOnItemClickListener(this)
    }

    companion object {
        const val TAG = "SquareFragment"
    }

}
