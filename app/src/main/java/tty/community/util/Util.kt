package tty.community.util

import android.util.Log
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.sql.Blob
import java.sql.SQLException
import kotlin.experimental.and


object Util {

    @Throws(SQLException::class, IOException::class)
    fun blobToString(blob: Blob): String {
        val reString: String
        val `is` = blob.binaryStream
        val byteArrayInputStream = `is` as ByteArrayInputStream
        val byteData = ByteArray(byteArrayInputStream.available()) //byteArrayInputStream.available()返回此输入流的字节数
        byteArrayInputStream.read(byteData, 0, byteData.size) //将输入流中的内容读到指定的数组
        reString = String(byteData, StandardCharsets.UTF_8) //再转为String，并使用指定的编码方式
        `is`.close()
        return reString
    }

    fun getMD5(input: String): String {
        return try {
            //拿到一个MD5转换器（如果想要SHA1加密参数换成"SHA1"）
            val messageDigest = MessageDigest.getInstance("MD5")
            //输入的字符串转换成字节数组
            val inputByteArray = input.toByteArray()
            //inputByteArray是输入字符串转换得到的字节数组
            messageDigest.update(inputByteArray)
            //转换并返回结果，也是字节数组，包含16个元素
            val resultByteArray = messageDigest.digest()
            //字符数组转换成字符串返回
            byteArrayToHex(resultByteArray)

        } catch (e: NoSuchAlgorithmException) {
            "null"
        }

    }

    private fun byteArrayToHex(byteArray: ByteArray): String {
        //首先初始化一个字符数组，用来存放每个16进制字符
        val hexDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
        //new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符）
        val resultCharArray = CharArray(byteArray.size * 2)
        //遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        var index = 0
        for (b in byteArray) {
            resultCharArray[index++] = hexDigits[b.toInt().ushr(4) and 0xf]
            resultCharArray[index++] = hexDigits[(b and 0xf).toInt()]
        }

        //字符数组组合成字符串返回
        return String(resultCharArray)
    }

    private fun validateJson(json: String?): JsonType {
        if (json == null) {
            Log.d(TAG, "json is null")
            return Util.JsonType.JsonNull
        }
        Log.d(TAG, json)
        val element: JsonElement
        try {
            element = JsonParser().parse(json)
        } catch (e: Exception) {
            return Util.JsonType.JsonNull
        }

        return when {
            element.isJsonObject -> Util.JsonType.JsonObject
            element.isJsonArray -> Util.JsonType.JsonArray
            else -> Util.JsonType.JsonNull
        }
    }

    fun getJsonObject(json: String?): JsonObject? {
        if (validateJson(json) == Util.JsonType.JsonObject) {
            return JsonParser().parse(json).asJsonObject
        }
        return null
    }

    enum class JsonType {
        JsonObject, JsonArray, JsonNull
    }

    const val TAG = "util"

}
