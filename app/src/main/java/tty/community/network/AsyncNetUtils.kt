package tty.community.network

import android.os.Handler
import java.io.File

object AsyncNetUtils {
    fun get(url: String, callback: Callback) {
        val handler = Handler()
        Thread(Runnable {
            val result = NetUtils.get(url)
            when(result.status) {
                NetUtils.Status.Success -> handler.post { callback.onResponse(result.result) }
                NetUtils.Status.Fail -> handler.post { callback.onFailure("网络异常") }
            }
        }).start()
    }

    fun post(url: String, content: HashMap<String, String>, callback: Callback) {
        val handler = Handler()
        Thread(Runnable {
            val result = NetUtils.post(url, content)
            when(result.status) {
                NetUtils.Status.Success -> handler.post { callback.onResponse(result.result) }
                NetUtils.Status.Fail -> handler.post { callback.onFailure("网络异常") }
            }
        }).start()
    }

    fun postMultipleForm(url: String, map: Map<String, String>, files: ArrayList<File>, callback: Callback, name: String = "files") {
        val handler = Handler()
        Thread(Runnable {
            val result = NetUtils.postMultipleForm(url, map, files, name)
            when(result.status) {
                NetUtils.Status.Success -> handler.post { callback.onResponse(result.result) }
                NetUtils.Status.Fail -> handler.post { callback.onFailure("网络异常") }
            }
        }).start()
    }

    interface Callback {
        fun onResponse(result: String?): Int
        fun onFailure(msg: String): Int
    }
}
