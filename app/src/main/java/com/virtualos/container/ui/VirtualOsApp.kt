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
                    Text("TwoYi runtime", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Launch the integrated Android 8.1 container. First boot extracts the root filesystem and may take several minutes.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Button(onClick = { context.startActivity(Intent(context, io.twoyi.Render2Activity::class.java)) }, modifier = Modifier.fillMaxWidth()) { Text("Start Android container") }
                }
            }
            OutlinedButton(onClick = { context.startActivity(Intent(context, io.twoyi.ui.SelectAppActivity::class.java)) }, modifier = Modifier.fillMaxWidth()) { Text("Import apps into container") }
            OutlinedButton(onClick = { context.startActivity(Intent(context, io.twoyi.ui.SettingsActivity::class.java)) }, modifier = Modifier.fillMaxWidth()) { Text("Container settings") }
            Spacer(Modifier.weight(1f))
            Text("TwoYi engine • ARM64 • Android host 8.1–12", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}
