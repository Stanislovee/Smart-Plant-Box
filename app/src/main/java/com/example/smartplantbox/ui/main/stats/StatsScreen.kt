package com.example.smartplantbox.ui.main.stats

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartplantbox.R
import com.example.smartplantbox.data.repository.AuthRepositoryImpl
import com.example.smartplantbox.domain.model.PlantHistoryData
import com.example.smartplantbox.ui.theme.SmartPlantBoxTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class MetricOption(val name: String, val unit: String, val color: Color, val icon: String)

data class DeviceDisplayInfo(val name: String, val sn: String) {
    fun getDisplayText(): String = "$name ($sn)"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { AuthRepositoryImpl() }

    val statisticsText = stringResource(R.string.statistics)
    val analiseText = stringResource(R.string.analise_statistic)
    val deviceText = stringResource(R.string.device)
    val periodText = stringResource(R.string.period)
    val metricText = stringResource(R.string.metric)
    val dayText = stringResource(R.string.day)
    val weekText = stringResource(R.string.week)
    val monthText = stringResource(R.string.month)
    val allText = stringResource(R.string.all)
    val customText = stringResource(R.string.custom)
    val temperatureText = stringResource(R.string.temperature)
    val airHumidityText = stringResource(R.string.air_humidity)
    val soilMoistureText = stringResource(R.string.soil_moisture)
    val lightText = stringResource(R.string.light)
    val celsiusText = stringResource(R.string.celsius)
    val percentText = stringResource(R.string.percent)
    val zoomOutText = stringResource(R.string.zoom_out)
    val zoomInText = stringResource(R.string.zoom_in)
    val averageText = stringResource(R.string.average)
    val minText = stringResource(R.string.min)
    val maxText = stringResource(R.string.max)
    val changeText = stringResource(R.string.change)
    val recentRecordsText = stringResource(R.string.recent_records)
    val noDataAvailableText = stringResource(R.string.no_data_available)
    val connectDeviceWaitText = stringResource(R.string.connect_device_wait)
    val noDataForPeriodText = stringResource(R.string.no_data_for_period)
    val selectStartDateText = stringResource(R.string.select_start_date)
    val selectEndDateText = stringResource(R.string.select_end_date)
    val switchToEndDateText = stringResource(R.string.switch_to_end_date)
    val switchToStartDateText = stringResource(R.string.switch_to_start_date)
    val startDateText = stringResource(R.string.start_date)
    val endDateText = stringResource(R.string.end_date)
    val previousMonthText = stringResource(R.string.previous_month)
    val nextMonthText = stringResource(R.string.next_month)
    val monText = stringResource(R.string.mon)
    val tueText = stringResource(R.string.tue)
    val wedText = stringResource(R.string.wed)
    val thuText = stringResource(R.string.thu)
    val friText = stringResource(R.string.fri)
    val satText = stringResource(R.string.sat)
    val sunText = stringResource(R.string.sun)
    val selectedText = stringResource(R.string.selected)
    val nextText = stringResource(R.string.next)
    val applyText = stringResource(R.string.apply)
    val cancelText = stringResource(R.string.cancel)
    val clearText = stringResource(R.string.clear)
    val updateText = stringResource(R.string.update)
    val dataUpdatedText = stringResource(R.string.data_updated_successfully)
    val failedToUpdateText = stringResource(R.string.failed_to_update_data)
    val failedToLoadText = stringResource(R.string.failed_to_load_history)
    val failedToLoadDevicesText = stringResource(R.string.failed_to_load_devices)
    val userNotLoggedInText = stringResource(R.string.user_not_logged_in)
    val networkErrorText = stringResource(R.string.network_error)
    val lampOnText = stringResource(R.string.lamp_on)
    val lampOffText = stringResource(R.string.lamp_off)
    val waterYesText = stringResource(R.string.water_yes)
    val waterNoText = stringResource(R.string.water_no)
    val wateringOnText = stringResource(R.string.watering_on)
    val wateringOffText = stringResource(R.string.watering_off)
    val retryText = stringResource(R.string.retry)
    val deviceLabelText = stringResource(R.string.device)
    val noDataFoundText = stringResource(R.string.no_data_found)

