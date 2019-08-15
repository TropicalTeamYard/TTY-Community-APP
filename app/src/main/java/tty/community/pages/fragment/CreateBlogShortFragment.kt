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
import kotlinx.android.synthetic.main.fragment_create_blog_short.*
import org.json.JSONObject
import pub.devrel.easypermissions.EasyPermissions
import tty.community.R
import tty.community.adapter.ImageListAdapter
import tty.community.database.MainDBHelper
import tty.community.file.IO
import tty.community.image.BitmapUtil
import tty.community.model.Shortcut
import tty.community.model.blog.Type
import tty.community.model.blog.Type.Companion.value
import tty.community.network.AsyncTaskUtil
import tty.community.values.Const
import java.io.File

class CreateBlogShortFragment : Fragment(), ImageListAdapter.OnItemClickListener,
    EasyPermissions.PermissionCallbacks {
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>?) {
        Toast.makeText(this.context, "获取权限失败，将无法选择图片", Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?) {}


    override fun onClick(p0: View?) {

    }

    override fun onItemClick(v: View?, position: Int) {
        Log.d(TAG, "pos: $position")
        if (position + 1 < imagesAdapter.itemCount) {
            imagesAdapter.delete(position)
        } else {
            choosePic()
        }
    }

    private var submitEnabled = true
    private var id = ""
    private var token = ""
    private val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private lateinit var imagesAdapter: ImageListAdapter
    private var _bitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_blog_short, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        setAdapter()

        create_blog_short_submit.setOnClickListener {
            submitEnabled = false
            create_blog_short_submit.isClickable = false
            submit()
        }

    }

    private fun choosePic() {
        getPermission()
        val intent = Intent(Intent.ACTION_PICK, null)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intent, RESULT_LOAD_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                RESULT_LOAD_IMAGE -> {
                    val selectedImage = data.data!!
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor = this@CreateBlogShortFragment.activity?.contentResolver?.query(
                        selectedImage,
                        filePathColumn,
                        null,
                        null,
                        null
                    )
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

    private fun getPermission() {
        if (!EasyPermissions.hasPermissions(activity, *permissions)) {
            EasyPermissions.requestPermissions(this, "我们需要获取您的相册使用权限", 1, *permissions)
        }
    }

    private fun getToken() {
        val user = this.context?.let { MainDBHelper(it).findUser() }
        if (user != null) {
            id = user.id
            token = user.token
        } else {
            Toast.makeText(this.context, "登录已过期，请先登录", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setAdapter() {
        imagesAdapter = ImageListAdapter()
        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        create_blog_short_images.adapter = imagesAdapter
        create_blog_short_images.layoutManager = layoutManager
        imagesAdapter.setOnItemClickListener(this)
    }

    private fun submit() {
        getToken()
        val map = HashMap<String, String>()
        map["id"] = id
        map["token"] = token
        map["title"] = TITLE
        map["type"] = "${TYPE.value}"
        var content = create_blog_short_content.text.toString() + "\n"
        val tag = create_blog_short_tag.text.trim().toString()
        map["tag"] = if (tag.isNotEmpty()) {
            tag
        } else {
            "ALL"
        }
        map["file_count"] = "${imagesAdapter.images.size}"
        val files = ArrayList<File>()

        map["introduction"] = try {
            (content.substring(0, 120) + "...")
        } catch (e: StringIndexOutOfBoundsException) {
            content
        }

        content = "<pre>\n$content\n</pre>\n\n"

        for (bitmap in imagesAdapter.images) {
            val file = IO.saveBitmapFile(this.context!!, bitmap)
            files.add(file)
            content =
                content.plus("![${file.name}](./picture?id=####blog_id####&key=${file.name})<br><br>")
        }

        map["content"] = content

        val url = Const.api[Const.Route.Blog] + "/create"

        AsyncTaskUtil.AsyncNetUtils.postMultipleForm(
            url,
            map,
            files,
            object : AsyncTaskUtil.AsyncNetUtils.Callback {
                override fun onResponse(response: String) {
                    Log.d(TAG, response)
                    val result = JSONObject(response)
                    val msg = result.optString("msg", "unknown error")
                    when (val shortcut = Shortcut.phrase(result.optString("shortcut", "UNKNOWN"))) {
                        Shortcut.OK -> {
                            Toast.makeText(
                                this@CreateBlogShortFragment.context,
                                msg,
                                Toast.LENGTH_SHORT
                            ).show()
                            this@CreateBlogShortFragment.activity?.finish()
                        }

                        Shortcut.TE -> {
                            //TODO 备份编辑项目
                            Toast.makeText(
                                this@CreateBlogShortFragment.context,
                                "账号信息已过期，请重新登陆",
                                Toast.LENGTH_SHORT
                            ).show()
                            submitEnabled = true
                            create_blog_short_submit.isClickable = true
                        }

                        else -> {
                            //TODO 备份编辑项目
                            Toast.makeText(
                                this@CreateBlogShortFragment.context,
                                "shortcut: ${shortcut.name}, error: $msg",
                                Toast.LENGTH_SHORT
                            ).show()
                            submitEnabled = true
                            create_blog_short_submit.isClickable = true
                        }
                    }
                }

            })
    }

    companion object {
        const val TAG = "CreateBlogShortFragment"
        private val TYPE = Type.Short
        const val TITLE = "####nickname####的动态"
        const val RESULT_LOAD_IMAGE = 10
        const val RESULT_CROP_IMAGE = 20
    }

}
