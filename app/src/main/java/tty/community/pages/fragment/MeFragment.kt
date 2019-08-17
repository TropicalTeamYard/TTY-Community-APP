package tty.community.pages.fragment


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.fragment_me.*
import tty.community.database.MainDBHelper
import tty.community.image.BitmapUtil
import tty.community.model.user.User
import tty.community.pages.activity.LoginActivity
import tty.community.pages.activity.UserDetailActivity
import tty.community.values.CONF

class MeFragment : Fragment(), OnRefreshListener {

    override fun onRefresh(refreshLayout: RefreshLayout) {
        user?.let { refresh(it) }

        if (user == null) {
            Toast.makeText(this@MeFragment.context, "请先登录", Toast.LENGTH_SHORT).show()
            me_fragment_refresh.finishRefresh(100)
        }

    }

    private var user: User? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(tty.community.R.layout.fragment_me, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        me_login.setOnClickListener {
            startActivity(Intent(this.context, LoginActivity::class.java))
        }

        me_more.setOnClickListener {
            startActivity(Intent(this.context, UserDetailActivity::class.java))
        }

        me_fragment_refresh.setOnRefreshListener(this)
    }

    @SuppressLint("SetTextI18n")
    private fun refresh(user: User) {
        user.let {
            me_id.text = "ID: ${it.id}"
            me_nickname.text = it.nickname
            val url = CONF.API.public.portrait + "?" + "id=${it.id}"
            Glide.with(this).load(url).apply(BitmapUtil.optionsNoCache()).into(me_portrait)
            me_fragment_refresh.finishRefresh(500)
        }
    }

    override fun onResume() {
        super.onResume()
        user = MainDBHelper(this.context!!).findUser()
        onRefresh(me_fragment_refresh)
    }

    companion object {
        const val TAG = "MeFragment"
    }
}
