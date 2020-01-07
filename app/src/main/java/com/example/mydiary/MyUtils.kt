package com.example.mydiary

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import java.io.ByteArrayOutputStream
import java.io.InputStream

class MyUtils {
    companion object {
        fun tintMenuIcon(context: Context, item: MenuItem, color: Int) {
            val normalDrawable = item.icon
            val wrapDrawable = DrawableCompat.wrap(normalDrawable)
            DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(context, color))
            item.setIcon(wrapDrawable)
        }

        fun getImageFromStream(resolver: ContentResolver, uri: Uri): Bitmap {
            val opt = BitmapFactory.Options()
            opt.inJustDecodeBounds = true
            var inputStream: InputStream = resolver.openInputStream(uri)!!
            BitmapFactory.decodeStream(inputStream, null, opt)
            inputStream.close()
            var bitmapSize = 1
            if((opt.outHeight * opt.outWidth) > 500_000) {
                val outSize: Double = (opt.outHeight * opt.outWidth / 500_000).toDouble()
                bitmapSize = (Math.sqrt(outSize) + 1).toInt()
            }
            opt.inJustDecodeBounds = false
            opt.inSampleSize = bitmapSize
            inputStream = resolver.openInputStream(uri)!!
            val bmp = BitmapFactory.decodeStream(inputStream, null, opt)!!
            inputStream.close()
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