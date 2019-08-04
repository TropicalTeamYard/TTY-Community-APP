package tty.community.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.closeQuietly
import tty.community.values.Values
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

object NetUtils {
    fun post(url: String, params: HashMap<String, String>): String {
        val client = OkHttpClient()
        val builder = FormBody.Builder()
        for (item in params) {
            builder.add(item.key, item.value)
        }
        val body = builder.build()
        val request = Request.Builder().url(url).post(body).build()
        try {
            val response = client.newCall(request).execute()
            val string = response.body!!.string()
            response.closeQuietly()
            return string
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Values.errorJson
    }

    fun postBitmap(url: String, params: HashMap<String, String>): Bitmap? {
        val client = OkHttpClient()
        val builder = FormBody.Builder()
        for (item in params) {
            builder.add(item.key, item.value)
        }
        val body = builder.build()
        val request = Request.Builder().url(url).post(body).build()
        try {
            val response = client.newCall(request).execute()
            val `is` = response.body!!.byteStream()
            val bitmap = BitmapFactory.decodeStream(`is`)
            response.closeQuietly()
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun get(url: String): String {
        val urlE = toURLEncoded(url)
        val client = OkHttpClient()
        val request = Request.Builder().url(urlE).build()
        try {
            val response = client.newCall(request).execute()
            val string = response.body!!.string()
            response.closeQuietly()
            return string
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Values.errorJson
    }


    private fun toURLEncoded(paramString: String): String {
        var str = paramString
        if (paramString.isEmpty()) {
            return ""
        }
        try {
            str = URLEncoder.encode(str, "UTF-8")
            return str
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return ""
    }

    @Throws(IOException::class)
    private fun getStringFromInputStream(stream: InputStream): String {
        val os = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var len: Int
        do {
            len = stream.read(buffer)
            if (len == -1) {
                break
            }
            os.write(buffer, 0, len)
        } while (true)
        stream.close()
        val state = os.toString()
        os.close()
        return state
    }

    private const val TAG = "NetUtils"
}