    val periodInfoTitle = stringResource(R.string.period_info_title)
    val periodInfoContent = stringResource(R.string.period_info_content)
    val periodInfoDay = stringResource(R.string.period_info_day)
    val periodInfoWeek = stringResource(R.string.period_info_week)
    val periodInfoMonth = stringResource(R.string.period_info_month)
    val periodInfoAll = stringResource(R.string.period_info_all)
    val periodInfoCustom = stringResource(R.string.period_info_custom)
    val periodInfoHowTo = stringResource(R.string.period_info_how_to)
    val periodInfoNote = stringResource(R.string.period_info_note)

    var isLoading by remember { mutableStateOf(true) }
    var isUpdating by remember { mutableStateOf(false) }
    var isLoadingDevices by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var historyData by remember { mutableStateOf<List<PlantHistoryData>>(emptyList()) }
    var filteredData by remember { mutableStateOf<List<PlantHistoryData>>(emptyList()) }
    var selectedDeviceSn by remember { mutableStateOf("") }
    var availableDevices by remember { mutableStateOf<List<DeviceDisplayInfo>>(emptyList()) }
    var deviceDropdownExpanded by remember { mutableStateOf(false) }

    var selectedFilter by remember { mutableStateOf(dayText) }
    val filterOptions = listOf(dayText, weekText, monthText, allText, customText)

    var selectedMetric by remember { mutableStateOf(temperatureText) }
    val metricOptions = listOf(
        MetricOption(temperatureText, celsiusText, Color(0xFFFF9800), "🌡️"),
        MetricOption(airHumidityText, percentText, Color(0xFF2196F3), "💧"),
        MetricOption(soilMoistureText, percentText, Color(0xFF4CAF50), "🌱"),
        MetricOption(lightText, percentText, Color(0xFFFFC107), "💡")
    )

    var metricDropdownExpanded by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    var customStartDate by remember { mutableStateOf<Long?>(null) }
    var customEndDate by remember { mutableStateOf<Long?>(null) }
    val nowCal = Calendar.getInstance()
    var startYear by remember { mutableIntStateOf(nowCal.get(Calendar.YEAR)) }
    var startMonth by remember { mutableIntStateOf(nowCal.get(Calendar.MONTH)) }
    var startDay by remember { mutableIntStateOf(nowCal.get(Calendar.DAY_OF_MONTH)) }
    var endYear by remember { mutableIntStateOf(nowCal.get(Calendar.YEAR)) }
    var endMonth by remember { mutableIntStateOf(nowCal.get(Calendar.MONTH)) }
    var endDay by remember { mutableIntStateOf(nowCal.get(Calendar.DAY_OF_MONTH)) }
    var isSelectingStart by remember { mutableStateOf(true) }

    var showPeriodInfoDialog by remember { mutableStateOf(false) }

    val itemsPerPage = 15
    var visibleStartIndex by remember { mutableIntStateOf(0) }
    var visibleEndIndex by remember { mutableIntStateOf(itemsPerPage) }

    fun loadDeviceNamesMap(context: Context): Map<String, String> {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val json = prefs.getString("device_names", "{}")
        return try {
            Gson().fromJson(json, object : TypeToken<Map<String, String>>() {}.type)
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun parseDate(s: String): Date? = try {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(s)
    } catch (_: Exception) { null }

    fun fmtDate(ts: Long?) = if (ts == null) "" else
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(ts))

    fun fmtDatePicker(y: Int, m: Int, d: Int): String {
        val cal = Calendar.getInstance().also { it.set(y, m, d) }
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cal.time)
    }

    fun toTimestamp(y: Int, m: Int, d: Int) =
        Calendar.getInstance().also { it.set(y, m, d, 0, 0, 0) }.timeInMillis

