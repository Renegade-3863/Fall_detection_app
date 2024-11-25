package com.example.fall_detection_app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fall_detection_app.ui.theme.Fall_detection_appTheme

class MainActivity : ComponentActivity() {
    private lateinit var sensorService: SensorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize sensor service
        sensorService = SensorService(this)

        // Request permissions
        checkAndRequestPermissions()

        // Start monitoring falls
        sensorService.startMonitoring()

        // Observe sensor data and handle UI updates
        sensorService.fallDetected.observe(this) { isFallDetected ->
            if (isFallDetected) {
                AlarmUtils.playAlarmSound(this)
            }
        }

        setContent {
            Fall_detection_appTheme {
                FallDetectionApp(sensorService)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorService.stopMonitoring()
        AlarmUtils.stopAlarmSound()
    }

    private fun checkAndRequestPermissions() {
        val permissions = arrayOf(
            // TODO: initiate phone call
            Manifest.permission.CALL_PHONE,
            Manifest.permission.BODY_SENSORS
        )

        val notGrantedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (notGrantedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, notGrantedPermissions.toTypedArray(), 101)
        }
    }
}

@Composable
fun FallDetectionApp(sensorService: SensorService) {
    var isFallDetected by remember { mutableStateOf(false) }

    // Observe LiveData from SensorService
    LaunchedEffect(sensorService) {
        sensorService.fallDetected.observeForever { detected ->
            isFallDetected = detected
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (isFallDetected) {
                EmergencyButton(
                    onHold = { /* Handle hold logic here */ },
                    onRelease = { isFallDetected = false; AlarmUtils.stopAlarmSound() }
                )
            } else {
                BasicText(
                    text = "Monitoring for falls...",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun EmergencyButton(onHold: () -> Unit, onRelease: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Hold for 5 seconds to cancel",
            color = Color.Red,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Surface(
            modifier = Modifier
                .size(150.dp)
                .background(Color.Red, shape = CircleShape),
            shape = CircleShape,
            color = Color.Red,
            shadowElevation = 8.dp,
            onClick = {}
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "HOLD",
                    color = Color.White,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FallDetectionPreview() {
    Fall_detection_appTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            // Preview monitoring state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                BasicText(
                    text = "Monitoring for falls...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Preview fall-detected state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                EmergencyButton(onHold = {}, onRelease = {})
            }
        }
    }
}