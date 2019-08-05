package tty.community.pages.fragment


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_me.*
import tty.community.R
import tty.community.data.MainDBHelper
import tty.community.model.User
import tty.community.network.AsyncTaskUtil
import tty.community.pages.activity.LoginActivity
import tty.community.pages.activity.UserDetailActivity
import tty.community.values.Values

class MeFragment : Fragment() {

    private var user: User? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_me, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        me_login.setOnClickListener {
            startActivity(Intent(this.context, LoginActivity::class.java))
        }

        me_more.setOnClickListener {
            startActivity(Intent(this.context, UserDetailActivity::class.java))
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        user = MainDBHelper(this.context!!).findUser()
        if(user != null) {
            me_id.text = "ID: ${user?.id}"
            me_nickname.text = user?.nickname

            val url = "${Values.api["public_user"]}/portrait"
            val map = HashMap<String, String>()
            map["target"] = user?.id?:"null"
            AsyncTaskUtil.AsyncNetUtils.postBitmap(url, map, object : AsyncTaskUtil.AsyncNetUtils.CallbackBitmap{
                override fun onResponse(bitmap: Bitmap) {
                    me_portrait?.setImageBitmap(bitmap)
                }
            })
        }

    }

    companion object {
        const val TAG = "MeFragment"
    }
}
