package com.example.smartplantbox.ui.main.potsettings

import android.content.Context
import android.graphics.Color.green
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartplantbox.R
import com.example.smartplantbox.data.repository.AuthRepositoryImpl
import com.example.smartplantbox.ui.theme.SmartPlantBoxTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Sensor data snapshot loaded from the server
data class DeviceData(
    val airTemperature: Int,
    val airHumidity: Int,
    val lightLevel: Int,
    val lightThreshold: Double,
    val isLampOn: Boolean,
    val soilHumidity: Double,
    val soilThreshold: Double,
    val waterTank: String,
    val isWateringOn: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PotSettingsScreen(
    onBackClick: () -> Unit,
    onSettingsSaved: () -> Unit = {},
    deviceName: String,
    deviceSN: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { AuthRepositoryImpl() }

    val potSettingsText = stringResource(R.string.pot_settings)
    val backText = stringResource(R.string.back)
    val deviceInfoText = stringResource(R.string.device_info)
    val snText = stringResource(R.string.sn)
    val updateText = stringResource(R.string.update)
    val airTemperatureText = stringResource(R.string.air_temperature)
    val airHumidityText = stringResource(R.string.air_humidity)
    val lightText = stringResource(R.string.light)
    val currentLightingLevelText = stringResource(R.string.current_lighting_level)
    val onText = stringResource(R.string.on)
    val offText = stringResource(R.string.off)
    val setLightThresholdText = stringResource(R.string.set_light_threshold)
    val yourLightThresholdText = stringResource(R.string.your_light_threshold)
    val soilMoistureText = stringResource(R.string.soil_moisture)
    val currentSoilMoistureText = stringResource(R.string.current_soil_moisture)
    val waterTankText = stringResource(R.string.water_tank)
    val wateringText = stringResource(R.string.watering)
    val setSoilMoistureText = stringResource(R.string.set_soil_moisture)
    val yourSoilMoistureText = stringResource(R.string.your_soil_moisture)
    val setTheSettingsText = stringResource(R.string.set_the_settings)
    val failedToLoadDeviceDataText = stringResource(R.string.failed_to_load_device_data)
    val deviceDataUpdatedSuccessText = stringResource(R.string.device_data_updated_successfully)
    val failedToUpdateDeviceDataText = stringResource(R.string.failed_to_update_device_data)
    val dataHasBeenSavedText = stringResource(R.string.data_has_been_successfully_saved)
    val failedToSetLightThresholdText = stringResource(R.string.failed_to_set_light_threshold)
    val failedToSetSoilHumidityText = stringResource(R.string.failed_to_set_soil_humidity)
    val failedToSetLampStatusText = stringResource(R.string.failed_to_set_lamp_status)
    val failedToReloadDeviceDataText = stringResource(R.string.failed_to_reload_device_data)
    val userNotLoggedInText = stringResource(R.string.user_not_logged_in)
    val networkErrorText = stringResource(R.string.network_error)
    val yesText = stringResource(R.string.yes)
    val noText = stringResource(R.string.no)
    val supplementalLightingText = stringResource(R.string.lamp_status_label)

    val lightDialogTitle = stringResource(R.string.light_dialog_title)
    val lightDialogShort = stringResource(R.string.light_dialog_short)
    val lightDialogDescription = stringResource(R.string.light_dialog_description)
    val lightDialogRecommendation = stringResource(R.string.light_dialog_recommendation)
    val lightDialogCurrent = stringResource(R.string.light_dialog_current)

    val soilDialogTitle = stringResource(R.string.soil_dialog_title)
    val soilDialogShort = stringResource(R.string.soil_dialog_short)
    val soilDialogDescription = stringResource(R.string.soil_dialog_description)
    val soilDialogRecommendation = stringResource(R.string.soil_dialog_recommendation)
    val soilDialogWaterTank = stringResource(R.string.soil_dialog_water_tank)
    val soilDialogWatering = stringResource(R.string.soil_dialog_watering)
    val soilDialogCurrentThreshold = stringResource(R.string.soil_dialog_current_threshold)

    val gotItText = stringResource(R.string.got_it)

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    var isUpdating by remember { mutableStateOf(false) }

    var isLampOn by remember { mutableStateOf(false) }
    var airTemperature by remember { mutableIntStateOf(0) }
    var airHumidity by remember { mutableIntStateOf(0) }
    var lightLevel by remember { mutableIntStateOf(0) }
    var waterTank by remember { mutableStateOf(noText) }
    var isWateringOn by remember { mutableStateOf(false) }

    var realSoilHumidity by remember { mutableIntStateOf(0) }

    var soilThresholdFromApi by remember { mutableIntStateOf(60) }

    var tempSoilThreshold by remember { mutableIntStateOf(60) }

    var lightThreshold by remember { mutableFloatStateOf(50f) }

    var showLightInfoDialog by remember { mutableStateOf(false) }
    var showSoilInfoDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    fun applyDeviceData(data: DeviceData) {
        airTemperature = data.airTemperature
        airHumidity = data.airHumidity
        lightLevel = data.lightLevel
        lightThreshold = data.lightThreshold.toFloat()
        isLampOn = data.isLampOn
        waterTank = if (data.waterTank.equals("YES", ignoreCase = true)) yesText else noText
        isWateringOn = data.isWateringOn
        realSoilHumidity = data.soilHumidity.toInt()
        soilThresholdFromApi = data.soilThreshold.toInt()
        tempSoilThreshold = soilThresholdFromApi
    }

    suspend fun reload() {
        loadDeviceData(context, repository, deviceSN) { data ->
            if (data != null) applyDeviceData(data)
            else errorMessage = failedToReloadDeviceDataText
        }
    }

    LaunchedEffect(Unit) {
        loadDeviceData(context, repository, deviceSN) { data ->
            if (data != null) applyDeviceData(data)
            else errorMessage = failedToLoadDeviceDataText
            isLoading = false
        }
    }

    fun updateDeviceData() {
        scope.launch {
            isUpdating = true; errorMessage = null
            val token = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                .getString("jwt_token", null)
            if (token == null) {
                errorMessage = userNotLoggedInText; isUpdating = false
                snackbarHostState.showSnackbar(userNotLoggedInText, duration = SnackbarDuration.Short)
                return@launch
            }
            try {
                val response = repository.sendCommand(deviceSN, "update", token)
                if (response.success) {
                    snackbarHostState.showSnackbar(deviceDataUpdatedSuccessText, duration = SnackbarDuration.Short)
                    delay(500); reload()
                } else {
                    errorMessage = response.message ?: failedToUpdateDeviceDataText
                    snackbarHostState.showSnackbar("$failedToUpdateDeviceDataText: ${response.message}", duration = SnackbarDuration.Short)
                }
            } catch (e: Exception) {
                errorMessage = "$networkErrorText: ${e.message}"
                snackbarHostState.showSnackbar("$networkErrorText: ${e.message}", duration = SnackbarDuration.Short)
            } finally { isUpdating = false }
        }
    }

    fun saveSettings() {
        scope.launch {
            isSaving = true; errorMessage = null
            val token = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                .getString("jwt_token", null)
            if (token == null) { errorMessage = userNotLoggedInText; isSaving = false; return@launch }

            try {
                // 1 — Light threshold
                val lightCommand = "light_threshold:${lightThreshold.toInt()}"
                println("🔧 Sending light command: $lightCommand")
                val lightResp = repository.sendCommand(deviceSN, lightCommand, token)
                if (!lightResp.success) {
                    snackbarHostState.showSnackbar("$networkErrorText: ${lightResp.message ?: failedToSetLightThresholdText}", duration = SnackbarDuration.Short)
                    isSaving = false; return@launch
                }

                // 2 — Soil threshold
                val soilCommand = "soil_threshold:${tempSoilThreshold}"
                println("🔧 Sending soil command: $soilCommand")
                val soilResp = repository.sendCommand(deviceSN, soilCommand, token)
                if (!soilResp.success) {
                    snackbarHostState.showSnackbar("$networkErrorText: ${soilResp.message ?: failedToSetSoilHumidityText}", duration = SnackbarDuration.Short)
                    isSaving = false; return@launch
                }

                soilThresholdFromApi = tempSoilThreshold

                // 3 — Lamp status
                val lampResp = repository.setLampStatus(deviceSN, isLampOn, token)
                if (!lampResp.success) {
                    snackbarHostState.showSnackbar("$networkErrorText: ${lampResp.message ?: failedToSetLampStatusText}", duration = SnackbarDuration.Short)
                    isSaving = false; return@launch
                }

                snackbarHostState.showSnackbar(dataHasBeenSavedText, duration = SnackbarDuration.Short)
                delay(1700)
                onSettingsSaved()
            } catch (e: Exception) {
                errorMessage = "$networkErrorText: ${e.message}"
                snackbarHostState.showSnackbar("$networkErrorText: ${e.message}", duration = SnackbarDuration.Short)
            } finally { isSaving = false }
        }
    }

    // UI
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.top_overlay),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 48.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, backText, tint = Color.White)
                    }
                    Text(potSettingsText, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    Spacer(modifier = Modifier.size(48.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else {
                    if (errorMessage != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                        ) {
                            Text(errorMessage!!, color = Color(0xFFD32F2F), modifier = Modifier.padding(12.dp))
                        }
                    }

                    SettingsCard {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = deviceInfoText,
                                    modifier = Modifier.size(32.dp),
                                    tint = Color(0xFF1B5E20)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(deviceName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                                    Text("$snText: $deviceSN", fontSize = 14.sp, color = Color.Gray)
                                }
                            }
                            Button(
                                onClick = { updateDeviceData() },
                                enabled = !isUpdating,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.width(70.dp).height(40.dp)
                            ) {
                                if (isUpdating) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(painterResource(R.drawable.ic_system_update_alt), updateText, modifier = Modifier.size(20.dp), tint = Color.White)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SettingsCard {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(painterResource(R.drawable.ic_thermostat), airTemperatureText, modifier = Modifier.size(35.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${airTemperature}°C", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                                    Text(airTemperatureText, fontSize = 14.sp, color = Color.Gray)
                                }
                            }
                            Box(modifier = Modifier.width(1.dp).height(60.dp).background(Color(0xFFE0E0E0)))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(painterResource(R.drawable.ic_opacity), airHumidityText, modifier = Modifier.size(34.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${airHumidity}%", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                                    Text(airHumidityText, fontSize = 14.sp, color = Color.Gray)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SettingsCard {
                        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.ic_lightbulb),
                                    contentDescription = lightText,
                                    modifier = Modifier.size(28.dp),
                                    contentScale = ContentScale.Fit
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = lightText,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1B5E20)
                                )
                            }

                            SensorRow(
                                label = currentLightingLevelText,
                                value = "$lightLevel%"
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            SensorRow(
                                label = supplementalLightingText,
                                value = if (isLampOn) onText else offText,
                                valueColor = if (isLampOn) Color(0xFF4CAF50) else Color(0xFFD32F2F)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SettingsCard {
                        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(painterResource(R.drawable.ic_wb_sunny), setLightThresholdText, modifier = Modifier.size(32.dp), contentScale = ContentScale.Fit)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(setLightThresholdText, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1B5E20))
                                }
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = "Info",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable { showLightInfoDialog = true },
                                    tint = Color(0xFF1B5E20)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(yourLightThresholdText, fontSize = 16.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
                            Slider(
                                value = lightThreshold,
                                onValueChange = { lightThreshold = it },
                                valueRange = 0f..100f,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFF4CAF50), activeTrackColor = Color(0xFF4CAF50))
                            )
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("0%", fontSize = 12.sp, color = Color.Gray)
                                Text("${lightThreshold.toInt()}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                                Text("100%", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SettingsCard {
                        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                                Image(painterResource(R.drawable.ic_water_drop), soilMoistureText, modifier = Modifier.size(28.dp), contentScale = ContentScale.Fit)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(soilMoistureText, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1B5E20))
                            }
                            SensorRow(label = currentSoilMoistureText, value = "${realSoilHumidity}%")
                            Spacer(modifier = Modifier.height(12.dp))
                            SensorRow(
                                label = "$waterTankText:",
                                value = waterTank,
                                valueColor = if (waterTank == yesText) Color(0xFF4CAF50) else Color(0xFFD32F2F)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            SensorRow(
                                label = "$wateringText:",
                                value = if (isWateringOn) onText else offText,
                                valueColor = if (isWateringOn) Color(0xFF4CAF50) else Color(0xFFD32F2F)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SettingsCard {
                        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(painterResource(R.drawable.ic_set_soil_humid), setSoilMoistureText, modifier = Modifier.size(34.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(setSoilMoistureText, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1B5E20))
                                }
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = "Info",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable { showSoilInfoDialog = true },
                                    tint = Color(0xFF1B5E20)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("$yourSoilMoistureText: ${tempSoilThreshold}%", fontSize = 16.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
                            Slider(
                                value = tempSoilThreshold.toFloat(),
                                onValueChange = { tempSoilThreshold = it.toInt() },
                                valueRange = 0f..100f,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFF4CAF50), activeTrackColor = Color(0xFF4CAF50))
                            )
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("0%", fontSize = 12.sp, color = Color.Gray)
                                Text("${tempSoilThreshold}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                                Text("100%", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { saveSettings() },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            disabledContainerColor = Color(0xFFA5D6A7)
                        ),
                        enabled = !isSaving && !isUpdating
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(setTheSettingsText, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    if (showLightInfoDialog) {
        AlertDialog(
            onDismissRequest = { showLightInfoDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_lightbulb),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = Color(0xFF4CAF50)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = lightDialogTitle,
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
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = lightDialogShort,
                            fontSize = 14.sp,
                            color = Color(0xFF1565C0),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = lightDialogDescription,
                        fontSize = 13.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = lightDialogRecommendation,
                            fontSize = 13.sp,
                            color = Color(0xFF2E7D32),
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = String.format(lightDialogCurrent, lightLevel),
                        fontSize = 13.sp,
                        color = Color(0xFF1B5E20),
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showLightInfoDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(gotItText, color = Color.White)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showSoilInfoDialog) {
        AlertDialog(
            onDismissRequest = { showSoilInfoDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_set_soil_humid),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = Color(0xFF4CAF50)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = soilDialogTitle,
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
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = soilDialogShort,
                            fontSize = 14.sp,
                            color = Color(0xFF1565C0),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = soilDialogDescription,
                        fontSize = 13.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = soilDialogRecommendation,
                            fontSize = 13.sp,
                            color = Color(0xFF2E7D32),
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = String.format(soilDialogCurrentThreshold, tempSoilThreshold),
                        fontSize = 13.sp,
                        color = Color(0xFF1B5E20),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = Color(0xFFE0E0E0))
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "$soilDialogWaterTank: $waterTank",
                        fontSize = 13.sp,
                        color = if (waterTank == yesText) Color(0xFF4CAF50) else Color(0xFFD32F2F)
                    )
                    Text(
                        text = "$soilDialogWatering: ${if (isWateringOn) onText else offText}",
                        fontSize = 13.sp,
                        color = if (isWateringOn) Color(0xFF4CAF50) else Color(0xFFD32F2F)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSoilInfoDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(gotItText, color = Color.White)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) { content() }
}

@Composable
private fun SensorRow(label: String, value: String, valueColor: Color = Color(0xFF1B5E20)) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

suspend fun loadDeviceData(
    context: Context,
    repository: AuthRepositoryImpl,
    deviceKey: String,
    onResult: (DeviceData?) -> Unit
) {
    try {
        val token = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getString("jwt_token", null) ?: return onResult(null)

        val airData = runCatching { repository.getAirData(deviceKey, token) }.getOrNull()
        val lightData = runCatching { repository.getLightData(deviceKey, token) }.getOrNull()
        val soilData = runCatching { repository.getSoilData(deviceKey, token) }.getOrNull()

        if (airData?.success == true && airData.data != null &&
            lightData?.success == true && lightData.data != null &&
            soilData?.success == true && soilData.data != null
        ) {
            onResult(DeviceData(
                airTemperature = airData.data.air_temp.toInt(),
                airHumidity = airData.data.air_humid.toInt(),
                lightLevel = lightData.data.light.toInt(),
                lightThreshold = lightData.data.light_threshold,
                isLampOn = lightData.data.lamp == "ON",
                soilHumidity = soilData.data.soil_humid,
                soilThreshold = soilData.data.soil_threshold,
                waterTank = soilData.data.water,
                isWateringOn = soilData.data.watering == "ON"
            ))
        } else {
            onResult(null)
        }
    } catch (_: Exception) {
        onResult(null)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PotSettingsScreenPreview() {
    SmartPlantBoxTheme {
        PotSettingsScreen(onBackClick = {}, onSettingsSaved = {}, deviceName = "Onion", deviceSN = "8zL2Xq1111")
    }
}