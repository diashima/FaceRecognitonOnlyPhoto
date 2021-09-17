package kz.tengrilab.facerecognitononlyphoto.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

object GetProperImageFile {
    fun getRotatedImageFile(photoFile: File, context: Context?): File? {
        val option = BitmapFactory.Options()
        option.inSampleSize = 1

        val convertedBitmap: Bitmap =
            modifyOrientation(
                BitmapFactory.decodeFile(photoFile.absolutePath, option),
                photoFile.absolutePath
            )

        return saveImage(convertedBitmap, context)

    }

    private fun saveImage(image: Bitmap, context: Context?): File? {

        val filename = getImageFilePath(context)
        val imageFile = File(filename)

        val os = BufferedOutputStream(FileOutputStream(imageFile))
        image.compress(Bitmap.CompressFormat.JPEG, 100, os)
        os.close()
        return imageFile
    }

    private fun getImageFilePath(context: Context?): String {
        val filename = "${System.currentTimeMillis()}.jpg"
        val dir = context?.getExternalFilesDir(null)

        return if (dir == null) {
            filename
        } else {
            "${dir.absolutePath}/$filename"
        }
    }


    private fun modifyOrientation(bitmap: Bitmap, image_absolute_path: String): Bitmap {
        val ei: ExifInterface = ExifInterface(image_absolute_path);
        when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                return rotate(bitmap, 90f)
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                return rotate(bitmap, 180f)
            }
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                return rotate(bitmap, 270f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                return rotate(bitmap, 270f)
            }
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> {
                return flip(bitmap, true, vertical = false)
            }
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                return flip(bitmap, false, vertical = true)
            }
            else -> {
                return bitmap
            }
        }
    }

    private fun rotate(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun flip(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
        val matrix = Matrix()
        matrix.preScale(if (horizontal) (-1f) else 1f, if (vertical) (-1f) else 1f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true);
    }
}