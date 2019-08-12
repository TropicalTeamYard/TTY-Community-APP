package tty.community.pages.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_square.*
import tty.community.R
import tty.community.pages.activity.CreateBlogActivity

class SquareFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_square, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab_add_blog.setOnClickListener {
            startActivity(Intent(this.context, CreateBlogActivity::class.java))
        }
        square_refreshLayout.setOnRefreshListener {
            square_refreshLayout.finishRefresh(2000);//传入false表示刷新失败
        }

        square_refreshLayout.setOnLoadMoreListener {
            square_refreshLayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
        }


    }

}
