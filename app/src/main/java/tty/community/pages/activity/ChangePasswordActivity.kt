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
import org.json.JSONObject
import tty.community.R
import tty.community.model.Shortcut
import tty.community.network.AsyncTaskUtil
import tty.community.values.Const
import tty.community.values.Util

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
            val url = Const.api[Const.Route.User] + "/change_password"
            AsyncTaskUtil.AsyncNetUtils.post(
                url,
                map,
                object : AsyncTaskUtil.AsyncNetUtils.Callback {
                    override fun onResponse(response: String) {
                        Log.d(TAG, response)
                        val result = JSONObject(response)
                        when (Shortcut.phrase(result.optString("shortcut"))) {
                            Shortcut.OK -> {
                                Toast.makeText(
                                    this@ChangePasswordActivity,
                                    "更改密码成功，请重新登录",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(
                                    Intent(
                                        this@ChangePasswordActivity,
                                        LoginActivity::class.java
                                    )
                                )
                                finish()
                            }

                            Shortcut.UPE -> {
                                Toast.makeText(
                                    this@ChangePasswordActivity,
                                    "原密码错误",
                                    Toast.LENGTH_SHORT
                                ).show()
                                change_password_old_password.requestFocus()
                            }

                            Shortcut.UNE -> {
                                Toast.makeText(
                                    this@ChangePasswordActivity,
                                    "当前账户未被注册",
                                    Toast.LENGTH_SHORT
                                ).show()
                                change_password_id.requestFocus()
                            }

                            else -> {
                                Toast.makeText(
                                    this@ChangePasswordActivity,
                                    "未知错误",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    }

                })
        }
    }

    companion object {
        const val TAG = "ChangePasswordActivity"
    }
}
