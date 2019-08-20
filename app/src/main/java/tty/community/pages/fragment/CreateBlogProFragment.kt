package tty.community.pages.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.zzhoujay.richtext.RichText
import kotlinx.android.synthetic.main.fragment_create_blog_pro.*
import tty.community.R
import tty.community.model.Blog.Companion.BlogType.Pro

class CreateBlogProFragment : Fragment(), TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        RichText.fromMarkdown(editText_markdown.text.toString()).into(editText_preview)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_blog_pro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editText_markdown.addTextChangedListener(this)
    }

    companion object {
        const val TAG = "CreateBlogProFragment"
        private val TYPE = Pro
    }

}
