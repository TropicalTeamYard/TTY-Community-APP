package tty.community.pages.fragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_create_blog_complex.*
import tty.community.R
import tty.community.model.Blog.Companion.BlogType
import tty.community.model.BlogData
import tty.community.model.BlogData.IGetBlogData

class CreateBlogComplexFragment : Fragment(), View.OnClickListener, IGetBlogData {
    override fun getBlogData(): BlogData {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClick(v: View?) {
        when(v) {
            btn_b -> {
                complexEditText.setBold(!complexEditText.selectionFontStyle.isBold)
            }
            btn_i -> {
                complexEditText.setItalic(!complexEditText.selectionFontStyle.isItalic)
            }
            btn_d -> {
                complexEditText.setStreak(!complexEditText.selectionFontStyle.isStreak)
            }
            btn_print -> {
                Log.d(TAG, "text= ${complexEditText.text.toString()} start=${complexEditText.selectionStart} end=${complexEditText.selectionEnd}")
            }
            btn_h1 -> {
                complexEditText.setFontSize(32)
            }
            btn_h2 -> {
                complexEditText.setFontSize(14)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_blog_complex, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_b.setOnClickListener(this)
        btn_i.setOnClickListener(this)
        btn_d.setOnClickListener(this)
        btn_print.setOnClickListener(this)
        btn_h1.setOnClickListener(this)
        btn_h2.setOnClickListener(this)
    }

    companion object {
        const val TAG = "CreateBlogComplexFragment"
        private val TYPE = BlogType.Pro
    }

}
