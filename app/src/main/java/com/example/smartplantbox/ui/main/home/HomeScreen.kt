package com.example.smartplantbox.ui.main.home

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartplantbox.R
import com.example.smartplantbox.data.repository.AuthRepositoryImpl
import com.example.smartplantbox.presentation.auth.AuthViewModel
import com.example.smartplantbox.ui.theme.SmartPlantBoxTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import androidx.core.content.edit

@Composable
fun HomeScreen(onNavigateToPotSettings: (String, String) -> Unit) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel()
    val scope = rememberCoroutineScope()

    val noDeviceText = stringResource(R.string.no_device)
    val noDeviceDescText = stringResource(R.string.no_device_desc)
    val connectDeviceText = stringResource(R.string.connect_device)
    val addMoreDeviceText = stringResource(R.string.add_more_device)
    val connectFirstDeviceText = stringResource(R.string.connect_first_device)
    val deviceKeyText = stringResource(R.string.device_key)
    val max11CharsText = stringResource(R.string.max_11_chars)
    val connectText = stringResource(R.string.connect)
    val saveText = stringResource(R.string.save)
    val cancelText = stringResource(R.string.cancel)
    val deleteText = stringResource(R.string.delete)
    val addText = stringResource(R.string.add)
    val closeText = stringResource(R.string.close)
    val backgroundText = stringResource(R.string.background)
    val noDeviceIconText = stringResource(R.string.no_device_icon)
    val connectYourFirstDeviceText = stringResource(R.string.connect_your_first_device)
    val addNewDeviceText = stringResource(R.string.add_new_device)
    val enterDeviceSerialNumberText = stringResource(R.string.enter_device_serial_number)
    val enterDeviceKeyPlaceholderText = stringResource(R.string.enter_device_key_placeholder)
    val max11CharactersText = stringResource(R.string.max_11_characters)
    val networkErrorText = stringResource(R.string.network_error)

    val noKeysFoundText = stringResource(R.string.no_keys_found)

    val onionText = stringResource(R.string.onion)
    val garlicText = stringResource(R.string.garlic)
    val cucumberText = stringResource(R.string.cucumber)
    val tomatoText = stringResource(R.string.tomato)
    val carrotText = stringResource(R.string.carrot)
    val onionDescText = stringResource(R.string.onion_description)
    val garlicDescText = stringResource(R.string.garlic_description)
    val cucumberDescText = stringResource(R.string.cucumber_description)
    val tomatoDescText = stringResource(R.string.tomato_description)
    val carrotDescText = stringResource(R.string.carrot_description)
    val guideText = stringResource(R.string.guide)

    var devices by remember { mutableStateOf<List<DeviceItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var showAddDeviceDialog by remember { mutableStateOf(false) }
    var newDeviceKey by remember { mutableStateOf("") }
    var newDeviceKeyError by remember { mutableStateOf<String?>(null) }

    var showGuideDialog by remember { mutableStateOf(false) }
    var selectedPlantGuide by remember { mutableStateOf<PlantGuide?>(null) }

    suspend fun isDeviceActive(deviceSn: String, token: String): Boolean {
        return try {
            val repository = AuthRepositoryImpl()
            val response = repository.getAirData(deviceSn, token)
            response.success && response.data != null
        } catch (e: Exception) {
            false
        }
    }

    suspend fun loadDevices(
        context: Context,
        authViewModel: AuthViewModel,
        onResult: (List<DeviceItem>, String?) -> Unit
    ) {
        try {
            val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val email = prefs.getString("user_email", null)
            val token = prefs.getString("jwt_token", null)
            if (email == null || token == null) {
                onResult(emptyList(), null)
                return
            }

            val response = AuthRepositoryImpl().getBoundDevices(email, token)
            if (response.success && response.keys != null) {
                val savedNames = loadDeviceNames(context, response.keys)
                val devicesWithStatus = mutableListOf<DeviceItem>()

                for ((index, key) in response.keys.withIndex()) {
                    val isActive = isDeviceActive(key, token)
                    devicesWithStatus.add(
                        DeviceItem(
                            name = savedNames[key] ?: "Device ${index + 1}",
                            sn = key,
                            isActive = isActive
                        )
                    )
                }
                onResult(devicesWithStatus, null)
            } else {
                val errorMsg = if (response.message == "No keys found for this email") {
                    noKeysFoundText
                } else {
                    response.message
                }
                onResult(emptyList(), errorMsg)
            }
        } catch (e: Exception) {
            onResult(emptyList(), "${context.getString(R.string.network_error)}: ${e.message}")
        }
    }

    LaunchedEffect(Unit) {
        loadDevices(context, authViewModel) { loadedDevices, error ->
            errorMessage = error
            devices = loadedDevices
            isLoading = false
        }
    }

    val plants = listOf(
        PlantItem(onionText, onionDescText, emoji = "🧅", guide = PlantGuide(stringResource(R.string.onion_guide_title), stringResource(R.string.onion_guide))),
        PlantItem(garlicText, garlicDescText, emoji = "🧄", guide = PlantGuide(stringResource(R.string.garlic_guide_title), stringResource(R.string.garlic_guide))),
        PlantItem(cucumberText, cucumberDescText, emoji = "🥒", guide = PlantGuide(stringResource(R.string.cucumber_guide_title), stringResource(R.string.cucumber_guide))),
        PlantItem(tomatoText, tomatoDescText, emoji = "🍅", guide = PlantGuide(stringResource(R.string.tomato_guide_title), stringResource(R.string.tomato_guide))),
        PlantItem(carrotText, carrotDescText, emoji = "🥕", guide = PlantGuide(stringResource(R.string.carrot_guide_title), stringResource(R.string.carrot_guide)))
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.top_overlay),
            contentDescription = backgroundText,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)))

        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            if (errorMessage != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Text(errorMessage!!, color = Color(0xFFD32F2F), modifier = Modifier.padding(12.dp), fontSize = 14.sp)
                    }
                }
            }

            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (devices.isEmpty()) {
                item {
                    NoDeviceCard(onAddDeviceClick = {
                        newDeviceKey = ""; newDeviceKeyError = null; showAddDeviceDialog = true
                    })
                }
            } else {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        devices.forEachIndexed { index, device ->
                            DeviceCard(
                                device = device,
                                onNameChange = { newName ->
                                    val updated = devices.toMutableList().also { it[index] = device.copy(name = newName) }
                                    devices = updated
                                    saveDeviceNames(context, devices)
                                },
                                onDeleteClick = {
                                    scope.launch {
                                        deleteDevice(context, device.sn,
                                            onSuccess = {
                                                devices = devices.toMutableList().also { it.removeAt(index) }
                                                saveDeviceNames(context, devices)
                                            },
                                            onError = { errorMessage = it }
                                        )
                                    }
                                },
                                onSettingsClick = { onNavigateToPotSettings(device.name, device.sn) }
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), contentAlignment = Alignment.Center) {
                    Button(
                        onClick = { newDeviceKey = ""; newDeviceKeyError = null; showAddDeviceDialog = true },
                        modifier = Modifier.width(300.dp).height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Icon(Icons.Default.Add, addText, tint = Color(0xFFE8F5E9))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (devices.isEmpty()) connectFirstDeviceText else addMoreDeviceText,
                            color = Color(0xFFE8F5E9), fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            items(plants) { plant ->
                PlantCard(plant = plant, onGuideClick = { selectedPlantGuide = plant.guide; showGuideDialog = true })
            }
        }
    }

    if (showAddDeviceDialog) {
        AlertDialog(
            onDismissRequest = { showAddDeviceDialog = false },
            title = {
                Text(
                    text = if (devices.isEmpty()) connectYourFirstDeviceText else addNewDeviceText,
                    fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20)
                )
            },
            text = {
                Column {
                    Text(enterDeviceSerialNumberText, fontSize = 14.sp, color = Color.Black, modifier = Modifier.padding(bottom = 8.dp))
                    OutlinedTextField(
                        value = newDeviceKey,
                        onValueChange = { v ->
                            val filtered = v.filter { it.isLetterOrDigit() }
                            if (filtered.length <= 11) { newDeviceKey = filtered; newDeviceKeyError = null }
                            else newDeviceKeyError = max11CharactersText
                        },
                        label = { Text(deviceKeyText, color = Color.Black) },
                        placeholder = { Text(enterDeviceKeyPlaceholderText, color = Color.Black) },
                        singleLine = true,
                        isError = newDeviceKeyError != null,
                        supportingText = {
                            Text(newDeviceKeyError ?: max11CharsText, fontSize = if (newDeviceKeyError != null) 12.sp else 10.sp,
                                color = if (newDeviceKeyError != null) MaterialTheme.colorScheme.error else Color.Black)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4CAF50),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            errorBorderColor = Color(0xFFD32F2F),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            disabledTextColor = Color.Black
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newDeviceKey.isNotBlank() && newDeviceKey.length <= 11) {
                            scope.launch {
                                addDevice(context, newDeviceKey,
                                    onSuccess = { newDevice ->
                                        devices = devices + newDevice
                                        saveDeviceNames(context, devices)
                                        showAddDeviceDialog = false
                                    },
                                    onError = { newDeviceKeyError = it }
                                )
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(8.dp),
                    enabled = newDeviceKey.isNotBlank() && newDeviceKey.length <= 11
                ) { Text(connectText, color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showAddDeviceDialog = false }, colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFD32F2F))) {
                    Text(cancelText)
                }
            },
            containerColor = Color.White, shape = RoundedCornerShape(16.dp)
        )
    }

    if (showGuideDialog && selectedPlantGuide != null) {
        AlertDialog(
            onDismissRequest = { showGuideDialog = false },
            title = { Text(selectedPlantGuide!!.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20)) },
            text = {
                Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                    Text(selectedPlantGuide!!.content, fontSize = 14.sp, color = Color.Black, lineHeight = 22.sp)
                }
            },
            confirmButton = {
                Button(onClick = { showGuideDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), shape = RoundedCornerShape(8.dp)) {
                    Text(closeText, color = Color.White)
                }
            },
            containerColor = Color.White, shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun NoDeviceCard(onAddDeviceClick: () -> Unit) {
    val noDeviceText = stringResource(R.string.no_device)
    val noDeviceDescText = stringResource(R.string.no_device_desc)
    val connectDeviceText = stringResource(R.string.connect_device)
    val addText = stringResource(R.string.add)
    val noDeviceIconText = stringResource(R.string.no_device_icon)

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(200.dp).background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9)))
            )
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Icon(Icons.Default.Warning, noDeviceIconText, tint = Color(0xFF4CAF50), modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(noDeviceText, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                Spacer(modifier = Modifier.height(4.dp))
                Text(noDeviceDescText, fontSize = 14.sp, color = Color.Black, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onAddDeviceClick, shape = RoundedCornerShape(24.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) {
                    Icon(Icons.Default.Add, addText, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(connectDeviceText, color = Color.White, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun DeviceCard(
    device: DeviceItem,
    onNameChange: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val activeText = stringResource(R.string.active)
    val inactiveText = stringResource(R.string.inactive)
    val editDeviceNameText = stringResource(R.string.edit_device_name)
    val deviceNameText = stringResource(R.string.device_name)
    val max16CharsText = stringResource(R.string.max_16_chars)
    val nameCannotBeEmptyText = stringResource(R.string.name_cannot_be_empty)
    val saveText = stringResource(R.string.save)
    val cancelText = stringResource(R.string.cancel)
    val deleteDeviceTitleText = stringResource(R.string.delete_device_title)
    val deleteDeviceConfirmFmt = stringResource(R.string.delete_device_confirm)
    val deleteText = stringResource(R.string.delete)
    val deleteDeviceText = stringResource(R.string.delete_device)
    val settingsPottyText = stringResource(R.string.settings_current_potty)
    val cardBgText = stringResource(R.string.card_background)
    val deviceIconText = stringResource(R.string.device_icon)

    var showEditDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf(device.name) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val statusColor = if (device.isActive) Color(0xFF4CAF50) else Color(0xFFD32F2F)
    val statusText = if (device.isActive) activeText else inactiveText

    Card(
        modifier = Modifier.width(280.dp).height(200.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(painterResource(R.drawable.device_card_bg), cardBgText, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)

            Image(
                painter = painterResource(R.drawable.device_center_icon),
                deviceIconText,
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.Center)
                    .clickable { onSettingsClick() },
                contentScale = ContentScale.Fit
            )

            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(statusColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            statusText,
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(device.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White,
                        modifier = Modifier.clickable { tempName = device.name; nameError = null; showEditDialog = true })
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.background(Color(0xFF163C25), RoundedCornerShape(12.dp)).border(1.dp, Color.White, RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text(device.sn, fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Medium)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, deleteDeviceText, tint = Color(0xFFD32F2F), modifier = Modifier.size(24.dp))
                        }
                        IconButton(onClick = onSettingsClick, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Build, settingsPottyText, tint = Color(0xFF4CAF50), modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(editDeviceNameText, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20)) },
            text = {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { v ->
                        val f = v.filter { it.isLetterOrDigit() || it.isWhitespace() }
                        if (f.length <= 16) { tempName = f; nameError = null } else nameError = max16CharsText
                    },
                    label = { Text(deviceNameText, color = Color.Black) },
                    singleLine = true,
                    isError = nameError != null,
                    supportingText = { Text(nameError ?: max16CharsText, fontSize = if (nameError != null) 12.sp else 10.sp,
                        color = if (nameError != null) MaterialTheme.colorScheme.error else Color.Black) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4CAF50),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        disabledTextColor = Color.Black
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = { if (tempName.isNotBlank() && tempName.length <= 16) { onNameChange(tempName.trim()); showEditDialog = false } else if (tempName.isBlank()) nameError = nameCannotBeEmptyText },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(8.dp),
                    enabled = tempName.isNotBlank() && tempName.length <= 16
                ) { Text(saveText, color = Color.White) }
            },
            dismissButton = { TextButton(onClick = { showEditDialog = false }, colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFD32F2F))) { Text(cancelText) } },
            containerColor = Color.White, shape = RoundedCornerShape(16.dp)
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(deleteDeviceTitleText, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F)) },
            text = { Text(String.format(deleteDeviceConfirmFmt, device.sn), fontSize = 14.sp, color = Color.Black) },
            confirmButton = {
                Button(onClick = { onDeleteClick(); showDeleteDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)), shape = RoundedCornerShape(8.dp)) {
                    Text(deleteText, color = Color.White)
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }, colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF4CAF50))) { Text(cancelText) } },
            containerColor = Color.White, shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun PlantCard(plant: PlantItem, onGuideClick: () -> Unit) {
    val guideText = stringResource(R.string.guide)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Text(plant.emoji ?: "🌿", fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    plant.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    plant.description,
                    fontSize = 12.sp,
                    color = Color.Black,
                    maxLines = 4,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onGuideClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .width(48.dp)
                    .height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = guideText,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

data class PlantItem(val name: String, val description: String, val imageRes: Int? = null, val emoji: String? = null, val guide: PlantGuide = PlantGuide("", ""))
data class PlantGuide(val title: String, val content: String)
data class DeviceItem(val name: String, val sn: String, val isActive: Boolean)

fun saveDeviceNames(context: Context, devices: List<DeviceItem>) {
    val json = Gson().toJson(devices.associate { it.sn to it.name })
    context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit {
        putString("device_names", json)
    }
}

fun loadDeviceNames(context: Context, devicesSn: List<String>): Map<String, String> {
    val json = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE).getString("device_names", "{}")
    return Gson().fromJson(json, object : TypeToken<Map<String, String>>() {}.type)
}

suspend fun isDeviceActive(deviceSn: String, token: String): Boolean {
    return try {
        val repository = AuthRepositoryImpl()
        val response = repository.getAirData(deviceSn, token)
        response.success && response.data != null
    } catch (e: Exception) {
        false
    }
}

suspend fun loadDevices(context: Context, authViewModel: AuthViewModel, onResult: (List<DeviceItem>, String?) -> Unit) {
    try {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val email = prefs.getString("user_email", null)
        val token = prefs.getString("jwt_token", null)
        if (email == null || token == null) {
            onResult(emptyList(), null)
            return
        }

        val response = AuthRepositoryImpl().getBoundDevices(email, token)
        if (response.success && response.keys != null) {
            val savedNames = loadDeviceNames(context, response.keys)
            val devicesWithStatus = mutableListOf<DeviceItem>()

            for ((index, key) in response.keys.withIndex()) {
                val isActive = isDeviceActive(key, token)
                devicesWithStatus.add(
                    DeviceItem(
                        name = savedNames[key] ?: "Device ${index + 1}",
                        sn = key,
                        isActive = isActive
                    )
                )
            }
            onResult(devicesWithStatus, null)
        } else {
            val noKeysFoundText = context.getString(R.string.no_keys_found)
            val errorMsg = if (response.message == "No keys found for this email") {
                noKeysFoundText
            } else {
                response.message
            }
            onResult(emptyList(), errorMsg)
        }
    } catch (e: Exception) {
        onResult(emptyList(), "${context.getString(R.string.network_error)}: ${e.message}")
    }
}

suspend fun addDevice(context: Context, deviceKey: String, onSuccess: (DeviceItem) -> Unit, onError: (String) -> Unit) {
    try {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val email = prefs.getString("user_email", null)
        val token = prefs.getString("jwt_token", null)
        if (email == null || token == null) {
            onError(context.getString(R.string.user_not_logged_in))
            return
        }

        val (_, response) = AuthRepositoryImpl().bindDevice(deviceKey, email, token)
        if (response.success) {
            val isActive = isDeviceActive(deviceKey, token)
            onSuccess(DeviceItem(name = "New Device", sn = deviceKey, isActive = isActive))
        } else {
            onError(response.message ?: context.getString(R.string.failed_to_bind_device))
        }
    } catch (e: Exception) {
        onError("${context.getString(R.string.network_error)}: ${e.message}")
    }
}

suspend fun deleteDevice(context: Context, deviceKey: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
    try {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val email = prefs.getString("user_email", null)
        val token = prefs.getString("jwt_token", null)
        if (email == null || token == null) {
            onError(context.getString(R.string.user_not_logged_in))
            return
        }

        val (_, response) = AuthRepositoryImpl().unbindDevice(deviceKey, email, token)
        if (response.success) {
            onSuccess()
        } else {
            onError(response.message ?: context.getString(R.string.failed_to_unbind_device))
        }
    } catch (e: Exception) {
        onError("${context.getString(R.string.network_error)}: ${e.message}")
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    SmartPlantBoxTheme { HomeScreen(onNavigateToPotSettings = { _, _ -> }) }
}