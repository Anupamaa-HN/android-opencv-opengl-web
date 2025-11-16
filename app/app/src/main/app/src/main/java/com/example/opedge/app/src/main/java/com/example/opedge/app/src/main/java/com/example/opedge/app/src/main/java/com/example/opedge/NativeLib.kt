package com.example.opedge

object NativeLib {
    init { System.loadLibrary("edgeproc") }

    external fun process(matAddr: Long)
    external fun bitmapToMatAddress(bitmap: android.graphics.Bitmap): Long
}
