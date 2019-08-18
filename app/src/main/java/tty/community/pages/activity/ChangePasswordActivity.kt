package tty.community.pages.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_change_password.*
import tty.community.network.AsyncNetUtils.Callback
import tty.community.R
import tty.community.model.Params
import tty.community.model.Shortcut
import tty.community.network.AsyncNetUtils
import tty.community.util.CONF
import tty.community.util.Message

class ChangePasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        change_password_new_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val password = change_password_new_password.text.trim().toString()
                val confirmPassword = change_password_confirm.text.toString()
                if (!(6..20).contains(password.length)) {
                    change_password_info.text = "密码长度应在6~20位"
                    change_password_info.setTextColor(Color.RED)
                } else if (password != confirmPassword) {
                    change_password_info.text = "两次密码不一致"
                    change_password_info.setTextColor(Color.RED)
                } else {
                    change_password_info.text = "密码符合要求"
                    change_password_info.setTextColor(Color.GREEN)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        })

        change_password_confirm.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val password = change_password_new_password.text.toString()
                val confirmPassword = change_password_confirm.text.toString()

                if (password != confirmPassword) {
                    change_password_info.text = "两次密码不一致"
                    change_password_info.setTextColor(Color.RED)
                } else if (!(6..20).contains(password.length)) {
                    change_password_info.text = "密码长度应在6~20位"
                    change_password_info.setTextColor(Color.RED)
                } else {
                    change_password_info.text = "密码符合要求"
                    change_password_info.setTextColor(Color.GREEN)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        })

        change_password_submit.setOnClickListener {

            val id = change_password_id.text.trim().toString()
            val oldPassword = change_password_old_password.text.toString()
            val newPassword = change_password_new_password.text.toString()
            val confirm = change_password_confirm.text.toString()

            if (id.isEmpty() || oldPassword.isEmpty()) {
                Toast.makeText(this, "账号/密码不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!(6..20).contains(newPassword.length)) {
                Toast.makeText(this, "密码长度应在6~20位", Toast.LENGTH_SHORT).show()
                change_password_new_password.requestFocus()
                return@setOnClickListener
            }

            if (newPassword != confirm) {
                Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show()
                change_password_confirm.requestFocus()
                return@setOnClickListener
            }

            AsyncNetUtils.post(CONF.API.user.changePassword, Params.changePassword(id, oldPassword, newPassword), object : Callback {
                fun onResult(msg: String): Int {
                    Log.e(TAG, msg)
                    Toast.makeText(this@ChangePasswordActivity, msg, Toast.LENGTH_SHORT).show()
                    return 0
                }

                override fun onFailure(msg: String): Int {
                    return onResult(msg)
                }

                override fun onResponse(result: String?): Int {
                    val message: Message.Msg? = Message.Msg.parse(result)
                    return if (message != null) {
                        when (message.shortcut) {
                            Shortcut.OK -> {
                                startActivity(Intent(this@ChangePasswordActivity, LoginActivity::class.java))
                                finish()
                                onResult("修改密码成功")
                            }
                            Shortcut.UNE -> {
                                change_password_id.requestFocus()
                                onResult("用户不存在")
                            }
                            Shortcut.UPE -> {
                                change_password_old_password.requestFocus()
                                onResult("密码错误")
                            }
                            else -> onResult("shortcut异常")
                        }
                    } else {
                        onResult("解析异常")
                    }
                }
            })
        }
    }



    companion object {
        const val TAG = "ChangePasswordActivity"
    }
}
