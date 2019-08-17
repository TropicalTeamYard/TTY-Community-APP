package tty.community.pages.activity

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_main.*
import tty.community.R
import tty.community.adapter.MainFragmentAdapter
import tty.community.database.MainDBHelper
import tty.community.model.Shortcut
import tty.community.network.AsyncNetUtils
import tty.community.values.CONF

class MainActivity : AppCompatActivity(), ViewPager.OnPageChangeListener,
    BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_home -> {
                main_viewPager.currentItem = 0
                true
            }
            R.id.nav_chat -> {
                main_viewPager.currentItem = 1
                true
            }
            R.id.nav_me -> {
                main_viewPager.currentItem = 2
                true
            }
            else -> false
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        main_nav.selectedItemId = when (position) {
            0 -> R.id.nav_home
            1 -> R.id.nav_chat
            2 -> R.id.nav_me
            else -> return
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setAdapter()

    }

    override fun onResume() {
        super.onResume()
        autoLogin()

    }

    private fun autoLogin() {
        val user = MainDBHelper(this).findUser()
        if (user == null) {
            Toast.makeText(this, "您还未登录账号，请先登录", Toast.LENGTH_SHORT).show()
        } else {
            val url = CONF.API.user.autoLogin
            val map = HashMap<String, String>()
            map["id"] = user.id
            map["token"] = user.token
            map["platform"] = "mobile"
            AsyncNetUtils.post(url, map, object : AsyncNetUtils.Callback {
                fun onFail(msg: String) {
                    Log.e(TAG, msg)
                    Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(msg: String) {
                    onFail(msg)
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
                                    val values = ContentValues()
                                    values.put("email", data["email"].asString)
                                    values.put("nickname", data["nickname"].asString)
                                    MainDBHelper(this@MainActivity).updateUser(user.id, values)
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

    private fun setAdapter() {
        val adapter = MainFragmentAdapter(supportFragmentManager)
        main_viewPager.adapter = adapter
        main_viewPager.addOnPageChangeListener(this)
        main_nav.setOnNavigationItemSelectedListener(this)
    }

    companion object {
        const val TAG = "MainActivity"
    }


}
