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
    fun saveBitmapFile(context: Context, bitmap: Bitmap): File {
        val file = Storage.getCacheDirectory(context, "image")
        val pic = File(file, randomString(Date()))
        pic.createNewFile()
        val bos = BufferedOutputStream(FileOutputStream(pic))
        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, bos)
        bos.flush()
        bos.close()

        return pic
    }

    private fun randomString(time: Date) =
        ("${time.time}${(100000..999999).random()}".hashCode() and Integer.MAX_VALUE).toString()
}