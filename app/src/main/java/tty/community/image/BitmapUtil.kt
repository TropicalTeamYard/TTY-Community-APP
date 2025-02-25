package tty.community.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import tty.community.R
import java.io.IOException


object BitmapUtil {

    private fun load(path: String): Bitmap {
        return BitmapFactory.decodeFile(path)
    }

    /** 从给定的路径加载图片，并指定是否自动旋转方向
     * @param path 图片路径
     * @param adjustOrientation 是否自动旋转方向
     */
    fun load(path: String, adjustOrientation: Boolean): Bitmap {
        if (!adjustOrientation) {
            return load(path)
        } else {
            var bm = load(path)
            var digree = 0
            var exif: ExifInterface?
            try {
                exif = ExifInterface(path)
            } catch (e: IOException) {
                e.printStackTrace()
                exif = null
            }

            if (exif != null) {
                // 读取图片中相机方向信息
                // 计算旋转角度
                digree = when (exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }
            }
            if (digree != 0) {
                // 旋转图片
                val m = Matrix()
                m.postRotate(digree.toFloat())
                bm = Bitmap.createBitmap(
                    bm, 0, 0, bm.width,
                    bm.height, m, true
                )
            }
            return bm
        }
    }

    fun Bitmap.cropCenter(): Bitmap {
        val width = this.width
        val height = this.height
        return if (width > height) {
            Bitmap.createBitmap(this, (width - height) / 2, 0, height, height)
        } else {
            Bitmap.createBitmap(this, 0, (height - width) / 2, width, width)
        }
    }

    fun Bitmap.zoom(newWidth: Int, newHeight: Int): Bitmap{
        val width = width
        val height = height
        val scaleWidth = newWidth / width.toFloat()
        val scaleHeight: Float = newHeight / height.toFloat()
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    fun optionsNoCachePortraitDefaultUser() =
        RequestOptions().error(R.drawable.ic_user_gray_64dp).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)

    fun optionsMemoryCacheDefaultPortrait() =
        RequestOptions().error(R.drawable.ic_user_gray_64dp).diskCacheStrategy(DiskCacheStrategy.NONE)

    fun optionsMemoryCache() =
        RequestOptions().error(R.drawable.ic_broken_image_gray).diskCacheStrategy(DiskCacheStrategy.NONE)

    fun optionsNoCache() =
        RequestOptions().error(R.drawable.ic_broken_image_gray).diskCacheStrategy(DiskCacheStrategy.NONE)

}