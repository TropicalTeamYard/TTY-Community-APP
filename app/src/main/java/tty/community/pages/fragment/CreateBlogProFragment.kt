package tty.community.pages.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ValueCallback
import androidx.core.view.get
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.zzhoujay.richtext.RichText
import kotlinx.android.synthetic.main.fragment_create_blog_pro.*
import tty.community.R
import tty.community.adapter.BlogProFragmentAdapter
import tty.community.model.Blog.Companion.BlogType.Pro
import tty.community.model.BlogData
import tty.community.model.BlogData.IGetBlogData

class CreateBlogProFragment : Fragment(), ValueCallback<String>, IGetBlogData {
    override fun getBlogData(): BlogData {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onReceiveValue(value: String?) {
        if (value != null){
            (adapter.getItem(1) as BlogProViewFragment).injectString(value)
        }
    }

    private lateinit var adapter: BlogProFragmentAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_blog_pro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter =BlogProFragmentAdapter(fragmentManager!!)
        viewPager_pro.adapter =  adapter
        viewPager_pro.setScroll(true)
        (adapter.getItem(0) as BlogProEditFragment).setTextChangedListener(this)
    }

    companion object {
        const val TAG = "CreateBlogProFragment"
        private val TYPE = Pro
    }

}
