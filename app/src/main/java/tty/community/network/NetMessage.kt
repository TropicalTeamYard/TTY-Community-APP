package tty.community.network

import android.os.Handler
import com.google.gson.reflect.TypeToken
import tty.community.model.Shortcut
import tty.community.util.Message
import java.io.File

object NetMessage {
    fun <T> get(url: String, typeToken: TypeToken<T>, callback: Callback) {
        val handler = Handler()
        Thread(Runnable {
            val result = NetUtils.get(url)
            when(result.status) {
                NetUtils.Status.Success -> handler.post { callback.result(Message.MsgData.parse(result.result, typeToken)) }
                NetUtils.Status.Fail -> handler.post { callback.result(null) }
            }
        }).start()
    }

    fun <T> post(url: String, content: HashMap<String, String>, typeToken: TypeToken<T>, callback: Callback) {
        val handler = Handler()
        Thread(Runnable {
            val result = NetUtils.post(url, content)
            when(result.status) {
                NetUtils.Status.Success -> handler.post { callback.result(Message.MsgData.parse(result.result, typeToken)) }
                NetUtils.Status.Fail -> handler.post { callback.result(null) }
            }
        }).start()
    }

    fun <T> postMultipleForm(url: String, map: Map<String, String>, files: ArrayList<File>, typeToken: TypeToken<T>, callback: Callback, name: String = "files") {
        val handler = Handler()
        Thread(Runnable {
            val result = NetUtils.postMultipleForm(url, map, files, name)
            when(result.status) {
                NetUtils.Status.Success -> handler.post { callback.result(Message.MsgData.parse(result.result, typeToken)) }
                NetUtils.Status.Fail -> handler.post { callback.result(null) }
            }
        }).start()
    }


    interface Callback {
        fun <T> result(message: T?): Int
    }
}
