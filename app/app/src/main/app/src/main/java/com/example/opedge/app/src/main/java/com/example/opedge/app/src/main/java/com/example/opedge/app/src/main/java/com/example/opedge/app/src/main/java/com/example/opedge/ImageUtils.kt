package com.example.opedge

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image

object ImageUtils {
    fun yuvToBitmap(context: Context, image: Image): Bitmap {
        val nv21 = yuv420ToNv21(image)
        val bitmap = BitmapFactory.decodeByteArray(nv21, 0, nv21.size)
        return bitmap ?: Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
    }

    private fun yuv420ToNv21(image: Image): ByteArray {
        val width = image.width
        val height = image.height
        val ySize = width * height
        val uvSize = width * height / 4
        val nv21 = ByteArray(ySize + uvSize * 2)

        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        yBuffer.get(nv21, 0, ySize)
        var pos = ySize

        val uBytes = ByteArray(uBuffer.remaining()); uBuffer.get(uBytes)
        val vBytes = ByteArray(vBuffer.remaining()); vBuffer.get(vBytes)

        for (i in uBytes.indices) {
            nv21[pos++] = vBytes[i]
            nv21[pos++] = uBytes[i]
        }
        return nv21
    }
}
