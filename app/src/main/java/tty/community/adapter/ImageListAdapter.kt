package tty.community.adapter

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_pic.view.*
import tty.community.R

class ImageListAdapter(val context: Context, recyclerView: RecyclerView) : RecyclerView.Adapter<ImageListAdapter.ViewHolder>() {

    var images = ArrayList<Bitmap>()
    private var listener: OnImageClickListener? = null

    init {
        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView.adapter = this
        recyclerView.layoutManager = layoutManager
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_pic, parent, false), listener)
    }

    override fun getItemCount(): Int {
        return images.size + 1
    }


    interface OnImageClickListener : View.OnClickListener {
        fun onImageClick(v: View?, position: Int)
    }

    fun setOnItemClickListener(listener: OnImageClickListener) {
        this.listener = listener
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
                context.resources.getDrawable(R.drawable.ic_add_gray, null)
            )
        }
    }

    inner class ViewHolder(v: View, private var listener: OnImageClickListener?) :
        RecyclerView.ViewHolder(v), View.OnClickListener {
        private var mListener: OnImageClickListener
        val picture: ImageView = v.item_img

        override fun onClick(p0: View?) {
            listener?.onImageClick(p0, layoutPosition)
        }

        init {
            v.setOnClickListener(this)
            mListener = this.listener!!
        }
    }

    companion object {
        const val TAG = "ImageListAdapter"
    }
}