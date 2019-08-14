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
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONObject
import tty.community.R
import tty.community.adapter.BlogListAdapter
import tty.community.model.Shortcut
import tty.community.model.blog.Outline
import tty.community.network.AsyncTaskUtil
import tty.community.pages.activity.CreateBlogActivity
import tty.community.values.Const
import java.lang.Exception
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

        square_refreshLayout.autoRefresh()

        square_refreshLayout.setOnRefreshListener(this)
        square_refreshLayout.setOnLoadMoreListener(this)
    }

    private fun refresh(tag: String = "") {
        Outline.initBlogList(Date(), 10, tag, object : AsyncTaskUtil.AsyncNetUtils.Callback {
            override fun onResponse(response: String) {
                try {
                    Log.d(TAG, response)
                    val result = JSONObject(response)
                    when(Shortcut.phrase(result.optString("shortcut", "UNKNOWN"))) {
                        Shortcut.OK -> {
                            Log.d(TAG, "刷新成功")
                            square_refreshLayout.finishRefresh()
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
                                    val portrait = Const.api[Const.Route.PublicUser] + "/portrait?target=$author"
                                    val blog = Outline(blogId, title, author, nickname, portrait, introduction, lastActiveTime, allTag)
                                    blogs.add(blog)
                                }
                                blogListAdapter.initData(blogs)

                            }
                        }

                        else -> {
                            Log.d(TAG, "刷新失败")
                            Toast.makeText(this@HomeFragment.context, "刷新失败", Toast.LENGTH_SHORT).show()
                            square_refreshLayout.finishRefresh(false)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d(TAG, "刷新失败")
                    Toast.makeText(this@HomeFragment.context, "未知错误", Toast.LENGTH_SHORT).show()
                    square_refreshLayout.finishRefresh(false)
                }

            }

        })
    }

    private fun loadMore(tag: String = "") {
        blogListAdapter.getLastBlogId()?.let {
            Outline.loadMore(it, 10, tag, object : AsyncTaskUtil.AsyncNetUtils.Callback {
                override fun onResponse(response: String) {
                    try {
                        Log.d(TAG, response)
                        val result = JSONObject(response)
                        when(Shortcut.phrase(result.optString("shortcut", "UNKNOWN"))) {
                            Shortcut.OK -> {
                                Log.d(TAG, "加载成功")
                                square_refreshLayout.finishLoadMore()
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
                                        val portrait = Const.api[Const.Route.PublicUser] + "/portrait?target=$author"
                                        val blog = Outline(blogId, title, author, nickname, portrait, introduction, lastActiveTime, allTag)
                                        blogs.add(blog)
                                    }
                                    blogListAdapter.add(blogs)

                                }
                            }

                            else -> {
                                Log.d(TAG, "加载失败1")
                                Toast.makeText(this@HomeFragment.context, "加载失败", Toast.LENGTH_SHORT).show()
                                square_refreshLayout.finishLoadMore(false)
                            }
                        }
                    } catch (e: Exception) {
                        square_refreshLayout.finishLoadMore()
                        Log.d(TAG, "加载失败2")
                        Toast.makeText(this@HomeFragment.context, "加载失败", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                }
            })

            return
        }
        Log.d(TAG, "加载失败3")
        Toast.makeText(this@HomeFragment.context, "加载失败", Toast.LENGTH_SHORT).show()
        square_refreshLayout.finishLoadMore(500)
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
        const val TAG = "HomeFragment"
    }

}
