package com.example.smartplantbox.domain.model

// Auth requests
data class LoginRequest(val Email: String, val Password: String)
data class RegisterRequest(val Name: String, val Email: String, val Password: String)
data class ForgotPasswordRequest(val email: String)
data class VerifyCodeRequest(val email: String, val code: String)
data class ChangePasswordRequest(val email: String, val code: String, val newPassword: String)

// Auth responses
data class ApiResponse(
    val success: Boolean,
    val message: String? = null,
    val name: String? = null,
    val token: String? = null
)

// Device
data class KeyListResponse(
    val success: Boolean,
    val message: String? = null,
    val keys: List<String>? = null
)

// Sensor data requests
data class AirDataRequest(val key: String, val type: String = "air")
data class LightDataRequest(val key: String, val type: String = "light")
data class SoilDataRequest(val key: String, val type: String = "soil")
data class CommandRequest(val key: String, val command: String)
data class PlantHistoryRequest(val key: String)

// Sensor data responses
data class AirData(val air_temp: Double, val air_humid: Double)
data class AirDataResponse(val success: Boolean, val message: String? = null, val data: AirData? = null)

data class LightData(val light: Double, val light_threshold: Double, val lamp: String)
data class LightDataResponse(val success: Boolean, val message: String? = null, val data: LightData? = null)

data class SoilData(val soil_humid: Double, val soil_threshold: Double, val water: String, val watering: String)
data class SoilDataResponse(val success: Boolean, val message: String? = null, val data: SoilData? = null)

data class CommandResponse(val success: Boolean, val message: String? = null)

// History data
data class PlantHistoryData(
    val id: Int,
    val key: String,
    val air_temp: Double,
    val air_humid: Double,
    val soil_humid: Double,
    val light: Double,
    val soil_threshold: Double,
    val light_threshold: Double,
    val interval_photo: Int,
    val water: String,
    val watering: String,
    val lamp: String,
    val created_at: String
)

data class PlantHistoryResponse(
    val success: Boolean,
    val message: String? = null,
    val data: List<PlantHistoryData>? = null
)

// Photo
data class PhotoIntervalData(val interval_photo: Int, val created_at: String)
data class PhotoIntervalResponse(val success: Boolean, val message: String? = null, val data: PhotoIntervalData? = null)

data class PhotoTimesResponse(
    val success: Boolean,
    val message: String? = null,
    val key: String,
    val count: Int,
    val times: List<String>
)

data class PhotoUrlResponse(val success: Boolean, val message: String? = null, val url: String)