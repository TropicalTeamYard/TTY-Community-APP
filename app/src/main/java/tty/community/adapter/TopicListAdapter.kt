package tty.community.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_tag.view.*
import tty.community.R
import tty.community.model.Topic

class TopicListAdapter(
    val context: Context,
    recyclerView: RecyclerView
) : RecyclerView.Adapter<TopicListAdapter.ViewHolder>() {

    init {
        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView.adapter = this
        recyclerView.layoutManager = layoutManager
    }

    var selected = 0
    var topics = ArrayList<Topic.Outline>()
    private var listener: OnTopicClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_tag, parent, false), listener)
    }

    override fun getItemCount(): Int {
        return topics.size
    }

    interface OnTopicClickListener : View.OnClickListener {
        fun onTopicClick(v: View?, topic: Topic.Outline, type: TopicType)
    }

    fun setOnItemClickListener(listener: OnTopicClickListener) {
        this.listener = listener
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

    fun updateTopics(tags: ArrayList<Topic.Outline>) {
        this.topics = tags
        this.topics.add(0, Topic.Outline("", "ALL", "", ""))
        this.topics.add(Topic.Outline("-1", " + ", "-1", "add"))
        notifyDataSetChanged()
    }

    inner class ViewHolder(v: View, private var listener: OnTopicClickListener?) :
        RecyclerView.ViewHolder(v), View.OnClickListener {
        private var mListener: OnTopicClickListener

        val tag: TextView = v.following_tag

        override fun onClick(p0: View?) {
            if (layoutPosition < topics.size - 1) {
                listener?.onTopicClick(p0, topics[layoutPosition], TopicType.Topic)
                selected = layoutPosition
                notifyDataSetChanged()
            } else {
                listener?.onTopicClick(p0, topics[layoutPosition], TopicType.Add)
            }
        }

        init {
            v.setOnClickListener(this)
            mListener = this.listener!!
        }
    }

    enum class TopicType {
        Topic, Add
    }
}