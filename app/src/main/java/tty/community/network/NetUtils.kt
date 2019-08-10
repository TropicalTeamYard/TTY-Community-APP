package tty.community.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Handler
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.closeQuietly
import tty.community.values.Values
import java.io.*
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

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
            if (response.isSuccessful) {
                val string = response.body!!.string()
                response.closeQuietly()
                return string
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Values.errorJson
    }

    fun postBitmap(url: String, params: HashMap<String, String>): Bitmap {
        val client = OkHttpClient()
        val builder = FormBody.Builder()
        for (item in params) {
            builder.add(item.key, item.value)
        }
        val body = builder.build()
        val request = Request.Builder().url(url).post(body).build()
        try {
            val response = client.newCall(request).execute()
            if(response.isSuccessful) {
                val `is` = response.body!!.byteStream()
                val bitmap = BitmapFactory.decodeStream(`is`)
                response.closeQuietly()
                return bitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val bitmap = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.GRAY)
        return bitmap
    }

    fun get(url: String): String {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        try {
            val response = client.newCall(request).execute()
            if(response.isSuccessful) {
                val string = response.body!!.string()
                response.closeQuietly()
                return string
            }
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

    object MultipleForm {
        fun post(url: String, map: Map<String, String>, files: ArrayList<File>): String {
            try {

                val client = OkHttpClient.Builder().writeTimeout(30, TimeUnit.SECONDS).build()
                val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)

                for (i in 0 until files.size) {
                    val file = files[i]
                    val body = file.asRequestBody("image/*".toMediaTypeOrNull())
                    requestBody.addFormDataPart("file_$i", file.name, body)
                }

                for (item in map) {
                    val body = item.value.toRequestBody("multipart/form-data; charset=utf-8".toMediaTypeOrNull())
                    requestBody.setType(MultipartBody.FORM).addFormDataPart(item.key, null, body)
                }

                val request = Request.Builder().url(url).post(requestBody.build()).build()
                val response = client.newBuilder().readTimeout(5000, TimeUnit.MILLISECONDS).build().newCall(request).execute()
                if (response.isSuccessful) {
                    val string = response.body!!.string()
                    response.closeQuietly()
                    return string
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return Values.errorJson
        }
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
