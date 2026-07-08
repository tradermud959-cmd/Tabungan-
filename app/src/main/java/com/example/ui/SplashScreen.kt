package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.ui.theme.PrimaryNeon
import com.example.utils.BiometricUtils
import kotlinx.coroutines.delay
import androidx.compose.ui.viewinterop.AndroidView
import android.net.Uri

@Composable
fun SplashScreen(activity: FragmentActivity, onSplashFinished: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    var biometricPassed by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )
    
    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = 1000)
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(3000)
        
        BiometricUtils.showBiometricPrompt(
            activity = activity,
            onSuccess = {
                biometricPassed = true
                onSplashFinished()
            },
            onError = {
                // If error, still allow to proceed or maybe we should close?
                // Let's just proceed for now if biometric fails because it's offline app and maybe no biometric set up.
                onSplashFinished()
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value)
        ) {
            
            // Video Player
            val videoResId = context.resources.getIdentifier("splash_loading", "raw", context.packageName)
            if (videoResId != 0) {
                Box(modifier = Modifier.size(200.dp)) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                useController = false
                                player = ExoPlayer.Builder(ctx).build().apply {
                                    val uri = Uri.parse("android.resource://${context.packageName}/$videoResId")
                                    setMediaItem(MediaItem.fromUri(uri))
                                    prepare()
                                    playWhenReady = true
                                    repeatMode = ExoPlayer.REPEAT_MODE_ALL
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                CircularProgressIndicator(color = PrimaryNeon, modifier = Modifier.size(80.dp))
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "TABUNGKU",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryNeon,
                letterSpacing = 4.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Sedikit demi sedikit menjadi besar.",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            LinearProgressIndicator(
                color = PrimaryNeon,
                trackColor = Color.DarkGray,
                modifier = Modifier
                    .width(200.dp)
                    .height(4.dp)
            )
        }
    }
}
