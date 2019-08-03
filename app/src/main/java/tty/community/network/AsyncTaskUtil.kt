package tty.community.network

import android.os.Handler
import java.io.InputStream

class AsyncTaskUtil {

    object AsyncNetUtils {
        operator fun get(url: String, callback: Callback) {
            val handler = Handler()
            Thread(Runnable {
                val response = NetUtils.get(url)
                handler.post { callback.onResponse(response) }
            }).start()
        }

        fun post(url: String, content: HashMap<String, String>, callback: Callback) {
            val handler = Handler()
            Thread(Runnable {
                val response = NetUtils.post(url, content)
                handler.post { callback.onResponse(response) }
            }).start()
        }

        fun postStream(url: String, content: HashMap<String, String>, callback: Callback1) {
            val handler = Handler()
            Thread(Runnable {
                val response = NetUtils.postStream(url, content)
                handler.post { callback.onResponse(response!!) }
            }).start()
        }

        interface Callback {
            fun onResponse(response: String)
        }

        interface Callback1 {
            fun onResponse(response: InputStream)
        }
    }
}
