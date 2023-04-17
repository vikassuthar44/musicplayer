package com.vikas.musicplayer

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.vikas.musicplayer.ui.theme.MusicPlayerTheme


class MainActivity : ComponentActivity() {

    companion object {
        const val TAG: String = "VikasSuthar"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current

            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Log.d(TAG, "onCreate: dscds")
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

            val isPermissionGranted = remember {
                mutableStateOf(false)
            }
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                isPermissionGranted.value = isGranted
            }


            MusicPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if(isPermissionGranted.value) {
                        Greeting()
                    } else {
                        CheckAndRequestCameraPermission(context = context, permission = permission, launcher = launcher)

                    }
                }
            }
        }
    }

}

@Composable
fun CheckAndRequestCameraPermission(
    context: Context,
    permission: String,
    launcher: ManagedActivityResultLauncher<String, Boolean>
) {
    val permissionCheckResult = ContextCompat.checkSelfPermission(context, permission)
    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
        Greeting()
    } else {
        SideEffect {
            launcher.launch(permission)
        }
        showPermissionText {
            launcher.launch(permission)
        }
    }
}

@Composable
fun showPermissionText(
    onClick :() -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Storage access is mandatory otherwise \nwe can't read music file from your device")
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = {
                    onClick.invoke()
                }) {
                    Text(text = "Give Storage Access Permission")
                }
            }

        }
    }
}


@Composable
fun Greeting() {
    val context = LocalContext.current
    val mainViewModel =
        ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application)
            .create(MainViewModel::class.java)

    val musicList = mainViewModel.getAllAudioFromDevice(context = context)
    HomeScreen(musicList = musicList)

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MusicPlayerTheme {
        Greeting()
    }
}