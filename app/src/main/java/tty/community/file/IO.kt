package tty.community.file

import android.content.Context
import android.graphics.Bitmap
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

object IO {
    @Throws(IOException::class)
    fun bitmap2FileCache(context: Context, bitmap: Bitmap, quality: Int): File {
        val file = Storage.getStorageDirectory(context, "cache")
        val pic = File(file, random(Date()))
        pic.createNewFile()
        val bos = BufferedOutputStream(FileOutputStream(pic))
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos)
        bos.flush()
        bos.close()

        return pic
    }

    private fun random(time: Date) = ("${time.time}${(100000..999999).random()}".hashCode() and Integer.MAX_VALUE).toString()

    const val TAG = "tty.community.file.IO"
}