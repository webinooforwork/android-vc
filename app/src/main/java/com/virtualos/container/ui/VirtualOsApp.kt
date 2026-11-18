package com.virtualos.container.ui

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext

@Composable
fun VirtualOsApp() {
    val context = LocalContext.current
    MaterialTheme(colorScheme = darkColorScheme(primary = androidx.compose.ui.graphics.Color(0xFFB8ABFF), background = androidx.compose.ui.graphics.Color(0xFF10111A), surface = androidx.compose.ui.graphics.Color(0xFF1A1B27))) {
        Column(Modifier.fillMaxSize().padding(22.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("VIRTUALOS", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text("Android container", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Card(shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Ananbox runtime", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Launch an isolated Android container. On first launch, select a compatible Ananbox rootfs archive (.7z). The engine supports Android 11+ hosts.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Button(onClick = { context.startActivity(Intent(context, com.github.ananbox.MainActivity::class.java)) }, modifier = Modifier.fillMaxWidth()) { Text("Start Android container") }
                }
            }
            OutlinedButton(onClick = { context.startActivity(Intent(context, com.github.ananbox.SettingsActivity::class.java)) }, modifier = Modifier.fillMaxWidth()) { Text("Container settings") }
            Spacer(Modifier.weight(1f))
            Text("Ananbox engine • ARM64 / x86_64 • Android host 11+", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}
