package tty.community.pages.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_login.*
import tty.community.R
import tty.community.database.MainDBHelper
import tty.community.model.Shortcut
import tty.community.model.user.Login
import tty.community.network.AsyncNetUtils
import tty.community.network.AsyncNetUtils.Callback
import tty.community.values.Const
import tty.community.values.Util.getMD5

class LoginActivity : AppCompatActivity() {

    private val map = HashMap<String, String>()
    private var loginType = "nickname"

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
            map["type"] = loginType
            map[loginType] = account
            map["password"] = getMD5(password)
            map["platform"] = "mobile"
            login()
        }

        login_change_method.setOnClickListener {
            if (loginType == "nickname") {
                loginType = "id"
                login_method.text = "账    号"
            } else {
                loginType = "nickname"
                login_method.text = "用 户 名"
            }
        }

        login_register_btn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    private fun login() {
        val url = Const.api[Const.Route.User] + "/" + "login"
        AsyncNetUtils.post(url, map, object : Callback {
            override fun onFailure(msg: String) {
                onFail(msg)
            }

            fun onFail(msg: String) {
                Log.e(TAG, msg)
                Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()
            }

            fun onSuccess() {
                Toast.makeText(this@LoginActivity, "登录成功，即将跳转主界面", Toast.LENGTH_SHORT).show()
                finish()
            }

            override fun onResponse(result: String?) {
                result?.let { it ->
                    Log.d(TAG, it)
                    val element = JsonParser().parse(it)
                    if (element.isJsonObject) {
                        val obj = element.asJsonObject
                        when (Shortcut.parse(obj["shortcut"].asString)) {
                            Shortcut.OK -> {
                                val data = obj["data"].asJsonObject
                                val id = data["id"].asString
                                val nickname = data["nickname"].asString
                                val email = data["email"].asString
                                val token = data["token"].asString
                                val values = Login(id, nickname, token, email).values
                                if (values != null) {
                                    MainDBHelper(this@LoginActivity).login(values)
                                    onSuccess()
                                } else {
                                    onFail("返回参数出错")
                                }
                            }
                            Shortcut.UNE -> {
                                onFail("用户不存在")
                            }
                            Shortcut.UPE -> {
                                onFail("密码错误")
                            }
                            else -> {
                                onFail("未知错误")
                            }
                        }
                    }
                    return
                }

                onFail("网络异常")
            }

        })
    }

    companion object {
        const val TAG = "LoginActivity"
    }
}
