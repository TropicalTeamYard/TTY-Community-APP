package tty.community.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_me.*
import kotlinx.android.synthetic.main.item_blog_outline.view.*
import tty.community.R
import tty.community.image.BitmapUtil
import tty.community.model.blog.Outline
import tty.community.values.Const
import tty.community.values.Util

class BlogListAdapter: RecyclerView.Adapter<BlogListAdapter.ViewHolder>() {
    private var blogs = ArrayList<Outline>()
    private var mClickListener: OnItemClickListener ?= null
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_blog_outline, parent, false), mClickListener)
    }

    override fun getItemCount(): Int {
        return blogs.size
    }


    interface OnItemClickListener:View.OnClickListener{
        fun onItemClick(v:View?, position:Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.mClickListener = listener
    }

    fun add(blog: ArrayList<Outline>) {
        blogs.addAll(blog)
        notifyDataSetChanged()
    }

    fun initData(blogs: ArrayList<Outline>) {
        this.blogs = blogs
        notifyDataSetChanged()
    }

    fun getLastBlogId(): String {
        return blogs[blogs.size - 1].blogId
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val author = blogs[position].author
        holder.blogId.text = blogs[position].blogId
        holder.time.text = Util.getTime(blogs[position].lastActiveTime)
        holder.title.text = blogs[position].title
        holder.nickname.text = blogs[position].nickname
        holder.introduction.text = blogs[position].introduction
        holder.tag.text = blogs[position].tag

//        val url = "${Const.api["public_user"]}/portrait?target=$author"

        val url = blogs[position].portrait
        Glide.with(context).load(url).apply(BitmapUtil.optionsMemoryCache()).into(holder.portrait)
    }

    inner class ViewHolder(v: View, private var listener: OnItemClickListener?):RecyclerView.ViewHolder(v), View.OnClickListener {
        private var mListener: OnItemClickListener
        val blogId: TextView = v.blog_id
        val time: TextView = v.blog_time
        val title: TextView = v.blog_title
        val nickname: TextView = v.blog_author_nickname
        val introduction: TextView = v.blog_introduction
        val portrait: ImageView = v.blog_author_portrait
        val tag: TextView = v.blog_tag

        override fun onClick(p0: View?) {
            listener?.onItemClick(p0, layoutPosition)
        }

        init {
            v.setOnClickListener(this)
            mListener = this.listener!!
        }
    }
}