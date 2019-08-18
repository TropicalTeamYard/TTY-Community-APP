package tty.community.pages.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_me.*
import tty.community.R
import tty.community.adapter.MainFragmentAdapter
import tty.community.image.BitmapUtil
import tty.community.model.User
import tty.community.model.User.Companion.autoLogin
import tty.community.util.CONF

class MainActivity : AppCompatActivity(), ViewPager.OnPageChangeListener,
    BottomNavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener {
    override fun onDrawerStateChanged(newState: Int) {}

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

    override fun onDrawerClosed(drawerView: View) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDrawerOpened(drawerView: View) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var user : User? = null

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_home -> {
                main_viewPager.currentItem = 0
                main_title.text = "主页"
                true
            }
            R.id.nav_chat -> {
                main_title.text = "私信"
                main_viewPager.currentItem = 1
                true
            }
            R.id.nav_me -> {
                main_viewPager.currentItem = 2
                main_title.text = user?.nickname?:"请登录"
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
        autoLogin(this)
        user = User.find(this)
        refresh()
    }

    private fun refresh() {

        if (user != null) {
            Glide.with(this).load(CONF.API.public.portrait + "?" + "id=${user?.id}").apply(BitmapUtil.optionsNoCache()).centerCrop().into(main_portrait)
        }
    }


    private fun setAdapter() {
        val adapter = MainFragmentAdapter(supportFragmentManager)
        main_viewPager.adapter = adapter
        main_viewPager.addOnPageChangeListener(this)
        main_viewPager.offscreenPageLimit = 2
        main_nav.setOnNavigationItemSelectedListener(this)
        main_drawer_layout.addDrawerListener(this)
    }

    companion object {
        const val TAG = "MainActivity"
    }


}
