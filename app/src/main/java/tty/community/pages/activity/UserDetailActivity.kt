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
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.activity_user_detail.*
import org.json.JSONObject
import tty.community.R
import tty.community.database.MainDBHelper
import tty.community.image.BitmapUtil
import tty.community.model.Shortcut
import tty.community.model.user.User
import tty.community.network.AsyncTaskUtil
import tty.community.values.Const

class UserDetailActivity : AppCompatActivity(), View.OnClickListener, OnRefreshListener {
    override fun onRefresh(refreshLayout: RefreshLayout) {
        user?.let { setData(it) }

        if (user == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show()
            user_detail_refresh.finishRefresh(100)
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.user_detail_change_password -> {
                startActivity(Intent(this, ChangePasswordActivity::class.java))
            }
        }
    }

    val url = Const.api[Const.Route.User] + "/info"
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
            AsyncTaskUtil.AsyncNetUtils.post(
                url,
                map,
                object : AsyncTaskUtil.AsyncNetUtils.Callback {
                    override fun onResponse(response: String) {
                        Log.d(TAG, response)
                        val result = JSONObject(response)
                        val msg = result.optString("msg", "unknown error")
                        when (Shortcut.phrase(result.optString("shortcut", "UNKNOWN"))) {
                            Shortcut.OK -> {
                                val data = result.optJSONObject("data")
                                val url =
                                    Const.api[Const.Route.PublicUser] + "/portrait?target=${it.id}"
                                user_detail_id.text = data?.optString("id") ?: ""
                                user_detail_nickname.text = data?.optString("nickname") ?: ""
                                user_detail_email.text = data?.optString("email") ?: ""
                                user_detail_signature.text = data?.optString("signature") ?: ""
                                user_detail_exp.text = data?.optString("exp") ?: ""
                                user_detail_user_group.text = data?.optString("userGroup") ?: ""
                                Glide.with(this@UserDetailActivity).load(url)
                                    .apply(BitmapUtil.optionsNoCache()).into(user_detail_portrait)
                                user_detail_refresh.finishRefresh(true)
                            }

                            else -> {
                                Toast.makeText(this@UserDetailActivity, msg, Toast.LENGTH_SHORT)
                                    .show()
                                user_detail_refresh.finishRefresh(false)
                            }
                        }
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
