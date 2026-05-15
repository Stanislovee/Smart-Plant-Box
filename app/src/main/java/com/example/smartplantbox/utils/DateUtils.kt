package com.example.smartplantbox.utils

import com.example.smartplantbox.domain.model.PlantHistoryData
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun formatDate(dateString: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = sdf.parse(dateString)
            SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(date ?: Date())
        } catch (_: Exception) {
            dateString.substring(0, 16).replace("T", " ")
        }
    }
    fun formatDateShort(dateString: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = sdf.parse(dateString)
            SimpleDateFormat("dd/MM", Locale.getDefault()).format(date ?: Date())
        } catch (_: Exception) {
            dateString.substring(5, 10)
        }
    }
    fun getDateFromString(dateString: String): Date? {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            sdf.parse(dateString)
        } catch (_: Exception) {
            null
        }
    }
    fun filterDataByDateRange(
        data: List<PlantHistoryData>,
        startDate: Date?,
        endDate: Date?
    ): List<PlantHistoryData> {
        if (startDate == null || endDate == null) return data

        return data.filter {
            val date = getDateFromString(it.created_at)
            date != null && date in startDate..endDate
        }
    }
}