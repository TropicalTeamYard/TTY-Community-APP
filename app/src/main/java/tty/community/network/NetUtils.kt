package tty.community.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.closeQuietly
import java.io.File
import java.lang.Exception
import java.util.concurrent.TimeUnit

object NetUtils {
    fun post(url: String, params: HashMap<String, String>): Result {
        try {
            val client = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()
            val builder = FormBody.Builder()
            for (item in params) {
                builder.add(item.key, item.value)
            }

            val body = builder.build()
            val request = Request.Builder().url(url).post(body).build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val result = Result(Status.Success, response.body?.string())
                response.closeQuietly()
                return result
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Result(Status.Fail, "Connect Failure")
    }

    fun postBitmap(url: String, params: HashMap<String, String>): Bitmap {
        val client = OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS).build()
        val builder = FormBody.Builder()
        for (item in params) {
            builder.add(item.key, item.value)
        }
        val body = builder.build()
        val request = Request.Builder().url(url).post(body).build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val `is` = response.body!!.byteStream()
            val bitmap = BitmapFactory.decodeStream(`is`)
            response.closeQuietly()
            return bitmap
        }

        val bitmap = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.GRAY)
        return bitmap
    }

    fun get(url: String): Result {
        try {
            val client = OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS).build()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val result = Result(Status.Success, response.body?.string())
                response.closeQuietly()
                return result
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Result(Status.Fail, "Connect Failure")
    }

    fun postMultipleForm(url: String, map: Map<String, String>, files: ArrayList<File>, name: String): Result {
        try {
            val client = OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS).build()
            val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)

            for (file in files) {
                val body = file.asRequestBody("image/*".toMediaTypeOrNull())
                requestBody.addFormDataPart(name, file.name, body)
            }

            for (item in map) {
                val body = item.value.toRequestBody("multipart/form-data; charset=utf-8".toMediaTypeOrNull())
                requestBody.setType(MultipartBody.FORM).addFormDataPart(item.key, null, body)
            }

            val request = Request.Builder().url(url).post(requestBody.build()).build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val result = Result(Status.Success, response.body?.string())
                response.closeQuietly()
                return result
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Result(Status.Fail, "Connect Failure")
    }


    class Result(val status: Status, val result: String?)

    enum class Status {
        Success, Fail
    }
    private const val TAG = "NetUtils"
}