    fun monthName(m: Int) = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )[m]

    fun periodKey(label: String) = when (label) {
        dayText -> "Day"
        weekText -> "Week"
        monthText -> "Month"
        customText -> "Custom"
        else -> "All"
    }
    fun filterByPeriod(data: List<PlantHistoryData>, label: String): List<PlantHistoryData> {
        if (data.isEmpty()) return emptyList()
        return try {
            when (periodKey(label)) {
                "Day" -> {
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    data.filter { it.created_at.substringBefore(" ") == today }
                }
                "Week" -> {
                    val ago = System.currentTimeMillis() - 7 * 86_400_000L
                    data.filter { parseDate(it.created_at)?.time?.let { t -> t >= ago } == true }
                }
                "Month" -> {
                    val ago = System.currentTimeMillis() - 30 * 86_400_000L
                    data.filter { parseDate(it.created_at)?.time?.let { t -> t >= ago } == true }
                }
                "Custom" -> if (customStartDate != null && customEndDate != null)
                    data.filter { parseDate(it.created_at)?.time?.let { t -> t in customStartDate!!..customEndDate!! } == true }
                else data
                else -> data
            }
        } catch (e: Exception) {
            Log.e("StatsScreen", "filterByPeriod", e)
            data
        }
    }

    fun metricValue(d: PlantHistoryData, m: MetricOption) = when (m.name) {
        temperatureText -> d.air_temp
        airHumidityText -> d.air_humid
        soilMoistureText -> d.soil_humid
        lightText -> d.light
        else -> d.air_temp
    }

    fun currentMetric() = metricOptions.find { it.name == selectedMetric } ?: metricOptions[0]

    fun resetViewport(size: Int) {
        visibleStartIndex = 0
        visibleEndIndex = minOf(itemsPerPage, size)
    }

    fun onPan(dx: Float) {
        val total = filteredData.size
        if (total == 0) return
        val count = visibleEndIndex - visibleStartIndex
        val step = maxOf(1, count / 5)
        if (dx > 0 && visibleStartIndex > 0) {
            visibleStartIndex = maxOf(0, visibleStartIndex - step)
            visibleEndIndex = minOf(total, visibleStartIndex + count)
        } else if (dx < 0 && visibleEndIndex < total) {
            visibleStartIndex = minOf(total - count, visibleStartIndex + step)
            visibleEndIndex = minOf(total, visibleStartIndex + count)
        }
    }

    suspend fun loadAvailableDevices(): List<DeviceDisplayInfo> {
        return try {
            val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val email = prefs.getString("user_email", null)
            val token = prefs.getString("jwt_token", null)
            if (email == null || token == null) {
                errorMessage = userNotLoggedInText
                return emptyList()
            }
            val resp = repository.getBoundDevices(email, token)
            if (resp.success && !resp.keys.isNullOrEmpty()) {
                val savedNames = loadDeviceNamesMap(context)
                resp.keys.map { key ->
                    val name = savedNames[key] ?: "Device"
                    DeviceDisplayInfo(name = name, sn = key)
                }
            } else {
                errorMessage = resp.message ?: failedToLoadDevicesText
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("StatsScreen", "loadAvailableDevices", e)
            errorMessage = "$networkErrorText: ${e.message}"
            emptyList()
        }
    }

    suspend fun loadHistory(key: String) {
        if (key.isEmpty()) {
            isLoading = false
            return
        }
        isLoading = true
        errorMessage = null
        try {
            val token = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                .getString("jwt_token", null)
            if (token == null) {
                errorMessage = userNotLoggedInText
                isLoading = false
                return
            }
            val resp = repository.getPlantHistory(key, token)
            if (resp.success && resp.data != null) {
                val sorted = resp.data.sortedByDescending { it.created_at }
                historyData = sorted
                filteredData = filterByPeriod(sorted, selectedFilter)
                resetViewport(filteredData.size)
                if (sorted.isEmpty()) {
                    errorMessage = noDataForPeriodText
                }
            } else {
                errorMessage = resp.message ?: failedToLoadText
                historyData = emptyList()
                filteredData = emptyList()
            }
        } catch (e: Exception) {
            Log.e("StatsScreen", "loadHistory", e)
            errorMessage = "$networkErrorText: ${e.message}"
            historyData = emptyList()
            filteredData = emptyList()
        } finally {
            isLoading = false
        }
    }

    suspend fun refreshDevicesAndHistory() {
        isLoadingDevices = true
        errorMessage = null
        try {
            val devices = loadAvailableDevices()
            availableDevices = devices

            if (devices.isNotEmpty()) {
                if (selectedDeviceSn.isEmpty() || !devices.any { it.sn == selectedDeviceSn }) {
                    selectedDeviceSn = devices.first().sn
                }
                loadHistory(selectedDeviceSn)
            } else {
                historyData = emptyList()
                filteredData = emptyList()
                errorMessage = noDataFoundText
                isLoading = false
            }
        } catch (e: Exception) {
            errorMessage = "$networkErrorText: ${e.message}"
        } finally {
            isLoadingDevices = false
        }
    }

    fun triggerUpdate() = scope.launch {
        if (selectedDeviceSn.isEmpty()) return@launch
        isUpdating = true
        errorMessage = null
        val token = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getString("jwt_token", null)
        if (token == null) {
            errorMessage = userNotLoggedInText
            isUpdating = false
            return@launch
        }
        try {
            val resp = repository.sendCommand(selectedDeviceSn, "update", token)
            if (resp.success) {
                android.widget.Toast.makeText(context, dataUpdatedText, android.widget.Toast.LENGTH_SHORT).show()
                delay(500)
                loadHistory(selectedDeviceSn)
            } else {
                errorMessage = resp.message ?: failedToUpdateText
                android.widget.Toast.makeText(context, "$failedToUpdateText: ${resp.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            errorMessage = "$networkErrorText: ${e.message}"
            android.widget.Toast.makeText(context, "$networkErrorText: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
        } finally {
            isUpdating = false
        }
    }

    LaunchedEffect(Unit) {
        refreshDevicesAndHistory()
    }

    LaunchedEffect(selectedFilter, historyData, customStartDate, customEndDate) {
        filteredData = filterByPeriod(historyData, selectedFilter)
        resetViewport(filteredData.size)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.top_overlay), "Background",
            modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 48.dp, start = 16.dp, end = 16.dp)
            ) {
                Text(statisticsText, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text(analiseText, fontSize = 16.sp, color = Color.White.copy(alpha = 0.8f))
            }
            Spacer(modifier = Modifier.height(24.dp))

            when {
                isLoadingDevices -> Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }

                errorMessage != null && availableDevices.isEmpty() -> StatsCard {
                    Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.DateRange, noDataFoundText, tint = Color(0xFF4CAF50), modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(errorMessage!!, fontSize = 14.sp, color = Color(0xFFD32F2F), textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { scope.launch { refreshDevicesAndHistory() } },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(retryText, color = Color.White)
                        }
                    }
                }

                else -> {
                    StatsCard {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(deviceLabelText, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
                                Button(
                                    onClick = { triggerUpdate() },
                                    enabled = !isUpdating && selectedDeviceSn.isNotEmpty(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.width(70.dp).height(36.dp)
                                ) {
                                    if (isUpdating) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                    } else {
                                        Icon(painterResource(R.drawable.ic_system_update_alt), updateText, modifier = Modifier.size(18.dp), tint = Color.White)
                                    }
                                }
                            }

                            ExposedDropdownMenuBox(
                                expanded = deviceDropdownExpanded,
                                onExpandedChange = { deviceDropdownExpanded = it }
                            ) {
                                val selectedDevice = availableDevices.find { it.sn == selectedDeviceSn }
                                OutlinedTextField(
                                    value = selectedDevice?.getDisplayText() ?: selectedDeviceSn,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(deviceDropdownExpanded) },
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
                                                scope.launch { loadHistory(device.sn) }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    if (errorMessage != null && historyData.isEmpty()) {
                        StatsCard {
                            Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(noDataFoundText, fontSize = 14.sp, color = Color(0xFFD32F2F), textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("$deviceLabelText: ${availableDevices.find { it.sn == selectedDeviceSn }?.getDisplayText() ?: selectedDeviceSn}", fontSize = 12.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { scope.launch { loadHistory(selectedDeviceSn) } },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(retryText, color = Color.White)
                                }
                            }
                        }
                    }
                    if (historyData.isNotEmpty()) {
                        StatsCard {
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(periodText, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
                                    Icon(
                                        imageVector = Icons.Outlined.Info,
                                        contentDescription = "Info",
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clickable { showPeriodInfoDialog = true },
                                        tint = Color(0xFF1B5E20)
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .horizontalScroll(rememberScrollState())
                                        .padding(bottom = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    filterOptions.forEach { opt ->
                                        FilterChip(
                                            selected = selectedFilter == opt,
                                            onClick = {
                                                if (opt == customText) {
                                                    val c = Calendar.getInstance()
                                                    startYear = c.get(Calendar.YEAR)
                                                    startMonth = c.get(Calendar.MONTH)
                                                    startDay = c.get(Calendar.DAY_OF_MONTH)
                                                    endYear = c.get(Calendar.YEAR)
                                                    endMonth = c.get(Calendar.MONTH)
                                                    endDay = c.get(Calendar.DAY_OF_MONTH)
                                                    isSelectingStart = true
                                                    showDatePicker = true
                                                } else {
                                                    selectedFilter = opt
                                                    customStartDate = null
                                                    customEndDate = null
                                                }
                                            },
                                            label = {
                                                Text(
                                                    text = opt,
                                                    fontSize = 13.sp,
                                                    maxLines = 1,
                                                    softWrap = false
                                                )
                                            },
                                            modifier = Modifier.height(36.dp),
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = Color(0xFF4CAF50),
                                                selectedLabelColor = Color.White,
                                                containerColor = Color(0xFFE8F5E9),
                                                labelColor = Color(0xFF4CAF50)
                                            )
                                        )
                                    }
                                }
                                if (selectedFilter == customText && customStartDate != null && customEndDate != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.DateRange, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("${fmtDate(customStartDate)} – ${fmtDate(customEndDate)}", fontSize = 12.sp, color = Color(0xFF1B5E20))
                                        }
                                        IconButton(
                                            onClick = { customStartDate = null; customEndDate = null; selectedFilter = allText },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Close, clearText, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                        StatsCard {
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Text(metricText, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
                                ExposedDropdownMenuBox(
                                    expanded = metricDropdownExpanded,
                                    onExpandedChange = { metricDropdownExpanded = it }
                                ) {
                                    OutlinedTextField(
                                        value = selectedMetric,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(metricDropdownExpanded) },
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
                                        expanded = metricDropdownExpanded,
                                        onDismissRequest = { metricDropdownExpanded = false },
                                        containerColor = Color.White
                                    ) {
                                        metricOptions.forEach { m ->
                                            DropdownMenuItem(
                                                text = {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(m.icon, fontSize = 16.sp, color = Color.Black)
                                                        Spacer(Modifier.width(8.dp))
                                                        Text(
                                                            text = m.name,
                                                            fontSize = 14.sp,
                                                            color = Color.Black
                                                        )
                                                        Spacer(Modifier.width(4.dp))
                                                        Text(
                                                            text = m.unit,
                                                            fontSize = 12.sp,
                                                            color = Color.Black
                                                        )
                                                    }
                                                },
                                                onClick = {
                                                    selectedMetric = m.name
                                                    metricDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (filteredData.isNotEmpty() && visibleStartIndex < filteredData.size) {
                            val endIdx = minOf(visibleEndIndex, filteredData.size)
                            val visibleData = if (visibleStartIndex < endIdx) filteredData.subList(visibleStartIndex, endIdx) else emptyList()
                            val metric = currentMetric()

                            val dataYear = try {
                                val firstRecord = filteredData.firstOrNull()
                                if (firstRecord != null) {
                                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                    val date = sdf.parse(firstRecord.created_at)
                                    if (date != null) {
                                        SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
                                    } else {
                                        ""
                                    }
                                } else {
                                    ""
                                }
                            } catch (_: Exception) { "" }

                            if (visibleData.isNotEmpty()) {
                                StatsCard {
                                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(metric.icon, fontSize = 20.sp)
                                                Spacer(Modifier.width(8.dp))
                                                Text(metric.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                                            }
                                            Text(
                                                text = if (dataYear.isNotEmpty()) "$dataYear" else "",
                                                fontSize = 14.sp,
                                                color = Color.Gray
                                            )
                                        }
                                        Spacer(Modifier.height(8.dp))

                                        val count = visibleEndIndex - visibleStartIndex
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                String.format(stringResource(R.string.showing_records), visibleStartIndex + 1, endIdx, filteredData.size),
                                                fontSize = (13.5).sp,
                                                color = Color.Gray
                                            )
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                PagBtn("◀◀", visibleStartIndex > 0) {
                                                    visibleStartIndex = maxOf(0, visibleStartIndex - count)
                                                    visibleEndIndex = minOf(filteredData.size, visibleStartIndex + count)
                                                }
                                                PagBtn("◀", visibleStartIndex > 0) {
                                                    visibleStartIndex = maxOf(0, visibleStartIndex - 5)
                                                    visibleEndIndex = minOf(filteredData.size, visibleStartIndex + count)
                                                }
                                                PagBtn("▶", visibleEndIndex < filteredData.size) {
                                                    visibleStartIndex = minOf(filteredData.size - count, visibleStartIndex + 5)
                                                    visibleEndIndex = minOf(filteredData.size, visibleStartIndex + count)
                                                }
                                                PagBtn("▶▶", visibleEndIndex < filteredData.size) {
                                                    visibleStartIndex = minOf(filteredData.size - count, visibleStartIndex + count)
                                                    visibleEndIndex = minOf(filteredData.size, visibleStartIndex + count)
                                                }
                                            }
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            TextButton(
                                                onClick = {
                                                    val newCnt = (count * 1.5).toInt().coerceAtMost(filteredData.size)
                                                    val center = (visibleStartIndex + visibleEndIndex) / 2
                                                    visibleStartIndex = maxOf(0, center - newCnt / 2)
                                                    visibleEndIndex = minOf(filteredData.size, visibleStartIndex + newCnt)
                                                },
                                                modifier = Modifier.height(40.dp).width(120.dp),
                                                shape = RoundedCornerShape(8.dp)
                                            ) { Text(zoomOutText, fontSize = 13.sp, color = Color(0xFF4CAF50)) }
                                            Spacer(Modifier.width(16.dp))
                                            TextButton(
                                                onClick = {
                                                    val newCnt = (count * 0.66).toInt().coerceAtLeast(5)
                                                    val center = (visibleStartIndex + visibleEndIndex) / 2
                                                    visibleStartIndex = maxOf(0, center - newCnt / 2)
                                                    visibleEndIndex = minOf(filteredData.size, visibleStartIndex + newCnt)
                                                },
                                                modifier = Modifier.height(40.dp).width(120.dp),
                                                shape = RoundedCornerShape(8.dp)
                                            ) { Text(zoomInText, fontSize = 13.sp, color = Color(0xFF4CAF50)) }
                                        }

                                        Spacer(Modifier.height(16.dp))

                                        Box(
                                            modifier = Modifier.fillMaxWidth().pointerInput(Unit) {
                                                detectDragGestures { change, drag ->
                                                    change.consume()
                                                    onPan(drag.x)
                                                }
                                            }
                                        ) {
                                            LineChart(
                                                data = visibleData,
                                                extractor = { metricValue(it, metric) },
                                                color = metric.color,
                                                unit = metric.unit
                                            )
                                        }

                                        Spacer(Modifier.height(16.dp))

                                        val vals = visibleData.map { metricValue(it, metric) }
                                        if (vals.isNotEmpty()) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceEvenly
                                            ) {
                                                StatCard(averageText, "%.1f".format(vals.average()), metric.unit, metric.color)
                                                StatCard(minText, "%.1f".format(vals.minOrNull() ?: 0.0), metric.unit, metric.color)
                                                StatCard(maxText, "%.1f".format(vals.maxOrNull() ?: 0.0), metric.unit, metric.color)
                                            }
                                            Spacer(Modifier.height(8.dp))
                                            val change = (vals.lastOrNull() ?: 0.0) - (vals.firstOrNull() ?: 0.0)
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Card(
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = if (change >= 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                                                    )
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        val tc = if (change >= 0) Color(0xFF4CAF50) else Color(0xFFD32F2F)
                                                        Text(if (change >= 0) "▲" else "▼", fontSize = 16.sp, color = tc)
                                                        Spacer(Modifier.width(4.dp))
                                                        Text(
                                                            "$changeText: ${"%.1f".format(kotlin.math.abs(change))} ${metric.unit}",
                                                            fontSize = 12.sp,
                                                            color = tc
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            StatsCard {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text(noDataForPeriodText, fontSize = 14.sp, color = Color.Gray)
                                }
                            }
                        }
                        if (filteredData.isNotEmpty()) {
                            StatsCard {
                                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                    Text(
                                        recentRecordsText,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1B5E20),
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )
                                    LazyColumn(modifier = Modifier.height(200.dp)) {
                                        items(filteredData.take(10)) { rec ->
                                            RecentRecordCard(
                                                rec, lampOnText, lampOffText,
                                                waterYesText, waterNoText,
                                                wateringOnText, wateringOffText
                                            )
                                            Spacer(Modifier.height(8.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
    if (showPeriodInfoDialog) {
        AlertDialog(
            onDismissRequest = { showPeriodInfoDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color(0xFF1B5E20)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = periodInfoTitle,
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
                        text = periodInfoContent,
                        fontSize = 14.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = periodInfoDay, fontSize = 13.sp, color = Color(0xFF1B5E20))
                            Text(text = periodInfoWeek, fontSize = 13.sp, color = Color(0xFF1B5E20))
                            Text(text = periodInfoMonth, fontSize = 13.sp, color = Color(0xFF1B5E20))
                            Text(text = periodInfoAll, fontSize = 13.sp, color = Color(0xFF1B5E20))
                            Text(text = periodInfoCustom, fontSize = 13.sp, color = Color(0xFF1B5E20))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = periodInfoHowTo,
                            fontSize = 13.sp,
                            color = Color(0xFF2E7D32),
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = periodInfoNote,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPeriodInfoDialog = false },
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
    if (showDatePicker) {
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        if (isSelectingStart) selectStartDateText else selectEndDateText,
                        fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Button(
                        onClick = { isSelectingStart = !isSelectingStart },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            if (isSelectingStart) switchToEndDateText else switchToStartDateText,
                            fontSize = 12.sp, color = Color(0xFF4CAF50)
                        )
                    }
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth().height(380.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = if (isSelectingStart) "$startDateText: ${fmtDatePicker(startYear, startMonth, startDay)}"
                            else "$endDateText: ${fmtDatePicker(endYear, endMonth, endDay)}",
                            fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1B5E20),
                            modifier = Modifier.padding(12.dp), textAlign = TextAlign.Center
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            if (isSelectingStart) {
                                if (startMonth == 0) { startMonth = 11; startYear-- } else startMonth--
                            } else {
                                if (endMonth == 0) { endMonth = 11; endYear-- } else endMonth--
                            }
                        }) { Icon(Icons.Default.ArrowDropDown, previousMonthText, modifier = Modifier.rotate(90f)) }
                        Text(
                            "${monthName(if (isSelectingStart) startMonth else endMonth)} ${if (isSelectingStart) startYear else endYear}",
                            fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1B5E20)
                        )
                        IconButton(onClick = {
                            if (isSelectingStart) {
                                if (startMonth == 11) { startMonth = 0; startYear++ } else startMonth++
                            } else {
                                if (endMonth == 11) { endMonth = 0; endYear++ } else endMonth++
                            }
                        }) { Icon(Icons.Default.ArrowDropDown, nextMonthText, modifier = Modifier.rotate(-90f)) }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        listOf(monText, tueText, wedText, thuText, friText, satText, sunText).forEach { d ->
                            Text(d, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.Gray, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        }
                    }
                    Spacer(Modifier.height(8.dp))

                    val cal = Calendar.getInstance()
                    val curY = if (isSelectingStart) startYear else endYear
                    val curM = if (isSelectingStart) startMonth else endMonth
                    cal.set(curY, curM, 1)
                    val firstDow = cal.get(Calendar.DAY_OF_WEEK)
                    val daysInM = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                    val offset = if (firstDow == Calendar.SUNDAY) 6 else firstDow - 2
                    Column {
                        for (week in 0..5) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                for (dow in 0..6) {
                                    val dayNum = week * 7 + dow - offset + 1
                                    if (dayNum in 1..daysInM) {
                                        val isSel = if (isSelectingStart) dayNum == startDay else dayNum == endDay
                                        Box(
                                            modifier = Modifier.weight(1f).aspectRatio(1f).padding(4.dp)
                                                .background(if (isSel) Color(0xFF4CAF50) else Color.Transparent, RoundedCornerShape(8.dp))
                                                .clickable { if (isSelectingStart) startDay = dayNum else endDay = dayNum },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                dayNum.toString(), fontSize = 14.sp,
                                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSel) Color.White else Color(0xFF1B5E20)
                                            )
                                        }
                                    } else Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    if (customStartDate != null || customEndDate != null) {
                        HorizontalDivider()
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "$selectedText: ${fmtDate(customStartDate)} – ${fmtDate(customEndDate)}",
                            fontSize = 11.sp, color = Color(0xFF4CAF50), modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (isSelectingStart) {
                            customStartDate = toTimestamp(startYear, startMonth, startDay)
                            isSelectingStart = false
                        } else {
                            customEndDate = toTimestamp(endYear, endMonth, endDay)
                            if (customStartDate != null && customStartDate!! > customEndDate!!) {
                                val tmp = customStartDate
                                customStartDate = customEndDate
                                customEndDate = tmp
                            }
                            selectedFilter = customText
                            showDatePicker = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(8.dp)
                ) { Text(if (isSelectingStart) nextText else applyText) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false; isSelectingStart = true }) { Text(cancelText) }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}


@Composable
fun StatsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
    ) { content() }
}

@Composable
fun PagBtn(label: String, enabled: Boolean, onClick: () -> Unit) {
    TextButton(onClick = onClick, enabled = enabled, modifier = Modifier.height(24.dp)) {
        Text(label, fontSize = 11.sp)
    }
}

@Composable
fun LineChart(data: List<PlantHistoryData>, extractor: (PlantHistoryData) -> Double, color: Color, unit: String) {
    if (data.isEmpty()) return
    val values = data.map { extractor(it).toFloat() }
    val maxV = values.maxOrNull() ?: 1f
    val minV = values.minOrNull() ?: 0f
    val range = if (maxV - minV > 0) maxV - minV else 1f

    Column {
        Box(
            modifier = Modifier.fillMaxWidth().height(220.dp).background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)).padding(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxHeight().width(40.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${maxV.toInt()}$unit", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp))
                Text("${"%.0f".format((maxV + minV) / 2)}$unit", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp))
                Text("${minV.toInt()}$unit", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp))
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width - 40.dp.toPx()
                val h = size.height
                val step = if (data.size > 1) w / (data.size - 1) else w
                for (i in 0..4) drawLine(Color(0xFFE0E0E0), Offset(40.dp.toPx(), i * h / 4), Offset(size.width, i * h / 4), 1f)
                val pts = data.mapIndexed { idx, rec ->
                    Offset(40.dp.toPx() + idx * step, (h * (1 - (extractor(rec).toFloat() - minV) / range)).coerceIn(0f, h))
                }
                if (pts.size > 1) for (i in 0 until pts.size - 1) drawLine(color, pts[i], pts[i + 1], 2f)
                pts.forEach { p -> drawCircle(color, 5f, p); drawCircle(Color.White, 2f, p) }
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(start = 40.dp, top = 4.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            data.forEach { rec -> Text(fmtDateShort(rec.created_at), fontSize = 9.sp, color = Color.Gray, maxLines = 1, modifier = Modifier.weight(1f), textAlign = TextAlign.Center) }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, unit: String, color: Color = Color(0xFF4CAF50)) {
    Card(
        modifier = Modifier.width(100.dp).padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
            Text(unit, fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
fun RecentRecordCard(
    record: PlantHistoryData,
    lampOn: String,
    lampOff: String,
    waterYes: String,
    waterNo: String,
    wateringOn: String,
    wateringOff: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(fmtDateTime(record.created_at), fontSize = 12.sp, color = Color.Gray)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricChip("🌡️ ${"%.1f".format(record.air_temp)}°C", Color(0xFFFF9800))
                    MetricChip("💧 ${"%.1f".format(record.air_humid)}%", Color(0xFF2196F3))
                }
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricChip("🌱 ${record.soil_humid}%", Color(0xFF4CAF50))
                    MetricChip("💡 ${record.light.toInt()}%", Color(0xFFFFC107))
                    MetricChip(if (record.lamp == "ON") lampOn else lampOff, if (record.lamp == "ON") Color(0xFF4CAF50) else Color(0xFF9E9E9E))
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                MetricChip(if (record.water == "YES") waterYes else waterNo, if (record.water == "YES") Color(0xFF4CAF50) else Color(0xFFD32F2F))
                Spacer(Modifier.height(4.dp))
                MetricChip(if (record.watering == "ON") wateringOn else wateringOff, if (record.watering == "ON") Color(0xFF4CAF50) else Color(0xFF9E9E9E))
            }
        }
    }
}

@Composable
fun MetricChip(label: String, color: Color) {
    Box(
        modifier = Modifier.background(color.copy(alpha = 0.1f), RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(label, fontSize = 10.sp, color = color, fontWeight = FontWeight.Medium)
    }
}

fun fmtDateTime(s: String): String = try {
    SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
        .format(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(s) ?: Date())
} catch (_: Exception) { s.take(16).replace("T", " ") }

fun fmtDateShort(s: String): String = try {
    SimpleDateFormat("dd/MM", Locale.getDefault())
        .format(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(s) ?: Date())
} catch (_: Exception) { s.substring(5, 10) }

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StatsScreenPreview() {
    SmartPlantBoxTheme { StatsScreen() }
}