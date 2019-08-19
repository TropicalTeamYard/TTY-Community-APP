package tty.community.pages.activity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
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
import tty.community.R
import tty.community.adapter.MainFragmentAdapter
import tty.community.image.BitmapUtil
import tty.community.model.User
import tty.community.model.User.Companion.autoLogin
import tty.community.pages.fragment.UserFragment
import tty.community.util.CONF

class MainActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener,
    UserFragment.OnFragmentInteractionListener,
    DrawerLayout.DrawerListener, View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.main_portrait-> main_drawer.openDrawer(GravityCompat.START)
            R.id.main_fab -> startActivity(intents[main_viewPager.currentItem])
            R.id.main_search -> main_nav.selectedItemId = R.id.nav_search

            else -> Log.d(TAG, "unknown view clicked")
        }
    }

    var user: User? = null

    override fun onDrawerStateChanged(newState: Int) {
//        Log.d(TAG, "state:$newState")
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
//        Log.d(TAG, "silde offset: $slideOffset")
    }

    override fun onDrawerClosed(drawerView: View) {
//        Log.d(TAG, "draw close")
    }

    override fun onDrawerOpened(drawerView: View) {
//        Log.d(TAG, "draw open")
    }

    override fun onFragmentInteraction(uri: Uri) {

    }

    private lateinit var intents: Array<Intent>

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_home -> {
                main_search.visibility = View.VISIBLE
                main_viewPager.currentItem = 0
                main_title.text = "主页"
                true
            }

            R.id.nav_search -> {
                main_search.visibility = View.GONE
                main_viewPager.currentItem = 1
                main_title.text = "发现"
                true
            }

            R.id.nav_chat -> {
                main_search.visibility = View.VISIBLE
                main_title.text = "私信"
                main_viewPager.currentItem = 2
                true
            }

            else -> false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    override fun onResume() {
        super.onResume()
        autoLogin(this)
        refresh()
    }

    private fun refresh() {
        user = User.find(this)
        if (user != null) {
            Glide.with(this).load(CONF.API.public.portrait + "?" + "id=${user?.id}").apply(BitmapUtil.optionsNoCache()).centerCrop().into(main_portrait)
        }
    }

    private fun init() {
        intents = arrayOf(
            Intent(this, CreateBlogActivity::class.java),
            Intent(this, CreateBlogActivity::class.java),
            Intent(this, CreateBlogActivity::class.java)
        )
        val adapter = MainFragmentAdapter(supportFragmentManager)
        main_viewPager.adapter = adapter
        main_viewPager.offscreenPageLimit = 2
        main_nav.setOnNavigationItemSelectedListener(this)
        main_drawer.addDrawerListener(this)
        main_drawer.setScrimColor(Color.TRANSPARENT)
        main_portrait.setOnClickListener(this)
        main_fab.setOnClickListener(this)
        main_search.setOnClickListener(this)
    }

    companion object {
        const val TAG = "MainActivity"
    }

}
