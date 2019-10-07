package tty.community.pages.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_main.*

import tty.community.R
import tty.community.adapter.MainFragmentAdapter
import tty.community.file.Storage
import tty.community.image.BitmapUtil
import tty.community.model.User
import java.io.File


class MainFragment : Fragment(), BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private lateinit var intents: Array<Intent>

    override fun onClick(v: View?) {
        when (v?.id) {
            //R.id.main_portrait -> main_drawer.openDrawer(GravityCompat.START)
            R.id.fab_add_text, R.id.fab_add_richText, R.id.fab_add_markdown -> {
                val intent = intents[main_viewPager.currentItem]
                intent.putExtra(
                    "mode", when (v.id) {
                        R.id.fab_add_richText -> "richText"
                        R.id.fab_add_markdown -> "markdown"
                        else -> "text"
                    }
                )
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

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    public fun refresh(user: User) {
        val portraitCache = File(Storage.getStorageDirectory(context!!, "portrait"), user.id)
        if (portraitCache.exists()) {
            Glide.with(this).load(portraitCache)
                .apply(BitmapUtil.optionsNoCachePortraitDefaultUser()).centerCrop()
                .into(main_portrait)
        } else {
            Log.e(TAG, "portrait file for user ${user.id} not exist")
        }
    }

    private fun init() {
        intents = arrayOf(
            Intent(context, CreateBlogActivity::class.java),
            Intent(context, CreateBlogActivity::class.java),
            Intent(context, CreateBlogActivity::class.java)
        )
        val adapter = MainFragmentAdapter(activity!!.supportFragmentManager)
        main_viewPager.adapter = adapter
        main_viewPager.offscreenPageLimit = 2
        main_nav.setOnNavigationItemSelectedListener(this)
        main_portrait.setOnClickListener(this)
        fab_add_text.setOnClickListener(this)
        fab_add_richText.setOnClickListener(this)
        fab_add_markdown.setOnClickListener(this)
        main_search.setOnClickListener(this)

    }

    companion object {
        const val TAG = "MainFragment"
    }
}
