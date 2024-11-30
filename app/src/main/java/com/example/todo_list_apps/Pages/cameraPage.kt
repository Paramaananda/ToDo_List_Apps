package com.example.todo_list_apps.Pages

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import coil.compose.rememberAsyncImagePainter
import com.example.todo_list_apps.R
import com.example.todo_list_apps.ToDoViewModel
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CameraPage(
    todoViewModel: ToDoViewModel,
    onImageCaptured: (Uri) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var permissionGranted by remember { mutableStateOf(false) }

    val previewView = remember { PreviewView(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        if (isGranted) {
            try {
                startCamera(context, lifecycleOwner, previewView) { capture ->
                    imageCapture = capture
                }
            } catch (e: Exception) {
                Log.e("CameraPage", "Camera setup error", e)
            }
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            permissionGranted = true
            try {
                startCamera(context, lifecycleOwner, previewView) { capture ->
                    imageCapture = capture
                }
            } catch (e: Exception) {
                Log.e("CameraPage", "Camera setup error", e)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black), // Background color for better visibility
        contentAlignment = Alignment.Center // Center all content inside the Box
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Camera Preview
            AndroidView(
                modifier = Modifier
                    .size(300.dp) // Set a fixed size for the camera view
                    .background(Color.Gray),
                factory = { previewView }
            )

            Spacer(modifier = Modifier.height(16.dp)) // Add space between camera and button

            // Capture Button
            Button(
                onClick = {
                    imageCapture?.let { capture ->
                        capturePhoto(context, capture) { uri ->
                            capturedImageUri = uri
                            onImageCaptured(uri)
                        }
                    }
                }
            ) {
                Text("Capture Photo")
            }

            Spacer(modifier = Modifier.height(16.dp)) // Add space between button and captured image

            // Display Captured Image
            capturedImageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Captured Image",
                    modifier = Modifier.size(200.dp)
                )
            }
        }
    }
}

// Utility Functions
private fun hasCameraPermission(context: Context) =
    ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

private fun startCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    onImageCaptureReady: (ImageCapture) -> Unit
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener({
        try {
            val cameraProvider = cameraProviderFuture.get()

            // Unbind any previous use cases
            cameraProvider.unbindAll()

            // Preview use case
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            // Image capture use case
            val imageCapture = ImageCapture.Builder()
                .setTargetResolution(Size(1280, 720))
                .build()

            // Select back camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            // Bind use cases to camera
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )

            onImageCaptureReady(imageCapture)

        } catch (exc: Exception) {
            Log.e("CameraPage", "Camera initialization failed", exc)
        }
    }, ContextCompat.getMainExecutor(context))
}

private fun capturePhoto(
    context: Context,
    imageCapture: ImageCapture?,
    onPhotoCaptured: (Uri) -> Unit
) {
    val photoFile = File(
        context.getExternalFilesDir(null),
        "todo_image_${System.currentTimeMillis()}.jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture?.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                onPhotoCaptured(savedUri)
            }

            override fun onError(exc: ImageCaptureException) {
                // Handle error
            }
        }
    )
}