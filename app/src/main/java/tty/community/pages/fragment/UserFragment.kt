package tty.community.pages.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.fragment_user.*

import tty.community.R
import tty.community.file.Storage
import tty.community.image.BitmapUtil
import tty.community.model.User
import tty.community.pages.activity.ChangeInfoActivity
import tty.community.pages.activity.LoginActivity
import tty.community.util.CONF
import java.io.File

class UserFragment : Fragment(), OnRefreshListener {
    private var listener: OnUserInteraction? = null

    private fun init() {
        user_refresh.setOnRefreshListener(this)
        user_login.setOnClickListener { startActivity(Intent(context, LoginActivity::class.java)) }
        user_change_info.setOnClickListener {
            val user = User.find(context!!)
            if (user != null) {
                startActivity(Intent(context, ChangeInfoActivity::class.java))
            } else {
                Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show()
                startActivity(Intent(context, LoginActivity::class.java))
            }
        }
    }

    private fun refresh() {
        Log.d(TAG, "user refresh")
        val user: User? = User.find(context!!)
        if (user != null) {
            activity?.let { User.autoLogin(it) }
            listener?.onUserRefreshed(user)
            user_outline.visibility = View.VISIBLE
            user_outline_nothing.visibility = View.GONE
            user_id.text = user.id
            user_nickname.text = user.nickname
            val portraitUrl = CONF.API.public.portrait + "?" + "id=${user.id}"
            Log.d(TAG, "START DOWNLOADING PORTRAIT")
//            Glide.with(this).load(portraitUrl).apply(BitmapUtil.optionsNoCachePortraitDefaultUser()).centerCrop().into(user_portrait)

            Thread {
                val file = File(Storage.getStorageDirectory(context!!, "portrait"), user.id)
                var tmpFile: File? = null
                try {
                    tmpFile = Glide.with(this).load(portraitUrl).apply(BitmapUtil.optionsNoCachePortraitDefaultUser()).downloadOnly(500, 500).get()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (!file.exists() && tmpFile != null) {
                    file.createNewFile()
                    val outputStream = file.outputStream()
                    val inputStream = tmpFile.inputStream()
                    var len: Int
                    val buffer = ByteArray(1024)
                    while (true) {
                        len = inputStream.read(buffer)
                        if (len == -1) {
                            break
                        }
                        outputStream.write(buffer, 0, len)
                    }
                    inputStream.close()
                    outputStream.close()
                    tmpFile.delete()
                    this@UserFragment.activity!!.runOnUiThread {
                        val portraitCache = File(Storage.getStorageDirectory(context!!, "portrait"), user.id)
                        if (portraitCache.exists()) {
                            Glide.with(this@UserFragment).load(portraitCache).apply(BitmapUtil.optionsNoCachePortraitDefaultUser()).centerCrop().into(user_portrait)
                            listener?.onUserRefreshed(user)
                        } else {
                            Log.e(TAG, "portrait file for user ${user.id} not exist")
                        }
                    }
                } else if (tmpFile != null) {
                    this@UserFragment.activity!!.runOnUiThread {
                        val portraitCache = File(Storage.getStorageDirectory(context!!, "portrait"), user.id)
                        if (portraitCache.exists()) {
                            Glide.with(this@UserFragment).load(portraitCache).apply(BitmapUtil.optionsNoCachePortraitDefaultUser()).centerCrop().into(user_portrait)
                            listener?.onUserRefreshed(user)
                        } else {
                            Log.e(TAG, "portrait file for user ${user.id} not exist")
                        }
                    }
                    val outputStream = file.outputStream()
                    val inputStream = tmpFile.inputStream()
                    var len: Int
                    val buffer = ByteArray(1024)
                    while (true) {
                        len = inputStream.read(buffer)
                        if (len == -1) {
                            break
                        }
                        outputStream.write(buffer, 0, len)
                    }
                    inputStream.close()
                    outputStream.close()
                    tmpFile.delete()
                } else if (file.exists()) {
                    this@UserFragment.activity!!.runOnUiThread {
                        val portraitCache = File(Storage.getStorageDirectory(context!!, "portrait"), user.id)
                        if (portraitCache.exists()) {
                            Glide.with(this@UserFragment).load(portraitCache).apply(BitmapUtil.optionsNoCachePortraitDefaultUser()).centerCrop().into(user_portrait)
                            listener?.onUserRefreshed(user)
                        } else {
                            Log.e(TAG, "portrait file for user ${user.id} not exist")
                        }
                    }
                }

            }.start()

            Log.d(TAG, "FINISH DOWNLOADING PORTRAIT")
            user_refresh.finishRefresh(true)
        } else {
            user_outline.visibility = View.GONE
            user_outline_nothing.visibility = View.VISIBLE
            Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show()
            user_refresh.finishRefresh(false)
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        refresh()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnUserInteraction) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnUserInteraction")
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }
    override fun onResume() {
        super.onResume()
        refresh()
    }
    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnUserInteraction {
        fun onUserRefreshed(user: User)
    }
    companion object {
        const val TAG = "UserFragment"
    }
}
