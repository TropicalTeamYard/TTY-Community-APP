package tty.community.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_tag.view.*
import tty.community.R
import tty.community.model.blog.Tag

class TagListAdapter : RecyclerView.Adapter<TagListAdapter.ViewHolder>() {

    var tags = ArrayList<Tag>()
    private var mClickListener: OnItemClickListener? = null
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_tag,
                parent,
                false
            ), mClickListener
        )
    }

    override fun getItemCount(): Int {
        return tags.size + 1
    }


    interface OnItemClickListener : View.OnClickListener {
        fun onItemClick(v: View?, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mClickListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in 0 until tags.size) {
            holder.tag.text = tags[position].text
        } else {
            holder.tag.text = " + "
        }
    }

    fun updateTags(tags: ArrayList<Tag>) {
        this.tags = tags
        notifyDataSetChanged()
    }

    inner class ViewHolder(v: View, private var listener: OnItemClickListener?) :
        RecyclerView.ViewHolder(v), View.OnClickListener {
        private var mListener: OnItemClickListener

        val tag: TextView = v.following_tag

        override fun onClick(p0: View?) {
            listener?.onItemClick(p0, layoutPosition)
        }

        init {
            v.setOnClickListener(this)
            mListener = this.listener!!
        }
    }


}