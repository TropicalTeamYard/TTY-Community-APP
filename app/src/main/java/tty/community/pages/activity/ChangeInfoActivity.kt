package tty.community.pages.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_change_info.*
import pub.devrel.easypermissions.EasyPermissions
import tty.community.R
import tty.community.file.IO
import tty.community.image.BitmapUtil
import tty.community.image.BitmapUtil.zoom
import tty.community.model.Params
import tty.community.model.Params.changeInfo
import tty.community.model.Shortcut
import tty.community.model.User
import tty.community.network.AsyncNetUtils
import tty.community.network.NetMessage
import tty.community.network.NetMessage.post
import tty.community.image.BitmapUtil.cropCenter
import tty.community.util.CONF
import tty.community.util.Message
import java.util.regex.Pattern

class ChangeInfoActivity : AppCompatActivity(), View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    var info: User.PrivateInfo? = null

    private fun getInfo(user: User?, setInfo: Boolean) {
        if (user != null) {
            post(CONF.API.user.info, Params.privateInfo(user), object : TypeToken<Message.MsgData<User.PrivateInfo>>() {}, object : NetMessage.Callback {
                fun onFail(msg: String): Int {
                    Toast.makeText(this@ChangeInfoActivity, msg, Toast.LENGTH_SHORT).show()
                    return 1
                }
                fun onSuccess(info: User.PrivateInfo): Int {
                    val url = CONF.API.public.portrait + "?" + "id=${user.id}"
                    Glide.with(this@ChangeInfoActivity).load(url).apply(BitmapUtil.optionsNoCachePortraitDefaultUser()).centerCrop().into(change_info_portrait)
                    this@ChangeInfoActivity.info = info
                    if (setInfo) {
                        change_info_nickname.setText(info.nickname)
                        change_info_email.setText(info.email)
                        change_info_school.setText(info.school)
                        change_info_signature.setText(info.signature)
                    }
                    return 0
                }
                override fun <T> result(message: T?): Int {
                    return if (message is Message.MsgData<*>) {
                        when (message.shortcut) {
                            Shortcut.OK -> {
                                if (message.data is User.PrivateInfo) {
                                    onSuccess(message.data)
                                } else {
                                    onFail("解析异常")
                                }
                            }
                            Shortcut.UNE -> {
                                User.reset(this@ChangeInfoActivity)
                                onFail("用户不存在，请重新登录")
                            }
                            Shortcut.TE -> {
                                User.reset(this@ChangeInfoActivity)
                                onFail("登录过期，请重新登录")
                            }
                            else -> onFail("shortcut异常")
                        }
                    } else {
                        onFail("网络异常")
                    }
                }
            })
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    private fun getPermission() {
        if (!EasyPermissions.hasPermissions(this, *permissions)) {
            EasyPermissions.requestPermissions(this, "我们需要获取您的相册使用权限", 1, *permissions)
        }
    }
    private fun getPicture() {
        getPermission()
        val intent = Intent(Intent.ACTION_PICK, null)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intent, RESULT_LOAD_IMAGE)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>?) {
        Toast.makeText(this, "获取权限失败，将无法选择图片", Toast.LENGTH_SHORT).show()
    }
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?) {}
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.change_info_submit -> {
                val info = this.info
                val user = User.find(this)
                if (user != null && info != null) {
                    val nickname = change_info_nickname.text.toString()
                    val email = change_info_email.text.toString()
                    val school = change_info_school.text.toString()
                    val signature = change_info_signature.text.toString()
                    AsyncNetUtils.post(CONF.API.user.changeInfo, changeInfo(user, info, nickname, email, school, signature), object : AsyncNetUtils.Callback {
                        override fun onResponse(result: String?): Int {
                            val message: Message.MsgData<ArrayList<User.PrivateInfo.Item>>? = Message.MsgData.parse(result, object : TypeToken<Message.MsgData<ArrayList<User.PrivateInfo.Item>>>(){})
                            return if (message != null) {
                                when(message.shortcut) {
                                    Shortcut.OK -> onSuccess(message.data)
                                    Shortcut.TE -> onFail("登录已过期, 请重新登录")
                                    Shortcut.UNE -> onFail("你的账号出现异常")
                                    else -> onFail("未知异常")
                                }
                            } else {
                                onFail("网络异常")
                            }
                        }

                        override fun onFailure(msg: String): Int {
                            return onFail(msg)
                        }

                        fun onFail(msg: String = "网络异常"): Int {
                            Toast.makeText(this@ChangeInfoActivity, msg, Toast.LENGTH_SHORT).show()
                            return 1
                        }

                        fun onSuccess(items: ArrayList<User.PrivateInfo.Item>): Int {
                            for (item in items) {
                                when(item.key) {
                                    "nickname" -> {
                                        when(item.status) {
                                            Shortcut.OK -> {
                                                change_info_nickname_info.text = "昵称修改成功"
                                                change_info_nickname_info.setTextColor(Color.GREEN)
                                            }

                                            Shortcut.AIF -> {
                                                change_info_nickname_info.text = "昵称只允许包含中文, 数字, 字母"
                                                change_info_nickname_info.setTextColor(Color.GREEN)
                                            }

                                            Shortcut.UNE -> {
                                                change_info_nickname_info.text = "你的账号出现异常"
                                                change_info_nickname_info.setTextColor(Color.RED)
                                            }

                                            Shortcut.UR -> {
                                                change_info_nickname_info.text = "昵称修改失败, $nickname 已经被注册"
                                                change_info_nickname_info.setTextColor(Color.RED)
                                            }

                                            else -> {
                                                change_info_nickname_info.text = "昵称修改失败, 未知异常"
                                                change_info_nickname_info.setTextColor(Color.RED)
                                            }
                                        }
                                    }

                                    "email" -> {
                                        when(item.status) {
                                            Shortcut.OK -> {
                                                change_info_email_info.text = "邮箱修改成功"
                                                change_info_email_info.setTextColor(Color.GREEN)
                                            }

                                            Shortcut.AIF -> {
                                                change_info_email_info.text = "邮箱修改失败, 邮箱格式错误"
                                                change_info_email_info.setTextColor(Color.RED)
                                            }

                                            Shortcut.UNE -> {
                                                change_info_email_info.text = "邮箱修改失败, 你的账号出现异常"
                                                change_info_email_info.setTextColor(Color.RED)
                                            }

                                            else -> {
                                                change_info_email_info.text = "邮箱修改失败, 未知异常"
                                                change_info_email_info.setTextColor(Color.RED)
                                            }
                                        }
                                    }

                                    "school" -> {
                                        when(item.status) {
                                            Shortcut.OK -> {
                                                change_info_school_info.text = "学校修改成功"
                                                change_info_school_info.setTextColor(Color.GREEN)
                                            }

                                            Shortcut.AIF -> {
                                                change_info_school_info.text = "学校修改失败, 学校名称不符合要求(长度)"
                                                change_info_school_info.setTextColor(Color.RED)
                                            }

                                            Shortcut.UNE -> {
                                                change_info_school_info.text = "学校修改失败, 你的账号出现异常"
                                                change_info_school_info.setTextColor(Color.RED)
                                            }

                                            else -> {
                                                change_info_school_info.text = "学校修改失败, 未知异常"
                                                change_info_school_info.setTextColor(Color.RED)
                                            }
                                        }
                                    }

                                    "signature" -> {
                                        when(item.status) {
                                            Shortcut.OK -> {
                                                change_info_signature_info.text = "签名修改成功"
                                                change_info_signature_info.setTextColor(Color.GREEN)
                                            }

                                            Shortcut.AIF -> {
                                                change_info_signature_info.text = "签名修改失败, 签名长度不符合要求"
                                                change_info_signature_info.setTextColor(Color.RED)
                                            }

                                            Shortcut.UNE -> {
                                                change_info_signature_info.text = "签名修改失败, 你的账号出现异常"
                                                change_info_signature_info.setTextColor(Color.RED)
                                            }

                                            else -> {
                                                change_info_signature_info.text = "签名修改失败, 未知异常"
                                                change_info_signature_info.setTextColor(Color.RED)
                                            }
                                        }
                                    }

                                }
                            }
                            getInfo(user, false)
                            return 0
                        }

                    })

                } else {
                    Toast.makeText(this, "用户账户验证失败", Toast.LENGTH_SHORT).show()
                }

            }

            R.id.change_info_portrait -> {
                getPicture()
            }

            else -> Log.d(TAG, "unknown button clicked")
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_info)
        val user = User.find(this)
        getInfo(user, true)
        change_info_nickname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val nickname = s.toString()
                when {
                    info == null -> {
                        change_info_nickname_info.text = "用户验证失败"
                        change_info_nickname_info.setTextColor(Color.RED)
                    }
                    nickname == info?.nickname -> {
                        change_info_nickname_info.text = "昵称与原来相同，无需改变"
                        change_info_nickname_info.setTextColor(Color.GRAY)
                        return
                    }
                    !(2..15).contains(nickname.length) -> {
                        change_info_nickname_info.text = "昵称 $nickname 长度不符合要求"
                        change_info_nickname_info.setTextColor(Color.RED)
                    }
                    !nickname.checkNickname() -> {
                        change_info_nickname_info.text = "昵称只允许包含中文, 数字, 字母"
                        change_info_nickname_info.setTextColor(Color.RED)
                        return
                    }
                    else -> {
                        AsyncNetUtils.post(CONF.API.user.checkName, hashMapOf(Pair("nickname", nickname)), object : AsyncNetUtils.Callback {
                            fun onFail(msg: String): Int {
                                change_info_nickname_info.text = msg
                                change_info_nickname_info.setTextColor(Color.RED)
                                return 1
                            }

                            fun onSuccess(msg: String): Int {
                                change_info_nickname_info.text = msg
                                change_info_nickname_info.setTextColor(Color.GREEN)
                                return 0
                            }

                            override fun onFailure(msg: String): Int {
                                return onFail(msg)
                            }

                            override fun onResponse(result: String?): Int {
                                val message: Message.Msg? = Message.Msg.parse(result)
                                return if (message != null) {
                                    when (message.shortcut) {
                                        Shortcut.OK -> onSuccess("昵称 $nickname 可以使用")
                                        Shortcut.UR -> onFail("昵称 $nickname 已经被注册了")
                                        else -> onFail("shortcut异常")
                                    }
                                } else {
                                    onFail("解析异常")
                                }
                            }
                        })
                    }
                }
                change_info_nickname_info.visibility = View.VISIBLE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        change_info_email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString()
                when {
                    info == null -> {
                        change_info_email_info.text = "用户验证失败"
                        change_info_email_info.setTextColor(Color.RED)
                    }
                    email == info?.email -> {
                        change_info_email_info.text = "邮箱与原来相同，无需改变"
                        change_info_email_info.setTextColor(Color.GRAY)
                        return
                    }
                    email.checkEmail() -> {
                        change_info_email_info.text = "邮箱格式正确"
                        change_info_email_info.setTextColor(Color.GREEN)
                    }
                    else -> {
                        change_info_email_info.text = "邮箱格式不合法"
                        change_info_email_info.setTextColor(Color.RED)
                    }
                }
                change_info_email_info.visibility = View.VISIBLE
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        change_info_school.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val school = s.toString()
                when {
                    info == null -> {
                        change_info_school_info.text = "用户验证失败"
                        change_info_school_info.setTextColor(Color.RED)
                    }
                    school == info?.school -> {
                        change_info_school_info.text = "学校与原来相同，无需改变"
                        change_info_school_info.setTextColor(Color.GRAY)
                        return
                    }
                    school.checkSchool() -> {
                        change_info_school_info.text = "学校名称符合要求"
                        change_info_school_info.setTextColor(Color.GREEN)
                    }
                    else -> {
                        change_info_school_info.text = "学校名称不符合要求(长度)"
                        change_info_school_info.setTextColor(Color.RED)
                    }
                }
                change_info_school_info.visibility = View.VISIBLE
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        })
        change_info_signature.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val signature = s.toString()
                when {
                    info == null -> {
                        change_info_signature_info.text = "用户验证失败"
                        change_info_signature_info.setTextColor(Color.RED)
                    }
                    signature == info?.signature -> {
                        change_info_signature_info.text = "签名与原来相同，无需更新"
                        change_info_signature_info.setTextColor(Color.GRAY)
                        return
                    }
                    !(2..20).contains(signature.length) -> {
                        change_info_signature_info.text = "签名长度不符合要求"
                        change_info_signature_info.setTextColor(Color.RED)
                    }
                    else -> {
                        change_info_signature_info.text = "签名符合要求"
                        change_info_signature_info.setTextColor(Color.GREEN)
                    }
                }
                change_info_signature_info.visibility = View.VISIBLE
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        change_info_submit.setOnClickListener(this)
        change_info_portrait.setOnClickListener(this)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                RESULT_LOAD_IMAGE -> {
                    val selectedImage = data.data!!
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor = this.contentResolver?.query(selectedImage, filePathColumn, null, null, null)
                    if (cursor != null && cursor.moveToFirst() && cursor.count > 0) {
                        val path = cursor.getString(cursor.getColumnIndex(filePathColumn[0]))
                        val bitmap = BitmapUtil.load(path, true).cropCenter().zoom(512, 512)
                        cursor.close()
                        updatePortrait(bitmap)
                    }
                }
            }
        }
    }

    private fun updatePortrait(bitmap: Bitmap) {

        User.find(this)?.let {

            val file = arrayListOf(IO.bitmap2FileCache(this, bitmap, 95))
            AsyncNetUtils.postMultipleForm(CONF.API.user.changePortrait, Params.changePortrait(it), file, object : AsyncNetUtils.Callback {
                fun onFail(msg: String = "网络异常"): Int {
                    Toast.makeText(this@ChangeInfoActivity, msg, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, msg)
                    return 1
                }

                fun onSuccess(): Int {
                    Log.d(TAG, "portrait changed")
                    change_info_portrait.setImageBitmap(bitmap)
                    Toast.makeText(this@ChangeInfoActivity, "修改头像成功", Toast.LENGTH_SHORT).show()
                    return 0
                }
                override fun onResponse(result: String?): Int {
                    val message = Message.Msg.parse(result)
                    return if (message != null) {
                        when(message.shortcut) {
                            Shortcut.OK -> onSuccess()
                            Shortcut.UNE -> {
                                User.reset(this@ChangeInfoActivity)
                                onFail("用户出现异常")
                            }
                            Shortcut.TE -> {
                                User.reset(this@ChangeInfoActivity)
                                onFail("登录已过期, 请重新登录")
                            }
                            else -> onFail("shortcut异常")
                        }
                    } else {
                        onFail("解析异常")
                    }
                }

                override fun onFailure(msg: String): Int {
                    return onFail()
                }

            }, "portrait")

        }

    }

    companion object {
        const val TAG = "RegisterActivity"
        const val RESULT_LOAD_IMAGE = 10
        const val RESULT_CROP_IMAGE = 20

        private fun String.checkNickname(): Boolean {
            return Pattern.matches("^[a-zA-Z0-9\\u4e00-\\u9fa5]+$", this)
        }
        private fun String.checkEmail(): Boolean {
            return Pattern.matches("^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+\$", this)
        }
        private fun String.checkSchool(): Boolean {
            // todo check school
            return (3..15).contains(this.length)
        }

    }
}
