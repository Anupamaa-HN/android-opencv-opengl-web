package com.example.opedge

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLSurfaceView
import android.view.SurfaceTexture

class GLSurface(context: Context) : GLSurfaceView(context) {
    private val renderer: GLRenderer

    init {
        setEGLContextClientVersion(2)
        renderer = GLRenderer()
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    // Expose a SurfaceTexture so Camera preview can target it
    val surfaceTexture: SurfaceTexture
        get() = renderer.surfaceTexture

    fun queueBitmap(bmp: Bitmap) {
        renderer.updateBitmap(bmp)
        requestRender()
    }
}
