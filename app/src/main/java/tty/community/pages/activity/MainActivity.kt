package tty.community.pages.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_user.*
import tty.community.R
import tty.community.adapter.MainFragmentAdapter
import tty.community.adapter.SimplePageFragmentAdapter
import tty.community.file.Storage
import tty.community.image.BitmapUtil
import tty.community.model.User
import tty.community.pages.fragment.UserFragment
import tty.community.util.CONF
import java.io.File

class MainActivity : AppCompatActivity(), UserFragment.OnUserInteraction{
    override fun onUserRefreshed(user: User) {
        val mainFragment = adapter.getItem(1) as MainFragment
        mainFragment.refresh(user)
    }

    private lateinit var intents: Array<Intent>
    private lateinit var adapter:SimplePageFragmentAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }


    private fun init() {
        adapter = SimplePageFragmentAdapter(supportFragmentManager, arrayListOf(UserFragment(), MainFragment()))
        viewPager.adapter = adapter
        viewPager.currentItem = 1
    }

    companion object {
        const val TAG = "MainActivity"
    }

}
