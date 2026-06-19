package com.example.myapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.myapplication.R
import com.example.myapplication.ui.theme.MiAppTheme
import com.example.myapplication.ui.theme.OrangeMain
import com.example.myapplication.ui.viewmodels.MainViewModel

class HomeFragment : Fragment() {
    // Usamos activityViewModels para compartir el estado del sonido con otros fragments si fuera necesario
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MiAppTheme {
                    HomeScreen(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val isSoundEnabled by viewModel.isSoundEnabled.collectAsState()
    val counter by viewModel.counter.collectAsState()

    // 5. Animación de parpadeo para el botón (Criterio de Aceptación 5)
    val infiniteTransition = rememberInfiniteTransition(label = "blinkTransition")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alphaAnimation"
    )

    Scaffold(
        topBar = {
            // 2. Toolbar Personalizada (Criterio de Aceptación 2)
            TopAppBar(
                title = { Text("Pico Botella", color = OrangeMain) },
                actions = {
                    IconButton(onClick = { /* Acción Estrella */ }) {
                        Icon(Icons.Default.Star, contentDescription = "Favoritos", tint = OrangeMain)
                    }
                    IconButton(onClick = { viewModel.toggleSound() }) {
                        Icon(
                            imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Sonido",
                            tint = OrangeMain
                        )
                    }
                    IconButton(onClick = { /* Acción Instrucciones */ }) {
                        Icon(Icons.Default.Info, contentDescription = "Info", tint = OrangeMain)
                    }
                    IconButton(onClick = { /* Acción Agregar */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar", tint = OrangeMain)
                    }
                    IconButton(onClick = { /* Acción Compartir */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir", tint = OrangeMain)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Fondo con textura de madera (Criterio de Aceptación 1)
            Image(
                painter = painterResource(id = R.drawable.piso_madera),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // 3. Imagen Central Botella (Criterio de Aceptación 3)
                    Image(
                        painter = painterResource(id = R.drawable.botella),
                        contentDescription = "Botella Juego",
                        modifier = Modifier.size(300.dp)
                    )

                    // 4. Contador Regresivo animado (Criterio de Aceptación 4)
                    AnimatedContent(
                        targetState = counter,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
                        },
                        label = "counterAnimation"
                    ) { targetCount ->
                        if (targetCount != null) {
                            Text(
                                text = targetCount.toString(),
                                color = Color.White,
                                fontSize = 100.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // 5. Botón Dinámico "Presióname" (Criterio de Aceptación 5)
                Button(
                    onClick = { viewModel.startCountdown() },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeMain),
                    modifier = Modifier
                        .alpha(alphaAnim) // Efecto de parpadeo
                        .width(220.dp)
                        .height(60.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "PRESIÓNAME",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}