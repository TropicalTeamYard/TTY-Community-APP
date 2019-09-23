package tty.community.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_topic_outline.view.*
import tty.community.R
import tty.community.image.BitmapUtil
import tty.community.model.Topic
import tty.community.util.CONF

class TopicOutLineListAdapter(val context: Context, recyclerView: RecyclerView) : RecyclerView.Adapter<TopicOutLineListAdapter.ViewHolder>() {

    init {
        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.adapter = this
        recyclerView.layoutManager = layoutManager
    }

    var selected = -1
    var topics = ArrayList<Topic.Outline>()
    private var listener: OnTopicClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_topic_outline, parent, false), listener)
    }

    override fun getItemCount(): Int {
        return topics.size
    }

    interface OnTopicClickListener : View.OnClickListener {
        fun onTopicClick(v: View?, topic: Topic.Outline)
    }

    fun setOnItemClickListener(listener: OnTopicClickListener) {
        this.listener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (selected == position) {
            holder.container.setBackgroundColor(Color.LTGRAY)
        } else {
            holder.container.setBackgroundColor(Color.WHITE)
        }
        holder.name.text = topics[position].name
        holder.introduction.text = topics[position].introduction
        val url = CONF.API.topic.picture + "?" + "id=${topics[position].id}"
//        Glide.with(context).load(url).apply(BitmapUtil.optionsMemoryCache()).centerCrop().into(holder.picture)
    }

    fun updateTopics(topics: ArrayList<Topic.Outline>) {
        selected = -1
        this.topics = topics
        this.topics.add(0, Topic.Outline("000000", "ALL", "000000", "TropicalTeamYard(TTY)"))
        notifyDataSetChanged()
    }

    inner class ViewHolder(v: View, private var listener: OnTopicClickListener?) :
        RecyclerView.ViewHolder(v), View.OnClickListener {
        private var mListener: OnTopicClickListener

        val container: RelativeLayout = v.topic_outline_box
        val name: TextView = v.topic_name
        val introduction: TextView = v.topic_introduction
        val picture: ImageView = v.topic_picture

        override fun onClick(p0: View?) {
            listener?.onTopicClick(p0, topics[layoutPosition])
            selected = layoutPosition
            notifyDataSetChanged()
        }

        init {
            container.setOnClickListener(this)
            name.setOnClickListener(this)
            introduction.setOnClickListener(this)
            picture.setOnClickListener(this)
            mListener = this.listener!!
        }
    }
}