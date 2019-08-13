package tty.community.pages.activity

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONObject
import tty.community.R
import tty.community.model.user.Register
import tty.community.model.Shortcut
import tty.community.network.AsyncTaskUtil
import tty.community.values.Util.getMD5
import tty.community.values.Value
import tty.community.widget.AlertDialogUtil
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_submit.setOnClickListener {
            val nickname = register_nickname.text.trim().toString()
            val email = register_email.text.trim().toString()
            val password = register_password.text.trim().toString()
            val confirmPassword = register_confirm_password.text.trim().toString()
            if (!(2..15).contains(nickname.length)) {
                Toast.makeText(this, "昵称长度应在2~15位", Toast.LENGTH_SHORT).show()
                register_nickname.requestFocus()
                return@setOnClickListener
            }

            val match = Pattern.matches("^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+\$", email)
            if (!match) {
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

            val url = Value.api["user"] + "/register"
            val map = Register(nickname, email, getMD5(password)).getMap()
            AsyncTaskUtil.AsyncNetUtils.post(url, map, object : AsyncTaskUtil.AsyncNetUtils.Callback {
                override fun onResponse(response: String) {
                    Log.d(MainActivity.TAG, response)
                    val result = JSONObject(response)
                    val msg = result.optString("msg")
                    when(Shortcut.phrase(result.optString("shortcut", "UNKNOWN"))) {
                        Shortcut.UR -> {
                            Toast.makeText(this@RegisterActivity, "昵称 `$nickname` 已经被注册了", Toast.LENGTH_SHORT).show()
                        }

                        Shortcut.OK -> {
                            AlertDialogUtil.registerResultDialog(this@RegisterActivity, nickname)
                        }

                        else -> {
                            Toast.makeText(this@RegisterActivity, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })


        }

        register_nickname.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                val nickname = p0?.trim().toString()
                if(!(2..15).contains(nickname.length)) {
                    register_nickname_info.text = "昵称 `$nickname` 长度不符合要求"
                    register_nickname_info.setTextColor(Color.RED)
                    return
                }
                val url = "${Value.api["user"]}/check_name"
                val map = hashMapOf(Pair("nickname", nickname))
                AsyncTaskUtil.AsyncNetUtils.post(url, map, object : AsyncTaskUtil.AsyncNetUtils.Callback {
                    override fun onResponse(response: String) {
                        Log.d(MainActivity.TAG, response)
                        val result = JSONObject(response)
                        when(Shortcut.phrase(result.optString("shortcut", "UNKNOWN"))) {
                            Shortcut.OK -> {
                                register_nickname_info.text = "昵称 `$nickname` 可以使用"
                                register_nickname_info.setTextColor(Color.GREEN)
                            }

                            Shortcut.UR -> {
                                register_nickname_info.text = "昵称 `$nickname` 已经被注册了"
                                register_nickname_info.setTextColor(Color.RED)
                            }

                            else -> {
                                register_nickname_info.text = "检查失败，未知异常"
                                register_nickname_info.setTextColor(Color.YELLOW)
                            }
                        }
                    }
                })
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        register_email.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                val email = register_email.text.trim().toString()
                val match = Pattern.matches("^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+\$", email)
                if (!match) {
                    register_email_info.text = "邮箱格式不合法"
                    register_email_info.setTextColor(Color.RED)
                } else {
                    register_email_info.text = "邮箱格式正确"
                    register_email_info.setTextColor(Color.GREEN)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })


        register_password.addTextChangedListener(object : TextWatcher{
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

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        register_confirm_password.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                val password = register_password.text.trim().toString()
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

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })
    }
}
