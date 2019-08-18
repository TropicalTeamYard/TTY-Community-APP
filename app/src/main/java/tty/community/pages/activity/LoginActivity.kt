package tty.community.pages.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_login.*
import tty.community.R
import tty.community.model.Params
import tty.community.model.Shortcut
import tty.community.model.User
import tty.community.network.AsyncNetUtils
import tty.community.network.AsyncNetUtils.Callback
import tty.community.pages.activity.LoginActivity.Companion.LoginType.*
import tty.community.util.CONF
import tty.community.util.Message

class LoginActivity : AppCompatActivity() {

    private var loginType = NICKNAME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login_btn.setOnClickListener {
            val account = login_account.text.toString()
            val password = login_password.text.toString()
            if (account.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "账号/密码不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            login(Params.login(account, password, loginType))
        }

        login_change_method.setOnClickListener {
            loginType = when (loginType) {
                NICKNAME -> ID
                ID -> NICKNAME
            }
            login_method.text = loginType.text
        }

        login_register_btn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login(params: HashMap<String, String>) {
        AsyncNetUtils.post(CONF.API.user.login, params, object : Callback {
            fun onLoginFail(msg: String): Int {
                Log.e(TAG, msg)
                Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()
                return 1
            }

            fun onSuccess(user: User): Int {
                user.login(this@LoginActivity)
                Toast.makeText(this@LoginActivity, "欢迎${user.nickname}!", Toast.LENGTH_SHORT).show()
                finish()
                return 0
            }

            fun onRunFail(msg: String = "未知错误"): Int {
                Log.e(TAG, msg)
                Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()
                return 1
            }

            override fun onFailure(msg: String): Int {
                return onRunFail(msg)
            }

            override fun onResponse(result: String?): Int {
                val message: Message.MsgData<User>? = Message.MsgData.parse(result, object : TypeToken<Message.MsgData<User>>(){})
                return if (message != null) {
                    when(message.shortcut) {
                        Shortcut.OK -> onSuccess(message.data)
                        Shortcut.UNE -> onLoginFail("用户不存在")
                        Shortcut.UPE -> onLoginFail("密码错误")
                        else -> onRunFail("shortcut异常")
                    }

                } else {
                    onRunFail("解析异常")
                }
            }

        })
    }

    companion object {
        const val TAG = "LoginActivity"
        enum class LoginType {
            ID, NICKNAME;

            val key: String
                get() {
                    return when (this) {
                        ID -> "id"
                        NICKNAME -> "nickname"
                    }
                }

            val text: String
                get() {
                    return when (this) {
                        ID -> "账    号"
                        NICKNAME -> "用 户 名"
                    }
                }
        }
    }
}
