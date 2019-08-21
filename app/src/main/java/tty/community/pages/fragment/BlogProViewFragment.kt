package tty.community.pages.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zzhoujay.richtext.RichText
import kotlinx.android.synthetic.main.fragment_blog_pro_view.*

import tty.community.R


class BlogProViewFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blog_pro_view, container, false)
    }

    fun injectString(text: String){
        RichText.fromMarkdown(text).into(tbx_pro)
    }
}
