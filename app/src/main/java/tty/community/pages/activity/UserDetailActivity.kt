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
import com.google.gson.reflect.TypeToken
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.activity_user_detail.*
import tty.community.R
import tty.community.image.BitmapUtil
import tty.community.model.Params
import tty.community.model.Shortcut
import tty.community.model.User
import tty.community.network.AsyncNetUtils
import tty.community.util.CONF
import tty.community.util.Message

class UserDetailActivity : AppCompatActivity(), View.OnClickListener, OnRefreshListener {
    override fun onRefresh(refreshLayout: RefreshLayout) {
        val user = User.find(this)

        if (user != null) {
            refresh(user)
        } else {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.user_detail_change_password -> {
                startActivity(Intent(this, ChangePasswordActivity::class.java))
            }
        }
    }

    private var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
        user_detail_change_password.setOnClickListener(this)
        user_detail_refresh.setOnRefreshListener(this)
    }

    override fun onResume() {
        super.onResume()
        user_detail_refresh.autoRefresh()
    }

    private fun refresh(user: User) {
        AsyncNetUtils.post(CONF.API.user.info, Params.privateInfo(user), object : AsyncNetUtils.Callback {
            fun onFail(msg: String): Int {
                Log.e(TAG, msg)
                Toast.makeText(this@UserDetailActivity, msg, Toast.LENGTH_SHORT).show()
                user_detail_refresh.finishRefresh(false)
                return 1
            }

            fun onSuccess(): Int {
                user_detail_refresh.finishRefresh(true)
                return 0
            }

            override fun onFailure(msg: String): Int {
                return onFail(msg)
            }

            override fun onResponse(result: String?): Int {
                val message: Message.MsgData<User.PrivateInfo>? = Message.MsgData.parse(result, object : TypeToken<Message.MsgData<User.PrivateInfo>>(){})
                if (message != null) {
                    when (message.shortcut) {
                        Shortcut.OK -> {
                            val info = message.data
                            val url = CONF.API.public.portrait + "?" + "id=${info.id}"
                            user_detail_id.text = info.id
                            user_detail_nickname.text = info.nickname
                            user_detail_email.text = info.email
                            user_detail_signature.text = info.signature
                            user_detail_exp.text = info.exp.toString()
                            user_detail_user_group.text = info.userGroup.toString()
                            Glide.with(this@UserDetailActivity).load(url).apply(BitmapUtil.optionsNoCachePortraitDefaultUser()).centerCrop().into(user_detail_portrait)
                            return onSuccess()
                        }
                        Shortcut.UNE -> return onFail("用户不存在")
                        Shortcut.TE -> return onFail("登录已过期，请重新登录")
                        else -> return onFail("shortcut异常")
                    }
                } else {
                    return onFail("解析异常")
                }
            }
        })
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
