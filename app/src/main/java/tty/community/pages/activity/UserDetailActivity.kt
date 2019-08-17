package tty.community.pages.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.JsonParser
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.activity_user_detail.*
import tty.community.R
import tty.community.database.MainDBHelper
import tty.community.image.BitmapUtil
import tty.community.model.Shortcut
import tty.community.model.user.User
import tty.community.network.AsyncNetUtils
import tty.community.values.CONF

class UserDetailActivity : AppCompatActivity(), View.OnClickListener, OnRefreshListener {
    override fun onRefresh(refreshLayout: RefreshLayout) {
        user?.let {
            setData(it)
            return
        }

        Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show()
        user_detail_refresh.finishRefresh(100)

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.user_detail_change_password -> {
                startActivity(Intent(this, ChangePasswordActivity::class.java))
            }
        }
    }

    val url = CONF.API.user.info
    private val map = HashMap<String, String>()

    private var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
        user_detail_change_password.setOnClickListener(this)
        user_detail_refresh.setOnRefreshListener(this)
    }

    override fun onResume() {
        super.onResume()
        user = MainDBHelper(this).findUser()
        user?.let { setData(it) }
    }

    private fun setData(user: User) {
        user.let {
            map["id"] = it.id
            map["token"] = it.token
            AsyncNetUtils.post(url, map, object : AsyncNetUtils.Callback {

                fun onFail(msg: String) {
                    Log.e(TAG, msg)
                    user_detail_refresh.finishRefresh(false)
                    Toast.makeText(this@UserDetailActivity, msg, Toast.LENGTH_SHORT).show()
                }

                fun onSuccess() {
                    user_detail_refresh.finishRefresh()
                }

                override fun onFailure(msg: String) {
                    onFail(msg)
                }

                override fun onResponse(result: String?) {
                    result?.let { it1 ->
                        Log.d(TAG, it1)
                        val element = JsonParser().parse(it1)
                        if (element.isJsonObject) {
                            val obj = element.asJsonObject
                            when (Shortcut.parse(obj["shortcut"].asString)) {
                                Shortcut.OK -> {
                                    val data = obj["data"].asJsonObject
                                    val url = CONF.API.public.portrait + "?" + "id=${it.id}"
                                    user_detail_id.text = data["id"].asString
                                    user_detail_nickname.text = data["nickname"].asString
                                    user_detail_email.text = data["email"].asString
                                    user_detail_signature.text = data["signature"].asString
                                    user_detail_exp.text = data["exp"].asInt.toString()
                                    user_detail_user_group.text = data["userGroup"].asString
                                    Glide.with(this@UserDetailActivity).load(url).apply(BitmapUtil.optionsNoCache()).centerCrop().into(user_detail_portrait)
                                    onSuccess()
                                }
                                Shortcut.UNE -> {
                                    onFail("用户不存在")
                                }
                                Shortcut.TE -> {
                                    onFail("登录已过期，请重新登录")
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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_deatil_user_info, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.change_info -> {
                user?.let {
                    startActivity(Intent(this, ChangeInfoActivity::class.java))
                }
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val TAG = "UserDetailActivity"
    }
}
