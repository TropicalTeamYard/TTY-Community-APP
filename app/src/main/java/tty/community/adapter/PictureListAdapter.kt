package tty.community.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_pic.view.*
import tty.community.R
import tty.community.image.BitmapUtil
import tty.community.widget.RoundAngleImageView

class PictureListAdapter(val context: Context, private val recyclerView: RecyclerView): RecyclerView.Adapter<PictureListAdapter.ViewHolder>()  {

    private var listener: OnPictureClickListener? = null
    var picUrls = ArrayList<String>()
        set(value) {
            when (value.size) {
                0, 1, 2, 3, 4 -> {
                    field = value
                    val layoutManager = GridLayoutManager(context, picUrls.size)
                    layoutManager.orientation = GridLayoutManager.VERTICAL
                    recyclerView.layoutManager = layoutManager
                }

                else -> {
                    field = value
                    val layoutManager = LinearLayoutManager(context)
                    layoutManager.orientation = LinearLayoutManager.HORIZONTAL
                    recyclerView.layoutManager = layoutManager
                }
            }
            notifyDataSetChanged()
        }


    init {
        recyclerView.adapter = this
    }


    interface OnPictureClickListener : View.OnClickListener {
        fun onPictureClick(v: View?, position: Int)
    }

    fun setOnPictureClickListener(listener: OnPictureClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_pic, parent, false), listener)
    }

    override fun getItemCount(): Int { return picUrls.size }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(picUrls[position]).apply(BitmapUtil.optionsMemoryCache()).override(256, 256).centerCrop().into(holder.picture)
    }

    class ViewHolder(v: View, private var listener: OnPictureClickListener?): RecyclerView.ViewHolder(v), View.OnClickListener {
        override fun onClick(p0: View?) {
            listener?.onPictureClick(p0, layoutPosition)
        }
        private var mListener: OnPictureClickListener
        val picture: ImageView = v.item_img

        init {
            picture.setOnClickListener(this)
            mListener = listener!!
        }
    }
}