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
import tty.community.R
import tty.community.adapter.MainFragmentAdapter
import tty.community.image.BitmapUtil
import tty.community.model.User
import tty.community.pages.fragment.UserFragment
import tty.community.util.CONF

class MainActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener,
    UserFragment.OnUserInteraction, View.OnClickListener {
    private lateinit var intents: Array<Intent>



    override fun onUserRefreshed(user: User) {
        refresh(user)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.main_portrait-> main_drawer.openDrawer(GravityCompat.START)
            R.id.fab_add_text, R.id.fab_add_richText, R.id.fab_add_markdown ->{
                val intent = intents[main_viewPager.currentItem]
                intent.putExtra("mode", when(v.id){
                    R.id.fab_add_richText -> "richText"
                    R.id.fab_add_markdown -> "markdown"
                    else -> "text"
                })
                startActivity(intent)
            }
            R.id.main_fab -> startActivity(intents[main_viewPager.currentItem])
            R.id.main_search -> main_nav.selectedItemId = R.id.nav_search

            else -> Log.d(TAG, "unknown view clicked")
        }
    }

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

    private fun refresh(user: User) {
        Glide.with(this).load(CONF.API.public.portrait + "?" + "id=${user.id}").apply(BitmapUtil.optionsNoCachePortraitDefaultUser()).centerCrop().into(main_portrait)
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
        main_drawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerOpened(drawerView: View) {}
        })
        main_drawer.setScrimColor(Color.TRANSPARENT)
        main_portrait.setOnClickListener(this)
        fab_add_text.setOnClickListener(this)
        fab_add_richText.setOnClickListener(this)
        fab_add_markdown.setOnClickListener(this)
        main_search.setOnClickListener(this)

    }

    companion object {
        const val TAG = "MainActivity"
    }

}
