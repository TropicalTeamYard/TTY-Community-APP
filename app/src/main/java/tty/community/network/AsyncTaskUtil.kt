package tty.community.network

import android.graphics.Bitmap
import android.os.Handler

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

        fun postBitmap(url: String, content: HashMap<String, String>, callback: CallbackBitmap) {
            val handler = Handler()
            Thread(Runnable {
                val response = NetUtils.postBitmap(url, content)
                handler.post { callback.onResponse(response) }
            }).start()
        }

        interface Callback {
            fun onResponse(response: String)
        }

        interface CallbackBitmap {
            fun onResponse(bitmap: Bitmap)
        }
    }
}
