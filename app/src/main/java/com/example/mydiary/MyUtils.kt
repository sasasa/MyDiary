package com.example.mydiary

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream

class MyUtils {
    companion object {
        fun getImageFromStream(resolver: ContentResolver, uri: Uri): Bitmap {
            return bmp
        }

        fun getImageFromByte(bytes: ByteArray): Bitmap {
            val opt = BitmapFactory.Options()
            opt.inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opt)
            var bitmapSize = 1
            if(opt.outHeight * opt.outWidth > 500_000) {
                val outSize = opt.outHeight * opt.outWidth / 500_000.0
                bitmapSize = (Math.sqrt(outSize) + 1).toInt()
            }
            opt.inJustDecodeBounds = false
            opt.inSampleSize = bitmapSize
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opt)
            return bmp
        }

        fun getByteFromImage(bmp: Bitmap): ByteArray {
            val stream = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
            return stream.toByteArray()
        }
    }
}