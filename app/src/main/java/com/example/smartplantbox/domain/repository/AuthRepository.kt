package com.example.smartplantbox.domain.repository

import android.content.Context
import com.example.smartplantbox.domain.model.ApiResponse
import com.example.smartplantbox.domain.model.KeyListResponse
import com.example.smartplantbox.domain.model.AirDataResponse
import com.example.smartplantbox.domain.model.LightDataResponse
import com.example.smartplantbox.domain.model.SoilDataResponse
import com.example.smartplantbox.domain.model.CommandResponse
import com.example.smartplantbox.domain.model.PhotoIntervalResponse
import com.example.smartplantbox.domain.model.PhotoTimesResponse
import com.example.smartplantbox.domain.model.PhotoUrlResponse
import com.example.smartplantbox.domain.model.PlantHistoryResponse

interface AuthRepository {
    suspend fun login(email: String, password: String, context: Context): Pair<Int, ApiResponse>
    suspend fun register(name: String, email: String, password: String): Pair<Int, ApiResponse>
    suspend fun getUserName(email: String): String?
    suspend fun requestPasswordReset(email: String): Pair<Int, ApiResponse>
    suspend fun verifyResetCode(email: String, code: String): Pair<Int, ApiResponse>
    suspend fun changePassword(email: String, code: String, newPassword: String): Pair<Int, ApiResponse>

    suspend fun updateProfile(token: String, name: String): Pair<Int, ApiResponse>

    suspend fun updatePasswordOnly(token: String, newPassword: String): Pair<Int, ApiResponse>

    suspend fun bindDevice(key: String, email: String, token: String): Pair<Int, ApiResponse>
    suspend fun getBoundDevices(email: String, token: String): KeyListResponse
    suspend fun unbindDevice(key: String, email: String, token: String): Pair<Int, ApiResponse>

    suspend fun getAirData(key: String, token: String): AirDataResponse
    suspend fun getLightData(key: String, token: String): LightDataResponse
    suspend fun getSoilData(key: String, token: String): SoilDataResponse
    suspend fun sendCommand(key: String, command: String, token: String): CommandResponse
    suspend fun setLampStatus(key: String, isOn: Boolean, token: String): ApiResponse

    suspend fun getPlantHistory(key: String, token: String): PlantHistoryResponse

    suspend fun getPhotoInterval(key: String, token: String): PhotoIntervalResponse
    suspend fun setPhotoInterval(key: String, intervalHours: Int, token: String): ApiResponse
    suspend fun getPhotoTimes(key: String, token: String): PhotoTimesResponse
    suspend fun takePhotoNow(key: String, token: String): ApiResponse
    suspend fun getPhotoUrl(key: String, time: String, token: String): PhotoUrlResponse
}