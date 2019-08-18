package tty.community.pages.fragment


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
import tty.community.image.BitmapUtil
import tty.community.model.User
import tty.community.pages.activity.LoginActivity
import tty.community.pages.activity.UserDetailActivity
import tty.community.util.CONF

class MeFragment : Fragment(), OnRefreshListener {

    override fun onRefresh(refreshLayout: RefreshLayout) {
        val user = User.find(context!!)
        if (user != null) {
            refresh(user)
            me_fragment_refresh.finishRefresh(true)
        } else {
            Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show()
            me_fragment_refresh.finishRefresh(false)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(tty.community.R.layout.fragment_me, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        me_login.setOnClickListener {
            startActivity(Intent(context, LoginActivity::class.java))
        }
        me_more.setOnClickListener {
            val user = User.find(context!!)

            if (user != null) {
                startActivity(Intent(context, UserDetailActivity::class.java))
            } else {
                Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show()
                startActivity(Intent(context, LoginActivity::class.java))
            }
        }
        me_fragment_refresh.setOnRefreshListener(this)

    }

    private fun refresh(user: User) {
        me_id.text = user.id
        me_nickname.text = user.nickname
        Glide.with(this).load(CONF.API.public.portrait + "?" + "id=${user.id}").apply(BitmapUtil.optionsNoCache()).centerCrop().into(me_portrait)
    }

    override fun onResume() {
        super.onResume()
        me_fragment_refresh.autoRefresh()
    }

    companion object {
        const val TAG = "MeFragment"
    }
}
