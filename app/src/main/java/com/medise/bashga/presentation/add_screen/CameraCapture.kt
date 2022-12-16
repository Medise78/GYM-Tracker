//package com.medise.bashga.presentation.add_screen
//
//import android.Manifest
//import android.content.Intent
//import android.net.Uri
//import android.provider.Settings
//import android.util.Log
//import androidx.camera.core.CameraSelector
//import androidx.camera.core.ImageCapture
//import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
//import androidx.camera.core.Preview
//import androidx.camera.core.UseCase
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.Button
//import androidx.compose.material.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalLifecycleOwner
//import androidx.compose.ui.unit.dp
//import com.medise.bashga.util.Permission
//import com.medise.bashga.util.executor
//import com.medise.bashga.util.getCameraProvider
//import com.medise.bashga.util.takePicture
//import kotlinx.coroutines.launch
//import java.io.File
//
//@Composable
//fun CameraCapture(
//    modifier: Modifier = Modifier,
//    cameraSelector:CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
//    onFileImage: (File) -> Unit = {}
//) {
//    val context = LocalContext.current
//    Permission(
//        permission = Manifest.permission.CAMERA,
//        rationale = "گفتی که عکس میخوای.خب پس منم رفتم برای گرفتن اجازه",
//        permissionNotAvailableContent = {
//            Column(modifier) {
//                Text(text = "دوربین نیست!")
//                Spacer(modifier = Modifier.height(8.dp))
//                Button(onClick = {
//                    context.startActivity(
//                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
//                            data = Uri.fromParts("package" , context.packageName , null)
//                        }
//                    )
//                }) {
//                    Text(text = "باز کردن تنظیمات")
//                }
//            }
//        }
//    ){
//        Box(modifier) {
//            val lifecycleOwner = LocalLifecycleOwner.current
//            val coroutineScope = rememberCoroutineScope()
//            var previewUseCase by remember {
//                mutableStateOf<UseCase>(Preview.Builder().build())
//            }
//            val imageCaptureUseCase by remember {
//                mutableStateOf(
//                    ImageCapture.Builder()
//                        .setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY)
//                        .build()
//                )
//            }
//            Box {
//                CameraPreview(
//                    modifier = Modifier.fillMaxSize(),
//                    onUseCase = {
//                        previewUseCase = imageCaptureUseCase
//                    }
//                )
//                CapturePictureButton(
//                    modifier = Modifier
//                        .size(100.dp)
//                        .padding(16.dp)
//                        .align(Alignment.BottomCenter),
//                    onClick = {
//                        coroutineScope.launch {
//                            imageCaptureUseCase.takePicture(context.executor).let {
//                                onFileImage(it)
//                            }
//                        }
//                    }
//                )
//            }
//            LaunchedEffect(key1 = previewUseCase){
//                val cameraProvider = context.getCameraProvider()
//                try {
//                    cameraProvider.unbindAll()
//                    cameraProvider.bindToLifecycle(
//                        lifecycleOwner , cameraSelector , previewUseCase , imageCaptureUseCase
//                    )
//                }catch (e:Exception){
//                    Log.e("CameraCapture", "Failed to bind camera use cases", e)
//                }
//            }
//        }
//    }
//}