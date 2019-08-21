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
import tty.community.model.Params
import tty.community.model.Shortcut
import tty.community.model.Blog
import tty.community.model.Blog.Companion.Topic
import tty.community.model.Blog.Companion.BlogType
import tty.community.model.User
import tty.community.network.AsyncNetUtils
import tty.community.util.CONF
import tty.community.util.Message
import java.io.File

class CreateBlogShortFragment : Fragment(), ImageListAdapter.OnItemClickListener, EasyPermissions.PermissionCallbacks {

    private val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val tag = Topic("000000", "ALL")
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
    private fun submit(user: User, type: BlogType, tag: Topic, title: String, introduction: String, content: String, files: ArrayList<File>) {

        create_blog_short_submit.isClickable = false
        Toast.makeText(this.context, "上传中...", Toast.LENGTH_SHORT).show()

        // TODO 后台service上传
        AsyncNetUtils.postMultipleForm(CONF.API.blog.create, Params.createBlog(user, title, type, introduction, content, tag), files, object : AsyncNetUtils.Callback {
            fun onFail(msg: String): Int {
                Log.e(TAG, msg)
                //TODO 备份编辑项目
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                create_blog_short_submit.isClickable = true
                return 1
            }

            fun onSuccess(): Int {
                Toast.makeText(context, "上传成功", Toast.LENGTH_SHORT).show()
                this@CreateBlogShortFragment.activity?.finish()
                return 0
            }

            override fun onFailure(msg: String): Int {
                return onFail(msg)
            }

            override fun onResponse(result: String?): Int {
                val message: Message.MsgData<Blog.Outline>? = Message.MsgData.parse(result, object : TypeToken<Message.MsgData<Blog.Outline>>(){})
                return if (message != null) {
                    when (message.shortcut) {
                        Shortcut.OK -> onSuccess()
                        Shortcut.TE -> onFail("账号信息已过期，请重新登陆")
                        else -> onFail("shortcut异常")
                    }
                } else {
                    onFail("解析异常")
                }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_blog_short, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        create_blog_short_submit.setOnClickListener {
            val user = this.context?.let { User.find(it) }
            if (user != null) {
                val title = "####nickname####的动态"
                var content = create_blog_short_content.text.toString()

                var introduction = "****summary****\n"
                    .plus(try { content.substring(0, 120) + "..." } catch (e: StringIndexOutOfBoundsException) { if (content.isNotEmpty()) { content } else { "no content" } })
                    .plus("\n****metadata****\n")

                content = "<pre>\n$content\n</pre>\n\n"

                val files = ArrayList<File>()
                val pics = ArrayList<String>()

                for (bitmap in imagesAdapter.images) {
                    val file = IO.bitmap2FileCache(this.context!!, bitmap, 80)
                    files.add(file)
                    content =
                        content.plus("![picture](./picture?id=####blog_id####&key=${file.name})<br>")
                    pics.add("id=####blog_id####&key=${file.name}")
                }

                if (pics.isNotEmpty()) {
                    introduction = introduction.plus(pics[0])
                }

                introduction = introduction.plus("\n****end****")

                submit(user, Blog.Companion.BlogType.Short, tag, title, introduction, content, files)
            } else {
                Toast.makeText(context, "您还未登录，请先登录", Toast.LENGTH_SHORT).show()
            }
        }

    }

    companion object {
        const val TAG = "CreateBlogShortFragment"
        const val RESULT_LOAD_IMAGE = 10
        const val RESULT_CROP_IMAGE = 20
    }

}
