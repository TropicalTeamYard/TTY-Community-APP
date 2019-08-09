package tty.community.pages.fragment


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import tty.community.R
import tty.community.network.AsyncTaskUtil
import tty.community.values.Values

class CreateBlogShortFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_blog_short, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    fun submit() {
        AsyncTaskUtil.AsyncNetUtils.postMultipleForm("${Values.api["blog"]}/create", mapOf(), arrayOf(), object : AsyncTaskUtil.AsyncNetUtils.Callback {
            override fun onResponse(response: String) {
                Log.d(TAG, response)
            }

        })
    }

    companion object {
        const val TAG = "CreateBlogShortFragment"
    }

}
