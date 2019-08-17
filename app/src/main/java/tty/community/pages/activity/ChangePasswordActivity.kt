package tty.community.pages.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_change_password.*
import tty.community.network.AsyncNetUtils.Callback
import tty.community.R
import tty.community.model.Shortcut
import tty.community.network.AsyncNetUtils
import tty.community.values.CONF
import tty.community.values.Util
import java.lang.Exception

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

            val map = HashMap<String, String>()
            map["id"] = id
            map["old"] = Util.getMD5(oldPassword)
            map["new"] = Util.getMD5(newPassword)

            AsyncNetUtils.post(CONF.API.user.changePassword, map, object : Callback {
                fun onResult(msg: String) {
                    Toast.makeText(this@ChangePasswordActivity, msg, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, msg)
                }

                override fun onFailure(msg: String) {
                    onResult(msg)
                }

                override fun onResponse(result: String?) {
                    try {
                        result?.let {
                            Log.d(TAG, it)
                            val obj = JsonParser().parse(it).asJsonObject
                            when (Shortcut.parse(obj["shortcut"].asString)) {
                                Shortcut.OK -> {
                                    onResult("修改密码成功")
                                    startActivity(Intent(this@ChangePasswordActivity, LoginActivity::class.java))
                                    finish()
                                }
                                Shortcut.UNE -> {
                                    onResult("用户不存在")
                                    change_password_id.requestFocus()
                                }
                                Shortcut.UPE -> {
                                    onResult("密码错误")
                                    change_password_old_password.requestFocus()
                                }
                                else -> {
                                    onResult("未知错误")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        onResult("返回数据异常")
                    }
                }
            })
        }
    }

    companion object {
        const val TAG = "ChangePasswordActivity"
    }
}
