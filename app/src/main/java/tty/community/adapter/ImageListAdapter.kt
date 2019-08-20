package tty.community.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_pic.view.*
import tty.community.R

class ImageListAdapter : RecyclerView.Adapter<ImageListAdapter.ViewHolder>() {

    var images = ArrayList<Bitmap>()
    private var mClickListener: OnItemClickListener? = null
    private lateinit var context: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_pic,
                parent,
                false
            ), mClickListener
        )
    }

    override fun getItemCount(): Int {
        return images.size + 1
    }


    interface OnItemClickListener : View.OnClickListener {
        fun onItemClick(v: View?, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mClickListener = listener
    }

    fun add(bitmap: Bitmap) {
        images.add(bitmap)
        notifyDataSetChanged()
    }

    fun delete(pos: Int) {
        if (pos in 0 until images.size) {
            images.removeAt(pos)
            notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in 0 until images.size) {
            val bitmap = images[position]
            val width = bitmap.width
            val height = bitmap.height
            val bm = if (width > height) {
                Bitmap.createBitmap(bitmap, (width - height) / 2, 0, height, height)
            } else {
                Bitmap.createBitmap(bitmap, 0, (height - width) / 2, width, width)
            }

            holder.picture.setImageBitmap(bm)
        } else {
            holder.picture.setImageDrawable(
                context.resources.getDrawable(
                    R.drawable.ic_add_gray,
                    null
                )
            )
        }
    }

    inner class ViewHolder(v: View, private var listener: OnItemClickListener?) :
        RecyclerView.ViewHolder(v), View.OnClickListener {
        private var mListener: OnItemClickListener
        val picture: ImageView = v.item_img

        override fun onClick(p0: View?) {
            listener?.onItemClick(p0, layoutPosition)
        }

        init {
            v.setOnClickListener(this)
            mListener = this.listener!!
        }
    }


}