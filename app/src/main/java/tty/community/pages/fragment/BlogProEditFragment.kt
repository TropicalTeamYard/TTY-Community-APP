package tty.community.pages.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ValueCallback
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.fragment_blog_pro_edit.*

import tty.community.R


class BlogProEditFragment : Fragment(), TextWatcher {
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        mTextWatcher?.onReceiveValue(edit_pro.text.toString())
    }

    private var mTextWatcher: ValueCallback<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blog_pro_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        edit_pro.addTextChangedListener(this)
    }

    fun setTextChangedListener(textWatcher: ValueCallback<String>){
        mTextWatcher = textWatcher
    }
}
