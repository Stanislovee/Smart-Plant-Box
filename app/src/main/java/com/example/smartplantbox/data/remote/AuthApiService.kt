package com.example.smartplantbox.data.remote

import android.content.Context
import com.example.smartplantbox.domain.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.TimeoutCancellationException

class AuthApiService(private val client: HttpClient = KtorClient.client) {

    // Auth

    suspend fun login(request: LoginRequest, context: Context): Pair<Int, ApiResponse> {
        println("[LOGIN] Sending request to: ${KtorClient.BASE_URL}login.php")
        println("[LOGIN] Email: ${request.Email}")

        return try {
            val response: HttpResponse = client.post("${KtorClient.BASE_URL}login.php") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            println("[LOGIN] Response status: ${response.status.value}")

            val responseText = response.bodyAsText()
            println("[LOGIN] Raw response body: $responseText")

            val apiResponse: ApiResponse = response.body()
            println("[LOGIN] Parsed - success: ${apiResponse.success}, message: ${apiResponse.message}, token: ${apiResponse.token?.take(10)}...")

            // Save token and email to SharedPreferences on successful login
            if (apiResponse.success && apiResponse.token != null) {
                context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit().apply {
                    putString("user_email", request.Email)
                    putString("jwt_token", apiResponse.token)
                    apply()
                }
                println("[LOGIN] Token saved successfully")
            } else {
                println("[LOGIN] Login failed: ${apiResponse.message}")
            }

            Pair(response.status.value, apiResponse)

        } catch (e: TimeoutCancellationException) {
            println("[LOGIN] Timeout error: ${e.message}")
            Pair(408, ApiResponse(success = false, message = "Connection timeout. Please try again."))
        } catch (e: Exception) {
            println("[LOGIN] Exception: ${e.javaClass.simpleName} - ${e.message}")
            e.printStackTrace()
            Pair(500, ApiResponse(success = false, message = "Network error: ${e.message}"))
        }
    }
    suspend fun register(request: RegisterRequest): Pair<Int, ApiResponse> {
        val response: HttpResponse = client.post("${KtorClient.BASE_URL}register.php") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return Pair(response.status.value, response.body())
    }
    suspend fun getUserName(email: String): String? {
        val response: HttpResponse = client.post("${KtorClient.BASE_URL}getUserName.php") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("email" to email))
        }
        val apiResponse: ApiResponse = response.body()
        return if (apiResponse.success) apiResponse.name else null
    }
    suspend fun requestPasswordReset(email: String): Pair<Int, ApiResponse> {
        val response: HttpResponse = client.post("${KtorClient.BASE_URL}forgot_password.php") {
            contentType(ContentType.Application.Json)
            setBody(ForgotPasswordRequest(email = email))
        }
        return Pair(response.status.value, response.body())
    }
    suspend fun verifyResetCode(email: String, code: String): Pair<Int, ApiResponse> {
        val response: HttpResponse = client.post("${KtorClient.BASE_URL}verify_code.php") {
            contentType(ContentType.Application.Json)
            setBody(VerifyCodeRequest(email = email, code = code))
        }
        return Pair(response.status.value, response.body())
    }
    suspend fun changePassword(email: String, code: String, newPassword: String): Pair<Int, ApiResponse> {
        val response: HttpResponse = client.post("${KtorClient.BASE_URL}change_password.php") {
            contentType(ContentType.Application.Json)
            setBody(ChangePasswordRequest(email = email, code = code, newPassword = newPassword))
        }
        return Pair(response.status.value, response.body())
    }
    // Profile
    suspend fun updateProfile(token: String, name: String): Pair<Int, ApiResponse> {
        val response: HttpResponse = client.post("${KtorClient.BASE_URL}update_profile.php") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            setBody(mapOf("name" to name))
        }
        return Pair(response.status.value, response.body())
    }

    suspend fun updatePasswordOnly(token: String, newPassword: String): Pair<Int, ApiResponse> {
        val response: HttpResponse = client.post("${KtorClient.BASE_URL}update_profile.php") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            setBody(mapOf("password" to newPassword))
        }
        return Pair(response.status.value, response.body())
    }
    // Devices
    suspend fun getBoundDevices(email: String, token: String): KeyListResponse {
        return try {
            val response: HttpResponse = client.post("${KtorClient.BASE_URL}key_list.php") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                setBody(mapOf("email" to email))
            }
            response.body()
        } catch (e: Exception) {
            KeyListResponse(success = false, message = e.message)
        }
    }
    suspend fun bindDevice(key: String, email: String, token: String): Pair<Int, ApiResponse> {
        val response: HttpResponse = client.post("${KtorClient.BASE_URL}device.php") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            setBody(mapOf("key" to key, "email" to email))
        }
        return Pair(response.status.value, response.body())
    }
    suspend fun unbindDevice(key: String, email: String, token: String): Pair<Int, ApiResponse> {
        val response: HttpResponse = client.post("${KtorClient.BASE_URL}unbind_device.php") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            setBody(mapOf("key" to key, "email" to email))
        }
        return Pair(response.status.value, response.body())
    }
    // Sensor data
    suspend fun getAirData(key: String, token: String): AirDataResponse {
        return try {
            val response: HttpResponse = client.post("${KtorClient.BASE_URL}type_data.php") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                setBody(AirDataRequest(key = key))
            }
            response.body()
        } catch (e: Exception) {
            AirDataResponse(success = false, message = e.message)
        }
    }
    suspend fun getLightData(key: String, token: String): LightDataResponse {
        return try {
            val response: HttpResponse = client.post("${KtorClient.BASE_URL}type_data.php") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                setBody(LightDataRequest(key = key))
            }
            response.body()
        } catch (e: Exception) {
            LightDataResponse(success = false, message = e.message)
        }
    }
    suspend fun getSoilData(key: String, token: String): SoilDataResponse {
        return try {
            val response: HttpResponse = client.post("${KtorClient.BASE_URL}type_data.php") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                setBody(SoilDataRequest(key = key))
            }
            response.body()
        } catch (e: Exception) {
            SoilDataResponse(success = false, message = e.message)
        }
    }
    suspend fun sendCommand(key: String, command: String, token: String): CommandResponse {
        return try {
            val response: HttpResponse = client.post("${KtorClient.BASE_URL}command.php") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                setBody(CommandRequest(key = key, command = command))
            }
            response.body()
        } catch (e: Exception) {
            CommandResponse(success = false, message = e.message)
        }
    }
    // Lamp control uses sendCommand internally
    suspend fun setLampStatus(key: String, isOn: Boolean, token: String): ApiResponse {
        val result = sendCommand(key, if (isOn) "lamp:ON" else "lamp:OFF", token)
        return ApiResponse(success = result.success, message = result.message)
    }

    suspend fun getPlantHistory(key: String, token: String): PlantHistoryResponse {
        return try {
            val response: HttpResponse = client.post("${KtorClient.BASE_URL}data.php") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                setBody(PlantHistoryRequest(key = key))
            }
            response.body()
        } catch (e: Exception) {
            PlantHistoryResponse(success = false, message = e.message)
        }
    }

    // Photo

    suspend fun getPhotoInterval(key: String, token: String): PhotoIntervalResponse {
        return try {
            val response: HttpResponse = client.post("${KtorClient.BASE_URL}type_data.php") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                setBody(mapOf("key" to key, "type" to "photo"))
            }
            response.body()
        } catch (e: Exception) {
            PhotoIntervalResponse(success = false, message = e.message)
        }
    }

    suspend fun setPhotoInterval(key: String, intervalHours: Int, token: String): ApiResponse {
        return try {
            val response: HttpResponse = client.post("${KtorClient.BASE_URL}command.php") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                setBody(mapOf("key" to key, "command" to "interval_photo:$intervalHours"))
            }
            response.body()
        } catch (e: Exception) {
            ApiResponse(success = false, message = e.message)
        }
    }

    suspend fun getPhotoTimes(key: String, token: String): PhotoTimesResponse {
        return try {
            val response: HttpResponse = client.post("${KtorClient.BASE_URL}photo_data.php") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                setBody(mapOf("key" to key))
            }
            response.body()
        } catch (e: Exception) {
            PhotoTimesResponse(success = false, message = e.message, key = key, count = 0, times = emptyList())
        }
    }

    suspend fun takePhotoNow(key: String, token: String): ApiResponse {
        return try {
            val response: HttpResponse = client.post("${KtorClient.BASE_URL}command.php") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                setBody(mapOf("key" to key, "command" to "take_photo"))
            }
            response.body()
        } catch (e: Exception) {
            ApiResponse(success = false, message = e.message)
        }
    }

    suspend fun getPhotoUrl(key: String, time: String, token: String): PhotoUrlResponse {
        return try {
            val response: HttpResponse = client.post("${KtorClient.BASE_URL}image.php") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                setBody(mapOf("key" to key, "time" to time))
            }
            response.body()
        } catch (e: Exception) {
            PhotoUrlResponse(success = false, message = e.message, url = "")
        }
    }
}