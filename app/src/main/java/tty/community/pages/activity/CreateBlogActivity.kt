package tty.community.pages.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_create_blog.*
import tty.community.R
import tty.community.adapter.CreateBlogFragmentAdapter

class CreateBlogActivity : AppCompatActivity(), ViewPager.OnPageChangeListener,
    BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.nav_short -> {
                create_blog_viewPager.currentItem = 0
                true
            }

            R.id.nav_complex -> {
                create_blog_viewPager.currentItem = 1
                true
            }

            R.id.nav_pro -> {
                create_blog_viewPager.currentItem = 2
                true
            }
            else -> false
        }
    }

    override fun onPageScrollStateChanged(state: Int) { }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }

    override fun onPageSelected(position: Int) {
        create_blog_nav.selectedItemId = when (position) {
            0 -> R.id.nav_short
            1 -> R.id.nav_complex
            2 -> R.id.nav_pro
            else -> return
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_blog)
        setAdapter()

    }

    private fun setAdapter() {
        val adapter = CreateBlogFragmentAdapter(supportFragmentManager)
        create_blog_viewPager.adapter = adapter
        create_blog_viewPager.addOnPageChangeListener(this)
        create_blog_nav.setOnNavigationItemSelectedListener(this)
    }
}
