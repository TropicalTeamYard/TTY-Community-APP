package tty.community.pages.fragment


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_create_blog_short.*
import pub.devrel.easypermissions.EasyPermissions
import tty.community.R
import tty.community.adapter.ImageListAdapter
import tty.community.file.IO
import tty.community.image.BitmapUtil
import tty.community.model.*
import tty.community.model.Blog.Companion.Tag
import tty.community.model.Blog.Companion.BlogType
import tty.community.network.AsyncNetUtils
import tty.community.util.CONF
import tty.community.util.Message
import java.io.File

class CreateBlogShortFragment : Fragment(), ImageListAdapter.OnItemClickListener, EasyPermissions.PermissionCallbacks, IGetBlogData{

    private val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val tag = Tag("000000", "ALL")
    private lateinit var imagesAdapter: ImageListAdapter
    private var _bitmap: Bitmap? = null

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>?) {
        Toast.makeText(context, "获取权限失败，将无法选择图片", Toast.LENGTH_SHORT).show()
    }
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?) {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                RESULT_LOAD_IMAGE -> {
                    val selectedImage = data.data!!
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor = this@CreateBlogShortFragment.activity?.contentResolver?.query(selectedImage, filePathColumn, null, null, null)
                    if (cursor != null && cursor.moveToFirst() && cursor.count > 0) {
                        val path = cursor.getString(cursor.getColumnIndex(filePathColumn[0]))
                        _bitmap = BitmapUtil.load(path, true)
                        _bitmap?.let { imagesAdapter.add(it) }
                        cursor.close()
                    }
                }
            }
        }
    }

    override fun onClick(p0: View?) {}
    override fun onItemClick(v: View?, position: Int) {
        Log.d(TAG, "pos: $position")
        if (position + 1 < imagesAdapter.itemCount) {
            imagesAdapter.delete(position)
        } else {
            getPicture()
        }
    }


    private fun init() {
        imagesAdapter = ImageListAdapter()
        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        create_blog_short_images.adapter = imagesAdapter
        create_blog_short_images.layoutManager = layoutManager
        imagesAdapter.setOnItemClickListener(this)
    }
    private fun getPermission() {
        if (!EasyPermissions.hasPermissions(activity, *permissions)) {
            EasyPermissions.requestPermissions(this, "我们需要获取您的相册使用权限", 1, *permissions)
        }
    }
    private fun getPicture() {
        getPermission()
        val intent = Intent(Intent.ACTION_PICK, null)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intent, RESULT_LOAD_IMAGE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_blog_short, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
//        create_blog_short_submit.setOnClickListener {
//            val user = this.context?.let { User.find(it) }
//            if (user != null) {
//                val title = "####nickname####的动态"
//                var content = create_blog_short_content.text.toString()
//
//                var introduction = "****summary****\n"
//                    .plus(try { content.substring(0, 120) + "..." } catch (e: StringIndexOutOfBoundsException) { if (content.isNotEmpty()) { content } else { "no content" } })
//                    .plus("\n****metadata****\n")
//
//                content = "<pre>\n$content\n</pre>\n\n"
//
//                val files = ArrayList<File>()
//                val pics = ArrayList<String>()
//
//                for (bitmap in imagesAdapter.images) {
//                    val file = IO.bitmap2FileCache(this.context!!, bitmap, 80)
//                    files.add(file)
//                    content =
//                        content.plus("![picture](./picture?id=####blog_id####&key=${file.name})<br>")
//                    pics.add("id=####blog_id####&key=${file.name}")
//                }
//
//                if (pics.isNotEmpty()) {
//                    introduction = introduction.plus(pics[0])
//                }
//
//                introduction = introduction.plus("\n****end****")
//
//                submit(user, Blog.Companion.BlogType.Short, tag, title, introduction, content, files)
//            } else {
//                Toast.makeText(context, "您还未登录，请先登录", Toast.LENGTH_SHORT).show()
//            }
//        }

    }

    /**
     * 获取Blog的所有信息，包装成BlogData
     */
    override fun getBlogData(): BlogData {
        val pics = ArrayList<File>()
        //To LocalCache
        for (bitmap in imagesAdapter.images) {
            val file = IO.bitmap2FileCache(this.context!!, bitmap, 80)
            pics.add(file)
        }
        //TODO 添加图片是否显示在主页的功能，目前默认使用前3张。
        val picLinks = ArrayList<Int>()
        for (i in 0 until imagesAdapter.images.size){
            if (i < 3){
                picLinks.add(i)
            }
        }

        return BlogData("", create_blog_short_content.text.toString(), picLinks, pics)
    }


    companion object {
        const val TAG = "CreateBlogShortFragment"
        const val RESULT_LOAD_IMAGE = 10
        const val RESULT_CROP_IMAGE = 20
    }

}
