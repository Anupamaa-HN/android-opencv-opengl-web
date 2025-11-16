package com.example.opedge

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.Image
import android.media.ImageReader
import android.os.Bundle
import android.util.Size
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.hardware.camera2.*
import android.view.Surface
import android.widget.FrameLayout
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var cameraManager: CameraManager
    private var cameraDevice: CameraDevice? = null
    private var session: CameraCaptureSession? = null
    private var imageReader: ImageReader? = null
    private lateinit var glSurface: GLSurface

    companion object {
        const val REQ_CAM = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glSurface = GLSurface(this)
        setContentView(FrameLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            addView(glSurface)
        })

        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQ_CAM)
        } else {
            openCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQ_CAM && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openCamera() {
        val camId = cameraManager.cameraIdList[0]
        val characteristics = cameraManager.getCameraCharacteristics(camId)
        val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        val size = map!!.getOutputSizes(ImageReader::class.java).firstOrNull() ?: Size(640,480)

        imageReader = ImageReader.newInstance(size.width, size.height, android.graphics.ImageFormat.YUV_420_888, 2)
        imageReader!!.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener
            val bmp = ImageUtils.yuvToBitmap(this, image)
            image.close()

            // Pass bitmap to native via JNI, render with GL
            val matAddr = NativeLib.bitmapToMatAddress(bmp)
            if (matAddr != 0L) {
                NativeLib.process(matAddr)
            }
            glSurface.queueBitmap(bmp)
        }, null)

        try {
            cameraManager.openCamera(camId, object : CameraDevice.StateCallback() {
                override fun onOpened(device: CameraDevice) {
                    cameraDevice = device
                    val readerSurface = imageReader!!.surface
                    val previewSurface = Surface(glSurface.surfaceTexture)
                    val req = device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                        addTarget(readerSurface)
                        addTarget(previewSurface)
                    }
                    device.createCaptureSession(listOf(readerSurface, previewSurface), object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(session: CameraCaptureSession) {
                            this@MainActivity.session = session
                            session.setRepeatingRequest(req.build(), null, null)
                        }
                        override fun onConfigureFailed(session: CameraCaptureSession) {}
                    }, null)
                }
                override fun onDisconnected(device: CameraDevice) { device.close() }
                override fun onError(device: CameraDevice, error: Int) { device.close() }
            }, null)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraDevice?.close()
        imageReader?.close()
        session?.close()
    }
}
