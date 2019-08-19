package tty.community.pages.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_change_info.*
import tty.community.R
import tty.community.image.BitmapUtil
import tty.community.model.Params
import tty.community.model.Shortcut
import tty.community.model.User
import tty.community.network.AsyncNetUtils
import tty.community.network.NetMessage
import tty.community.network.NetMessage.post
import tty.community.util.CONF
import tty.community.util.Message
import java.util.regex.Pattern

class ChangeInfoActivity : AppCompatActivity() {

    var info: User.PrivateInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_info)
        val user = User.find(this)

        if (user != null) {
            post(CONF.API.user.info, Params.privateInfo(user), object : TypeToken<Message.MsgData<User.PrivateInfo>>(){}, object : NetMessage.Callback {
                fun onFail(msg: String): Int {
                    Toast.makeText(this@ChangeInfoActivity, msg, Toast.LENGTH_SHORT).show()
                    return 1
                }

                fun onSuccess(info: User.PrivateInfo): Int {
                    Glide.with(this@ChangeInfoActivity).load(CONF.API.public.portrait + "?" + "id=${user.id}").apply(
                        BitmapUtil.optionsNoCache()).centerCrop().into(change_info_portrait)
                    this@ChangeInfoActivity.info = info
                    change_info_nickname.setText(info.nickname)
                    change_info_email.setText(info.email)
                    change_info_school.setText(info.school)
                    change_info_signature.setText(info.signature)
                    return 0
                }

                override fun <T> result(message: T?): Int {
                    return if(message is Message.MsgData<*>) {
                        when(message.shortcut) {
                            Shortcut.OK -> {
                                if(message.data is User.PrivateInfo) {
                                    onSuccess(message.data as User.PrivateInfo)
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
                            else -> {
                                onFail("shortcut异常")
                            }
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
                        val url = CONF.API.user.checkName
                        val map = hashMapOf(Pair("nickname", nickname))
                        AsyncNetUtils.post(url, map, object : AsyncNetUtils.Callback {
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
                    !(3..15).contains(school.length) -> {
                        change_info_school_info.text = "学校名称长度不符合要求"
                        change_info_school_info.setTextColor(Color.RED)
                    }
                    school.checkSchool() -> {
                        change_info_school_info.text = "学校名称验证成功"
                        change_info_school_info.setTextColor(Color.GREEN)
                    }
                    else -> {
                        change_info_school_info.text = "学校名称验证失败"
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
                    !(3..15).contains(signature.length) -> {
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

    }

    companion object {
        const val TAG = "RegisterActivity"
        private fun String.checkNickname(): Boolean {
            return Pattern.matches("^[a-zA-Z0-9\\u4e00-\\u9fa5]+$", this)
        }

        private fun String.checkEmail(): Boolean {
            return Pattern.matches("^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+\$", this)
        }

        private fun String.checkSchool(): Boolean {
            // todo check school
            return true
        }
    }
}
