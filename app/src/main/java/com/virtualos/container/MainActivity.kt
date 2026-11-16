package com.virtualos.container
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.virtualos.container.ui.VirtualOsApp
class MainActivity : ComponentActivity() { override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); window.statusBarColor=android.graphics.Color.rgb(16,17,26); window.navigationBarColor=android.graphics.Color.rgb(16,17,26); setContent { VirtualOsApp() } } }
