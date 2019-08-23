package tty.community.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.item_blog_outline.view.*
import tty.community.R
import tty.community.image.BitmapUtil
import tty.community.model.Blog.Outline
import tty.community.model.BlogData
import tty.community.util.CONF
import tty.community.util.Time
import tty.community.util.Time.getFormattedTime
import tty.community.widget.RoundAngleImageView
import java.lang.Exception

class BlogListAdapter(var context: Context, private val recyclerView: RecyclerView) : RecyclerView.Adapter<BlogListAdapter.ViewHolder>() {
    private var blogs = ArrayList<Outline>()

    private var listener: OnBlogClickListener? = null


    init {
        val blogListLayoutManager = LinearLayoutManager(this.context)
        blogListLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.adapter = this
        recyclerView.layoutManager = blogListLayoutManager
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_blog_outline, parent, false), listener)
    }

    override fun getItemCount(): Int {
        return blogs.size
    }


    fun setOnBlogItemClickListener(listener: OnBlogClickListener) {
        this.listener = listener
    }

    fun add(blogs: ArrayList<Outline>) {
        this.blogs.addAll(blogs)
        notifyDataSetChanged()
    }

    fun init(blogs: ArrayList<Outline>) {
        this.blogs = blogs
        notifyDataSetChanged()
    }

    fun getLastBlogId(): String? {
        return if (blogs.isNotEmpty()) { blogs[blogs.size - 1].blogId } else { null }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val author = blogs[position].author
        val introduction = blogs[position].introduction
        val blogId = blogs[position].blogId
        try {
            val data: BlogData.Introduction = CONF.gson.fromJson(introduction, object : TypeToken<BlogData.Introduction>(){}.type)
            holder.introduction.text = data.summary
            if (data.pics.size > 0){
                holder.picturesView.visibility = View.VISIBLE
                val picUrls = ArrayList<String>()
                for (key in data.picLinks) {
                    val url = BlogData.toUrl(blogId, data.pics[key])
                    picUrls.add(url)
                }
                holder.picturesAdapter.picUrls = picUrls
            } else {
                holder.picturesView.visibility = View.GONE
            }
        } catch (e:Exception) {
            e.printStackTrace()
        }

        holder.time.text = Time.getTime(blogs[position].lastActiveTime).getFormattedTime()
        holder.title.text = blogs[position].title
        holder.nickname.text = blogs[position].nickname

        holder.tag.text = blogs[position].topic.name
        val portraitUrl = blogs[position].portrait()

        Glide.with(context).load(portraitUrl).apply(BitmapUtil.optionsMemoryCacheDefaultPortrait()).centerCrop().into(holder.portrait)
    }

    inner class ViewHolder(v: View, private var listener: OnBlogClickListener?) :
        RecyclerView.ViewHolder(v), View.OnClickListener, PictureListAdapter.OnPictureClickListener {

        private var mListener: OnBlogClickListener
        private val card: CardView = v.blog_outline_card
        val time: TextView = v.blog_time
        val title: TextView = v.blog_title
        val nickname: TextView = v.blog_author_nickname
        val introduction: TextView = v.blog_introduction
        val portrait: RoundAngleImageView = v.blog_author_portrait
        val tag: TextView = v.blog_tag
        val picturesView: RecyclerView = v.blog_picture
        val picturesAdapter = PictureListAdapter(v.context, picturesView)
        private val more: Button = v.blog_outline_more

        override fun onClick(p0: View?) {
            listener?.onBlogClick(p0, layoutPosition, blogs[layoutPosition])
        }

        override fun onPictureClick(v: View?, position: Int) {
            listener?.onBlogPictureClick(v, layoutPosition, picturesAdapter.picUrls, position)
        }

        init {
            card.setOnClickListener(this)
            more.setOnClickListener(this)
            tag.setOnClickListener(this)
            portrait.setOnClickListener(this)
            picturesAdapter.setOnPictureClickListener(this)
            mListener = this.listener!!
        }
    }

    interface OnBlogClickListener : View.OnClickListener {
        fun onBlogClick(v: View?, position: Int, blog: Outline)
        fun onBlogPictureClick(v: View?, position: Int, picUrls: ArrayList<String>, index: Int)
    }
}