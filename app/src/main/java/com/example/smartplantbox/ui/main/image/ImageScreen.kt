package com.example.smartplantbox.ui.main.image

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.smartplantbox.R
import com.example.smartplantbox.data.repository.AuthRepositoryImpl
import com.example.smartplantbox.ui.theme.SmartPlantBoxTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class DeviceDisplayInfo(val name: String, val sn: String) {
    fun getDisplayText(): String = "$name ($sn)"
}

data class PhotoItem(val time: String, val url: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { AuthRepositoryImpl() }

    val plantGalleryText = stringResource(R.string.plant_gallery)
    val photosFromSmartPotText = stringResource(R.string.photos_from_smart_pot)
    val deviceText = stringResource(R.string.device)
    val refreshText = stringResource(R.string.refresh)
    val autoCaptureIntervalText = stringResource(R.string.auto_capture_interval)
    val currentText = stringResource(R.string.current)
    val everyHoursText = stringResource(R.string.every_hours)
    val hoursText = stringResource(R.string.hours)
    val takePhotoNowText = stringResource(R.string.take_photo_now)
    val captureCurrentPlantState = stringResource(R.string.capture_current_plant_state)
    val galleryText = stringResource(R.string.gallery)
    val photoText = stringResource(R.string.photo)
    val photosText = stringResource(R.string.photos)
    val noPhotosYetText = stringResource(R.string.no_photos_yet)
    val takeFirstPhotoText = stringResource(R.string.take_first_photo)
    val intervalUpdatedText = stringResource(R.string.interval_updated)
    val photoCapturedText = stringResource(R.string.photo_captured)
    val failedToLoadImageText = stringResource(R.string.failed_to_load_image)
    val userNotLoggedInText = stringResource(R.string.user_not_logged_in)
    val failedToLoadDataText = stringResource(R.string.failed_to_load_data)
    val failedToLoadDevicesText = stringResource(R.string.failed_to_load_devices)
    val failedToSetIntervalText = stringResource(R.string.failed_to_set_interval)
    val failedToCapturePhotoText  = stringResource(R.string.failed_to_capture_photo)
    val networkErrorText = stringResource(R.string.network_error)
    val intervalSetToText = stringResource(R.string.interval_set_to)
    val noDataAvailableText = stringResource(R.string.no_data_available)
    val retryText = stringResource(R.string.retry)
    val noDataFoundText = stringResource(R.string.no_data_found)
    val noPhotosForKeyText = stringResource(R.string.no_photos_for_key)
    val loadingPhotosText = stringResource(R.string.loading_photos)

    val photoGuideTitle = stringResource(R.string.photo_guide_title)
    val photoGuideContent = stringResource(R.string.photo_guide_content)
    val photoGuideTakePhoto = stringResource(R.string.photo_guide_take_photo)
    val photoGuideGallery = stringResource(R.string.photo_guide_gallery)
    val photoGuideAutoInterval = stringResource(R.string.photo_guide_auto_interval)
    val photoGuideIntervalList = stringResource(R.string.photo_guide_interval_list)
    val photoGuideHowToSave = stringResource(R.string.photo_guide_how_to_save)
    val photoGuideNote = stringResource(R.string.photo_guide_note)

    var isLoading by remember { mutableStateOf(true) }
    var isLoadingDevices by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isUpdating by remember { mutableStateOf(false) }

    var selectedDeviceSn by remember { mutableStateOf("") }
    var availableDevices by remember { mutableStateOf<List<DeviceDisplayInfo>>(emptyList()) }
    var deviceDropdownExpanded by remember { mutableStateOf(false) }

    var currentInterval by remember { mutableIntStateOf(0) }
    var selectedInterval by remember { mutableIntStateOf(4) }
    var isSettingInterval by remember { mutableStateOf(false) }
    var intervalDropdownExpanded by remember { mutableStateOf(false) }
    val intervalOptions = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 24)

    var photoTimes by remember { mutableStateOf<List<String>>(emptyList()) }
    var photoItems by remember { mutableStateOf<List<PhotoItem>>(emptyList()) }
    var isLoadingPhotos by remember { mutableStateOf(false) }
    var isTakingPhoto by remember { mutableStateOf(false) }

    var showPhotoDialog by remember { mutableStateOf(false) }
    var selectedPhotoUrl by remember { mutableStateOf("") }
    var selectedPhotoTime by remember { mutableStateOf("") }

    var showPhotoGuideDialog by remember { mutableStateOf(false) }

    fun loadDeviceNamesMap(context: Context): Map<String, String> {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val json = prefs.getString("device_names", "{}")
        return try {
            Gson().fromJson(json, object : TypeToken<Map<String, String>>() {}.type)
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun formatDateTime(dateString: String): String = try {
        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(dateString) ?: Date()
        )
    } catch (_: Exception) { dateString }

    suspend fun loadPhotoUrl(time: String): String? {
        return try {
            val token = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                .getString("jwt_token", null) ?: return null
            val resp = repository.getPhotoUrl(selectedDeviceSn, time, token)
            if (resp.success && resp.url.isNotEmpty()) resp.url else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun loadAllPhotoUrls() {
        if (photoTimes.isEmpty()) return

        isLoadingPhotos = true
        val newPhotoItems = mutableListOf<PhotoItem>()

        for (time in photoTimes) {
            val url = loadPhotoUrl(time)
            if (url != null) {
                newPhotoItems.add(PhotoItem(time = time, url = url))
            }
        }

        photoItems = newPhotoItems
        isLoadingPhotos = false
    }

    suspend fun loadPhotoData() {
        if (selectedDeviceSn.isEmpty()) {
            isLoading = false
            photoTimes = emptyList()
            photoItems = emptyList()
            return
        }
        isLoading = true
        errorMessage = null
        photoTimes = emptyList()
        photoItems = emptyList()
        try {
            val token = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                .getString("jwt_token", null) ?: run { errorMessage = userNotLoggedInText; isLoading = false; return }

            val intervalResp = repository.getPhotoInterval(selectedDeviceSn, token)
            if (intervalResp.success && intervalResp.data != null) {
                currentInterval  = intervalResp.data.interval_photo
                selectedInterval = currentInterval
            }
            val timesResp = repository.getPhotoTimes(selectedDeviceSn, token)
            if (timesResp.success) {
                photoTimes = timesResp.times
                if (photoTimes.isNotEmpty()) {
                    loadAllPhotoUrls()
                } else {
                    errorMessage = noPhotosForKeyText
                }
            } else {
                errorMessage = timesResp.message ?: failedToLoadDataText
                photoTimes = emptyList()
                photoItems = emptyList()
            }
        } catch (e: Exception) {
            errorMessage = "$failedToLoadDataText: ${e.message}"
            photoTimes = emptyList()
            photoItems = emptyList()
        } finally {
            isLoading = false
        }
    }

    suspend fun loadDevicesAndPhotos() {
        isLoadingDevices = true
        errorMessage = null
        photoTimes = emptyList()
        photoItems = emptyList()
        try {
            val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val email = prefs.getString("user_email", null)
            val token = prefs.getString("jwt_token", null)
            if (email != null && token != null) {
                val resp = repository.getBoundDevices(email, token)
                if (resp.success && !resp.keys.isNullOrEmpty()) {
                    val savedNames = loadDeviceNamesMap(context)
                    val devices = resp.keys.map { key ->
                        val name = savedNames[key] ?: "Device"
                        DeviceDisplayInfo(name = name, sn = key)
                    }
                    availableDevices = devices
                    if (selectedDeviceSn.isEmpty()) selectedDeviceSn = devices.first().sn
                    loadPhotoData()
                } else {
                    errorMessage = noDataFoundText
                    isLoading = false
                }
            } else {
                errorMessage = userNotLoggedInText
                isLoading = false
            }
        } catch (e: Exception) {
            errorMessage = "$failedToLoadDevicesText: ${e.message}"
            isLoading = false
        } finally {
            isLoadingDevices = false
        }
    }

    suspend fun refreshPhotoTimes() {
        if (selectedDeviceSn.isEmpty()) return
        isLoadingPhotos = true
        try {
            val token = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                .getString("jwt_token", null)
            if (token != null) {
                val resp = repository.getPhotoTimes(selectedDeviceSn, token)
                if (resp.success) {
                    photoTimes = resp.times
                    if (photoTimes.isNotEmpty()) {
                        errorMessage = null
                        loadAllPhotoUrls()
                    } else {
                        photoItems = emptyList()
                        errorMessage = noPhotosForKeyText
                    }
                }
            }
        } catch (_: Exception) {
        } finally { isLoadingPhotos = false }
    }

    fun setPhotoInterval() {
        scope.launch {
            isSettingInterval = true
            errorMessage = null
            successMessage = null
            try {
                val token = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    .getString("jwt_token", null) ?: run { errorMessage = userNotLoggedInText; return@launch }
                val resp = repository.setPhotoInterval(selectedDeviceSn, selectedInterval, token)
                if (resp.success) {
                    currentInterval = selectedInterval
                    successMessage  = "$intervalSetToText $selectedInterval $hoursText"
                    android.widget.Toast.makeText(context, intervalUpdatedText, android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    errorMessage = resp.message ?: failedToSetIntervalText
                }
            } catch (e: Exception) {
                errorMessage = "$networkErrorText: ${e.message}"
            } finally { isSettingInterval = false }
        }
    }

    fun takePhotoNow() {
        scope.launch {
            isTakingPhoto = true
            errorMessage = null
            successMessage = null
            try {
                val token = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    .getString("jwt_token", null) ?: run { errorMessage = userNotLoggedInText; return@launch }
                val resp = repository.takePhotoNow(selectedDeviceSn, token)
                if (resp.success) {
                    successMessage = photoCapturedText
                    android.widget.Toast.makeText(context, photoCapturedText, android.widget.Toast.LENGTH_SHORT).show()
                    delay(1000)
                    refreshPhotoTimes()
                } else {
                    errorMessage = resp.message ?: failedToCapturePhotoText
                }
            } catch (e: Exception) {
                errorMessage = "$networkErrorText: ${e.message}"
            } finally { isTakingPhoto = false }
        }
    }

    fun openPhoto(url: String, time: String) {
        selectedPhotoUrl = url
        selectedPhotoTime = time
        showPhotoDialog = true
    }

    LaunchedEffect(Unit) { loadDevicesAndPhotos() }

    LaunchedEffect(selectedDeviceSn) {
        if (selectedDeviceSn.isNotEmpty()) {
            loadPhotoData()
        }
    }

    // UI_________
    Box(modifier = Modifier.fillMaxSize()) {
        Image(painterResource(R.drawable.top_overlay), null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(plantGalleryText, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text(photosFromSmartPotText, fontSize = 16.sp, color = Color.White.copy(alpha = 0.8f))
            Spacer(modifier = Modifier.height(24.dp))

            if (successMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_check),
                            null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(successMessage!!, color = Color(0xFF2E7D32), fontSize = 14.sp)
                    }
                }
            }

            if (errorMessage != null && errorMessage != noPhotosForKeyText) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(errorMessage!!, color = Color(0xFFD32F2F), fontSize = 14.sp)
                    }
                }
            }

            if (isLoadingDevices) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF4CAF50))
                    }
                }
            }
            else if (availableDevices.isEmpty() && errorMessage != null && !isLoadingDevices && errorMessage != noPhotosForKeyText) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_photo_library),
                            noDataFoundText,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            noDataFoundText,
                            fontSize = 14.sp,
                            color = Color(0xFFF44336),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { scope.launch { loadDevicesAndPhotos() } },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(retryText, color = Color.White)
                        }
                    }
                }
            }
            else if (errorMessage == noPhotosForKeyText && availableDevices.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_photo_library),
                            noPhotosForKeyText,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            noPhotosForKeyText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1B5E20),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            takeFirstPhotoText,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            else if (availableDevices.isNotEmpty()) {
                ImageCard {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(deviceText, fontSize = 14.sp, color = Color.Gray)
                            IconButton(
                                onClick = { scope.launch { isUpdating = true; loadPhotoData(); isUpdating = false } },
                                enabled = !isLoading && !isUpdating,
                                modifier = Modifier.size(36.dp)
                            ) {
                                if (isLoading) CircularProgressIndicator(color = Color(0xFF4CAF50), modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                else Icon(painterResource(R.drawable.ic_system_update_alt), refreshText, modifier = Modifier.size(20.dp), tint = Color(0xFF4CAF50))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        ExposedDropdownMenuBox(
                            expanded = deviceDropdownExpanded,
                            onExpandedChange = { deviceDropdownExpanded = it }
                        ) {
                            val selectedDevice = availableDevices.find { it.sn == selectedDeviceSn }
                            OutlinedTextField(
                                value = selectedDevice?.getDisplayText() ?: selectedDeviceSn,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deviceDropdownExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF4CAF50),
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    disabledTextColor = Color.Black
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = deviceDropdownExpanded,
                                onDismissRequest = { deviceDropdownExpanded = false },
                                containerColor = Color.White
                            ) {
                                availableDevices.forEach { device ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = device.getDisplayText(),
                                                fontSize = 14.sp,
                                                color = Color.Black
                                            )
                                        },
                                        onClick = {
                                            selectedDeviceSn = device.sn
                                            deviceDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                ImageCard {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(painterResource(R.drawable.ic_timer), autoCaptureIntervalText, modifier = Modifier.size(28.dp), tint = Color(0xFF4CAF50))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(autoCaptureIntervalText, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                            }
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = "Info",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable { showPhotoGuideDialog = true },
                                tint = Color(0xFF1B5E20)
                            )
                        }

                        Text(
                            "$currentText: ${if (currentInterval > 0) "$currentInterval $everyHoursText" else "—"}",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ExposedDropdownMenuBox(
                                expanded = intervalDropdownExpanded,
                                onExpandedChange = { intervalDropdownExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = "$selectedInterval $hoursText", onValueChange = {}, readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = intervalDropdownExpanded) },
                                    modifier = Modifier.weight(1f).menuAnchor(),
                                    shape = RoundedCornerShape(12.dp),
                                    textStyle = TextStyle(fontSize = 14.sp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF4CAF50),
                                        unfocusedBorderColor = Color(0xFFE0E0E0),
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = Color.Black,
                                        disabledTextColor = Color.Black
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = intervalDropdownExpanded,
                                    onDismissRequest = { intervalDropdownExpanded = false },
                                    containerColor = Color.White
                                ) {
                                    intervalOptions.forEach { h ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = "$h $hoursText",
                                                    fontSize = 14.sp,
                                                    color = Color.Black
                                                )
                                            },
                                            onClick = { selectedInterval = h; intervalDropdownExpanded = false }
                                        )
                                    }
                                }
                            }
                            val canSave = !isSettingInterval && selectedInterval != currentInterval
                            IconButton(
                                onClick = { setPhotoInterval() },
                                enabled = canSave,
                                modifier = Modifier.size(48.dp).background(if (canSave) Color(0xFF4CAF50) else Color(0xFFA5D6A7), RoundedCornerShape(12.dp))
                            ) {
                                if (isSettingInterval) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                else Icon(painterResource(R.drawable.ic_save), stringResource(R.string.save), modifier = Modifier.size(24.dp), tint = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().clickable(enabled = !isTakingPhoto) { takePhotoNow() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(painterResource(R.drawable.ic_camera_alt), takePhotoNowText, modifier = Modifier.size(48.dp), tint = Color.White)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(takePhotoNowText, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(captureCurrentPlantState, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center)
                        if (isTakingPhoto) {
                            Spacer(modifier = Modifier.height(12.dp))
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val photoCount = photoItems.size
                val countLabel = if (photoCount == 1) "$photoCount $photoText" else "$photoCount $photosText"
                Text("📸 $galleryText ($countLabel)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(vertical = 8.dp))

                when {
                    isLoading -> Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                    isLoadingPhotos -> Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color(0xFF4CAF50))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(loadingPhotosText, fontSize = 12.sp, color = Color.White)
                        }
                    }
                    photoItems.isEmpty() && !isLoading && !isLoadingPhotos -> {
                        ImageCard {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_photo_library),
                                    noPhotosYetText,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color(0xFF4CAF50)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    noPhotosYetText,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1B5E20)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    takeFirstPhotoText,
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.heightIn(max = 500.dp)
                        ) {
                            items(photoItems) { photo ->
                                PhotoCardWithRealImage(
                                    photoUrl = photo.url,
                                    formattedTime = formatDateTime(photo.time),
                                    onClick = { openPhoto(photo.url, photo.time) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showPhotoDialog && selectedPhotoUrl.isNotEmpty()) {
        Dialog(
            onDismissRequest = { showPhotoDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context).data(selectedPhotoUrl).crossfade(true).build(),
                        contentDescription = plantGalleryText,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                    IconButton(
                        onClick = { showPhotoDialog = false },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, stringResource(R.string.cancel), tint = Color.White)
                    }
                    Text(
                        text = formatDateTime(selectedPhotoTime),
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    )
                }
            }
        }
    }

    if (showPhotoGuideDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoGuideDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_camera_alt),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = Color(0xFF4CAF50)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = photoGuideTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = photoGuideContent,
                        fontSize = 14.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = photoGuideTakePhoto,
                                fontSize = 13.sp,
                                color = Color(0xFF1565C0),
                                lineHeight = 18.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = photoGuideGallery,
                                fontSize = 13.sp,
                                color = Color(0xFF6A1B9A),
                                lineHeight = 18.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = photoGuideAutoInterval,
                                fontSize = 13.sp,
                                color = Color(0xFF2E7D32),
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = photoGuideIntervalList,
                                fontSize = 12.sp,
                                color = Color(0xFF2E7D32),
                                lineHeight = 16.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = photoGuideHowToSave,
                            fontSize = 13.sp,
                            color = Color(0xFFE65100),
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = photoGuideNote,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPhotoGuideDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(stringResource(R.string.got_it), color = Color.White)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun ImageCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) { content() }
}

@Composable
fun PhotoCardWithRealImage(photoUrl: String, formattedTime: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Plant photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(8.dp)
            ) {
                Text(
                    text = formattedTime,
                    fontSize = 11.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ImageScreenPreview() {
    SmartPlantBoxTheme { ImageScreen() }
}