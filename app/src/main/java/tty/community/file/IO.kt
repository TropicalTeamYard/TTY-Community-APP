package tty.community.file

import android.content.Context
import android.graphics.Bitmap

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class IO {
    @Throws(IOException::class)
    fun saveBitmapFile(context: Context, bitmap: Bitmap): File {
        val file = Storage.getCacheDirectory(context, "image")
        val pic = File(file, "")
        val bos = BufferedOutputStream(FileOutputStream(file!!))

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)

        bos.flush()
        bos.close()

        return file
    }
}