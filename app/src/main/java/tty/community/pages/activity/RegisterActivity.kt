package tty.community.pages.activity

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_register.*
import tty.community.R
import tty.community.model.Params
import tty.community.model.Shortcut
import tty.community.model.User
import tty.community.network.AsyncNetUtils
import tty.community.util.CONF
import tty.community.util.Message
import tty.community.util.Util.getMD5
import tty.community.widget.AlertDialogUtil
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_submit.setOnClickListener {
            val nickname = register_nickname.text.toString()
            val email = register_email.text.trim().toString()
            val password = register_password.text.toString()
            val confirmPassword = register_confirm_password.text.toString()

            if (!(2..15).contains(nickname.length)) {
                Toast.makeText(this, "昵称长度应在2~15位", Toast.LENGTH_SHORT).show()
                register_nickname.requestFocus()
                return@setOnClickListener
            }

            if (!nickname.checkNickname()) {
                Toast.makeText(this, "昵称只允许包含中文, 数字, 字母", Toast.LENGTH_SHORT).show()
                register_nickname.requestFocus()
                return@setOnClickListener
            }

            if (!email.checkEmail()) {
                Toast.makeText(this, "邮箱不合法", Toast.LENGTH_SHORT).show()
                register_email.requestFocus()
                return@setOnClickListener
            }

            if (!(6..20).contains(password.length)) {
                Toast.makeText(this, "密码长度应在6~20位", Toast.LENGTH_SHORT).show()
                register_password.requestFocus()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show()
                register_confirm_password.requestFocus()
                return@setOnClickListener
            }

            AsyncNetUtils.post(CONF.API.user.register, Params.register(User.Register(nickname, email, getMD5(password))), object : AsyncNetUtils.Callback {
                fun onFail(msg: String): Int {
                    Log.e(TAG, msg)
                    Toast.makeText(this@RegisterActivity, msg, Toast.LENGTH_SHORT).show()
                    return 1
                }

                fun onSuccess(): Int {
                    AlertDialogUtil.registerSuccessDialog(this@RegisterActivity, nickname)
                    return 0
                }

                override fun onFailure(msg: String): Int {
                    return onFail(msg)
                }

                override fun onResponse(result: String?): Int {
                    val message: Message.Msg? = Message.Msg.parse(result)
                    return if (message != null) {
                        when (message.shortcut) {
                            Shortcut.OK -> onSuccess()
                            Shortcut.AIF -> onFail("昵称或邮箱格式不符合要求")
                            Shortcut.UR -> onFail("昵称 $nickname 已经被注册了")
                            else -> onFail("shortcut异常")
                        }
                    } else {
                        onFail("解析异常")
                    }
                }
            })

        }

        register_nickname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val nickname = p0.toString()
                if (!(2..15).contains(nickname.length)) {
                    register_nickname_info.text = "昵称 $nickname 长度不符合要求"
                    register_nickname_info.setTextColor(Color.RED)
                    return
                }

                if (!nickname.checkNickname()) {
                    register_nickname_info.text = "昵称只允许包含中文, 数字, 字母"
                    register_nickname_info.setTextColor(Color.RED)
                    return
                }

                val url = CONF.API.user.checkName
                val map = hashMapOf(Pair("nickname", nickname))
                AsyncNetUtils.post(url, map, object : AsyncNetUtils.Callback {
                    fun onFail(msg: String): Int {
                        register_nickname_info.text = msg
                        register_nickname_info.setTextColor(Color.RED)
                        return 1
                    }

                    fun onSuccess(msg: String): Int {
                        register_nickname_info.text = msg
                        register_nickname_info.setTextColor(Color.GREEN)
                        return 0
                    }

                    override fun onFailure(msg: String): Int {
                        return onFail(msg)
                    }

                    override fun onResponse(result: String?): Int {
                        val message: Message.Msg? = Message.Msg.parse(result)
                        return if (message != null) {
                            when(message.shortcut) {
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

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        })

        register_email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val email = register_email.text.toString()
                if (!email.checkEmail()) {
                    register_email_info.text = "邮箱格式不合法"
                    register_email_info.setTextColor(Color.RED)
                } else {
                    register_email_info.text = "邮箱格式正确"
                    register_email_info.setTextColor(Color.GREEN)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        })

        register_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val password = register_password.text.trim().toString()
                val confirmPassword = register_confirm_password.text.toString()
                if (!(6..20).contains(password.length)) {
                    register_password_info.text = "密码长度应在6~20位"
                    register_password_info.setTextColor(Color.RED)
                } else if (password != confirmPassword) {
                    register_password_info.text = "两次密码不一致"
                    register_password_info.setTextColor(Color.RED)
                } else {
                    register_password_info.text = "密码符合要求"
                    register_password_info.setTextColor(Color.GREEN)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        })

        register_confirm_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val password = register_password.text.toString()
                val confirmPassword = register_confirm_password.text.toString()

                if (password != confirmPassword) {
                    register_password_info.text = "两次密码不一致"
                    register_password_info.setTextColor(Color.RED)
                } else if (!(6..20).contains(password.length)) {
                    register_password_info.text = "密码长度应在6~20位"
                    register_password_info.setTextColor(Color.RED)
                } else {
                    register_password_info.text = "密码符合要求"
                    register_password_info.setTextColor(Color.GREEN)
                }
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
            return Pattern.matches(
                "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+\$",
                this
            )
        }
    }
}
