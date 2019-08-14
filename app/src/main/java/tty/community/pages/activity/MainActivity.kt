package tty.community.pages.activity

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import tty.community.R
import tty.community.adapter.MainFragmentAdapter
import tty.community.database.MainDBHelper
import tty.community.model.Shortcut
import tty.community.network.AsyncTaskUtil
import tty.community.values.Value

class MainActivity : AppCompatActivity(), ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.nav_home -> {
                main_viewPager.currentItem = 0
                true
            }
            R.id.nav_square -> {
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

    override fun onPageScrollStateChanged(state: Int) { }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }

    override fun onPageSelected(position: Int) {
        main_nav.selectedItemId = when (position) {
            0 -> R.id.nav_home
            1 -> R.id.nav_square
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
            val url = Value.api[Value.Route.User] + "/auto_login"
            val map = HashMap<String, String>()
            map["id"] = user.id
            map["token"] = user.token
            map["platform"] = "mobile"
            AsyncTaskUtil.AsyncNetUtils.post(url, map, object : AsyncTaskUtil.AsyncNetUtils.Callback {
                override fun onResponse(response: String) {
                    Log.d(TAG, response)
                    val result = JSONObject(response)
                    val msg = result.optString("msg", "unknown error")
                    when(Shortcut.phrase(result.optString("shortcut", "UNKNOWN"))) {
                        Shortcut.OK -> {
                            val data = result.getJSONObject("data")
                            val values = ContentValues()
                            values.put("email", data.getString("email"))
                            values.put("nickname", data.getString("nickname"))
                            MainDBHelper(this@MainActivity).updateUser(user.id, values)
                        }

                        else -> {
                            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }
    }

    private fun setAdapter() {
        val adapter = MainFragmentAdapter(supportFragmentManager)
        main_viewPager.adapter = adapter
        main_viewPager.addOnPageChangeListener(this)
//        main_viewPager.setScroll(true)
        main_nav.setOnNavigationItemSelectedListener(this)
    }

    companion object {
        const val TAG = "MainActivity"
    }


}
