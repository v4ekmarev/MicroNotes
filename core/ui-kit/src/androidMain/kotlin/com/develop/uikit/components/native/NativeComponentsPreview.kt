package com.develop.uikit.components.native

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ============================================
// DatePicker Preview
// ============================================

@Preview(showBackground = true)
@Composable
fun NativeDatePickerPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NativeDatePicker",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            val dateState = rememberNativeDatePickerState()
            
            NativeDatePicker(
                state = dateState,
                onDateSelected = { /* handle selection */ }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Selected: ${formatDate(dateState.selectedDateMillis)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// ============================================
// TimePicker Preview
// ============================================

@Preview(showBackground = true)
@Composable
fun NativeTimePickerPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NativeTimePicker",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            val timeState = rememberNativeTimePickerState(
                initialHour = 14,
                initialMinute = 30
            )
            
            NativeTimePicker(
                state = timeState,
                onTimeSelected = { _, _ -> /* handle selection */ }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Selected: ${formatTime(timeState.hour, timeState.minute)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// ============================================
// DateTimePicker Preview
// ============================================

@Preview(showBackground = true)
@Composable
fun NativeDateTimePickerPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NativeDateTimePicker",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            val dateTimeState = rememberNativeDateTimePickerState()
            
            NativeDateTimePicker(
                state = dateTimeState,
                onDateTimeSelected = { /* handle selection */ }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Selected: ${formatDateTime(dateTimeState.selectedDateTimeMillis)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// ============================================
// AlertDialog Preview
// ============================================

@Preview(showBackground = true)
@Composable
fun NativeAlertDialogPreview() {
    MaterialTheme {
        var showDialog by remember { mutableStateOf(true) }
        
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(onClick = { showDialog = true }) {
                    Text("Show Alert Dialog")
                }
                
                if (showDialog) {
                    NativeAlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = "Delete Note?",
                        message = "This action cannot be undone. Are you sure you want to delete this note?"
                    ) {
                        destructiveAction("Delete") {
                            // perform delete
                        }
                        cancelAction("Cancel") {
                            // cancelled
                        }
                    }
                }
            }
        }
    }
}

// ============================================
// ActionSheet Preview
// ============================================

@Preview(showBackground = true)
@Composable
fun NativeActionSheetPreview() {
    MaterialTheme {
        var showActionSheet by remember { mutableStateOf(true) }
        
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(onClick = { showActionSheet = true }) {
                    Text("Show Action Sheet")
                }
                
                NativeActionSheet(
                    visible = showActionSheet,
                    onDismissRequest = { showActionSheet = false },
                    title = "Choose Action",
                    message = "What would you like to do with this note?"
                ) {
                    defaultAction("Edit") { /* edit */ }
                    defaultAction("Duplicate") { /* duplicate */ }
                    defaultAction("Share") { /* share */ }
                    destructiveAction("Delete") { /* delete */ }
                    cancelAction("Cancel") { /* cancel */ }
                }
            }
        }
    }
}

// ============================================
// ColorPicker Preview
// ============================================

@Preview(showBackground = true)
@Composable
fun NativeColorPickerPreview() {
    MaterialTheme {
        var showColorPicker by remember { mutableStateOf(false) }
        var selectedColor by remember { mutableStateOf(Color(0xFF2196F3)) }
        
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "NativeColorPicker",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Tap to change color:")
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(selectedColor)
                        .clickable { showColorPicker = true }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Color: #${selectedColor.toHexString()}",
                    style = MaterialTheme.typography.bodySmall
                )
                
                if (showColorPicker) {
                    NativeColorPicker(
                        color = selectedColor,
                        onColorChanged = { selectedColor = it },
                        onDismissRequest = { showColorPicker = false },
                        supportsAlpha = true
                    )
                }
            }
        }
    }
}

// ============================================
// ImagePicker Preview
// ============================================

@Preview(showBackground = true)
@Composable
fun NativeImagePickerPreview() {
    MaterialTheme {
        var showPicker by remember { mutableStateOf(false) }
        var resultText by remember { mutableStateOf("No image selected") }
        
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "NativeImagePicker",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(onClick = { showPicker = true }) {
                    Text("Pick Image")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = resultText,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                NativeImagePicker(
                    visible = showPicker,
                    config = ImagePickerConfig(
                        allowsMultipleSelection = true,
                        maxSelectionCount = 5
                    ),
                    onResult = { result ->
                        resultText = when (result) {
                            is ImagePickerResult.Success -> 
                                "Selected ${result.images.size} image(s)"
                            is ImagePickerResult.Cancelled -> 
                                "Cancelled"
                            is ImagePickerResult.Error -> 
                                "Error: ${result.message}"
                        }
                        showPicker = false
                    }
                )
            }
        }
    }
}

// ============================================
// ShareSheet Preview
// ============================================

@Preview(showBackground = true)
@Composable
fun NativeShareSheetPreview() {
    MaterialTheme {
        var showShare by remember { mutableStateOf(false) }
        
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "NativeShareSheet",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(onClick = { showShare = true }) {
                    Text("Share Text")
                }
                
                NativeShareSheet(
                    visible = showShare,
                    content = ShareContent.Text(
                        text = "Check out MicroNotes - a simple note-taking app!",
                        subject = "App Recommendation"
                    ),
                    onDismiss = { showShare = false }
                )
            }
        }
    }
}

// ============================================
// All Components Overview Preview
// ============================================

@Preview(showBackground = true, heightDp = 800)
@Composable
fun NativeComponentsOverviewPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Native Components",
                style = MaterialTheme.typography.headlineMedium
            )
            
            ComponentCard("NativeDatePicker") {
                Text("iOS: UIDatePicker (wheel)")
                Text("Android: Material3 DatePicker")
            }
            
            ComponentCard("NativeTimePicker") {
                Text("iOS: UIDatePicker (time mode)")
                Text("Android: Material3 TimePicker")
            }
            
            ComponentCard("NativeDateTimePicker") {
                Text("iOS: UIDatePicker (dateAndTime)")
                Text("Android: Material3 DatePicker + TimePicker")
            }
            
            ComponentCard("NativeAlertDialog") {
                Text("iOS: UIAlertController (alert)")
                Text("Android: Material3 AlertDialog")
            }
            
            ComponentCard("NativeActionSheet") {
                Text("iOS: UIAlertController (actionSheet)")
                Text("Android: Material3 ModalBottomSheet")
            }
            
            ComponentCard("NativeColorPicker") {
                Text("iOS: UIColorPickerViewController")
                Text("Android: Custom color picker dialog")
            }
            
            ComponentCard("NativeImagePicker") {
                Text("iOS: PHPickerViewController")
                Text("Android: PickVisualMedia")
            }
            
            ComponentCard("NativeShareSheet") {
                Text("iOS: UIActivityViewController")
                Text("Android: Intent.ACTION_SEND")
            }
        }
    }
}

@Composable
private fun ComponentCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

// ============================================
// Helper functions
// ============================================

private fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(millis))
}

private fun formatTime(hour: Int, minute: Int): String {
    return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
}

private fun formatDateTime(millis: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(millis))
}

private fun Color.toHexString(): String {
    val red = (this.red * 255).toInt()
    val green = (this.green * 255).toInt()
    val blue = (this.blue * 255).toInt()
    val alpha = (this.alpha * 255).toInt()
    return if (alpha == 255) {
        String.format("%02X%02X%02X", red, green, blue)
    } else {
        String.format("%02X%02X%02X%02X", alpha, red, green, blue)
    }
}
