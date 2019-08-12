package tty.community.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_blog_simple.view.*
import tty.community.R
import tty.community.model.blog.Outline
import tty.community.values.Util

class SimpleBlogListAdapter: RecyclerView.Adapter<SimpleBlogListAdapter.ViewHolder>() {
    private var blogs = ArrayList<Outline>()
    private var mClickListener: OnItemClickListener ?= null
    private lateinit var context: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_blog_simple, parent, false), mClickListener)
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
        holder.id.text = blogs[position].blogId
        holder.time.text = Util.getTime(blogs[position].lastActiveTime)
    }

    inner class ViewHolder(v: View, private var listener: OnItemClickListener?):RecyclerView.ViewHolder(v), View.OnClickListener {
        private var mListener: OnItemClickListener
        val id: TextView = v.blog_id
        val time: TextView = v.blog_time

        override fun onClick(p0: View?) {
            listener?.onItemClick(p0, layoutPosition)
        }

        init {
            v.setOnClickListener(this)
            mListener= this.listener!!
        }
    }

}