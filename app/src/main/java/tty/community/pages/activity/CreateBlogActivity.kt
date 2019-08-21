package tty.community.pages.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_create_blog.*
import tty.community.R
import tty.community.adapter.CreateBlogFragmentAdapter

class CreateBlogActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_blog)

        var mode = "text"
        if (intent != null && intent.hasExtra("mode")){
            Log.d(TAG, "mode=${intent.getStringExtra("mode")}")
            mode = intent.getStringExtra("mode")

        }

        setAdapter(mode)
    }

    private fun setAdapter(mode:String) {
        val adapter = CreateBlogFragmentAdapter(supportFragmentManager)
        create_blog_viewPager.adapter = adapter
        create_blog_viewPager.currentItem = when(mode){
            "richText" -> {
                label_edit_type.text = "高级"
                1
            }
            "markdown" -> {
                label_edit_type.text = "Markdown"
                2
            }
            else -> {
                label_edit_type.text = "普通"
                0
            }
        }
    }

    companion object {
        private const val TAG = "CreateBlogActivity"
    }
}
