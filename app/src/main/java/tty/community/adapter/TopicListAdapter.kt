package tty.community.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_tag.view.*
import tty.community.R
import tty.community.model.Blog.Companion.Topic

class TopicListAdapter : RecyclerView.Adapter<TopicListAdapter.ViewHolder>() {

    var selected = 0

    var topics = ArrayList<Topic>()
    private var mClickListener: OnTopicClickListener? = null
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_tag, parent, false), mClickListener)
    }

    override fun getItemCount(): Int {
        return topics.size
    }

    interface OnTopicClickListener : View.OnClickListener {
        fun onTopicClick(v: View?, topic: Topic)
    }

    fun setOnItemClickListener(listener: OnTopicClickListener) {
        this.mClickListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in 0 until topics.size) {
            holder.tag.text = topics[position].name
            if (selected == position) {
                holder.tag.setTextColor(Color.GREEN)
            } else {
                holder.tag.setTextColor(Color.GRAY)
            }
        }
    }

    fun updateTopics(tags: ArrayList<Topic>) {
        this.topics = tags
//        this.topics.add(0, Topic("000", "推荐"))
        this.topics.add(0, Topic("", "ALL"))
        this.topics.add(Topic("-1", " + "))

        notifyDataSetChanged()
    }

    inner class ViewHolder(v: View, private var listener: OnTopicClickListener?) :
        RecyclerView.ViewHolder(v), View.OnClickListener {
        private var mListener: OnTopicClickListener

        val tag: TextView = v.following_tag

        override fun onClick(p0: View?) {
            listener?.onTopicClick(p0, topics[layoutPosition])
            selected = layoutPosition
            notifyDataSetChanged()
        }

        init {
            v.setOnClickListener(this)
            mListener = this.listener!!
        }
    }

}