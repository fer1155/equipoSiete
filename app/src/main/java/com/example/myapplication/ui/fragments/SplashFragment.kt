package com.example.myapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.ui.theme.OrangeMain
import kotlinx.coroutines.delay

class SplashFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Criterio 1: Fullscreen (Ocultar barras del sistema)
        activity?.window?.let { window ->
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            controller.hide(WindowInsetsCompat.Type.statusBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SplashScreen {
                    findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Restaurar barras al salir del Splash
        activity?.window?.let { window ->
            WindowCompat.getInsetsController(window, window.decorView).show(WindowInsetsCompat.Type.statusBars())
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "bottleScale")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        delay(5000) // Criterio 4: Temporizador 5s
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.botella),
                contentDescription = "Botella",
                modifier = Modifier
                    .size(250.dp)
                    .scale(scale)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "pico botella",
                color = OrangeMain,
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}