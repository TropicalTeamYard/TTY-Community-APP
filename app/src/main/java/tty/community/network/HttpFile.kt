package tty.community.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

object HttpFile {
    fun getBitmap(picPath: String): Bitmap? {
        var myFileUrl: URL? = null
        var bitmap: Bitmap? = null
        try {
            myFileUrl = URL(picPath)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        try {
            val conn = myFileUrl?.openConnection()
            conn?.doInput = true
            conn?.connect()
            val `is` = conn?.getInputStream()
            bitmap = BitmapFactory.decodeStream(`is`)
            `is`?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }
}