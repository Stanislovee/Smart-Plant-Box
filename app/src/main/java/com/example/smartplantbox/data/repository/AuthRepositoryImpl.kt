
package com.example.smartplantbox.data.repository

import android.content.Context
import com.example.smartplantbox.data.remote.KtorClient
import com.example.smartplantbox.domain.model.*
import com.example.smartplantbox.domain.repository.AuthRepository

class AuthRepositoryImpl : AuthRepository {
    private val apiService = KtorClient.authApiService
    override suspend fun login(email: String, password: String, context: Context): Pair<Int, ApiResponse> {
        println("Repository: login called with email: $email")
        val request = LoginRequest(Email = email, Password = password)
        return try {
            val result = apiService.login(request, context)
            println("Repository: login result - status: ${result.first}, success: ${result.second.success}")
            result
        } catch (e: Exception) {
            println("Repository: login exception - ${e.message}")
            throw e
        }
    }
    override suspend fun register(name: String, email: String, password: String): Pair<Int, ApiResponse> {
        println("Repository: register called with name: $name, email: $email")
        val request = RegisterRequest(Name = name, Email = email, Password = password)
        return try {
            val result = apiService.register(request)
            println("Repository: register result - status: ${result.first}, success: ${result.second.success}")
            result
        } catch (e: Exception) {
            println("Repository: register exception - ${e.message}")
            throw e
        }
    }
    override suspend fun getUserName(email: String): String? {
        println("Repository: getUserName called with email: $email")
        return try {
            val name = apiService.getUserName(email)
            println("Repository: getUserName result - $name")
            name
        } catch (e: Exception) {
            println("Repository: getUserName exception - ${e.message}")
            null
        }
    }
    override suspend fun requestPasswordReset(email: String): Pair<Int, ApiResponse> {
        println("Repository: requestPasswordReset called with email: $email")
        return try {
            val result = apiService.requestPasswordReset(email)
            println("Repository: requestPasswordReset result - status: ${result.first}, success: ${result.second.success}")
            result
        } catch (e: Exception) {
            println("Repository: requestPasswordReset exception - ${e.message}")
            throw e
        }
    }
    override suspend fun verifyResetCode(email: String, code: String): Pair<Int, ApiResponse> {
        println("Repository: verifyResetCode called with email: $email, code: $code")
        return try {
            val result = apiService.verifyResetCode(email, code)
            println("Repository: verifyResetCode result - status: ${result.first}, success: ${result.second.success}")
            result
        } catch (e: Exception) {
            println("Repository: verifyResetCode exception - ${e.message}")
            throw e
        }
    }
    override suspend fun changePassword(email: String, code: String, newPassword: String): Pair<Int, ApiResponse> {
        println("Repository: changePassword called with email: $email, code: $code")
        return try {
            val result = apiService.changePassword(email, code, newPassword)
            println("Repository: changePassword result - status: ${result.first}, success: ${result.second.success}")
            result
        } catch (e: Exception) {
            println("Repository: changePassword exception - ${e.message}")
            throw e
        }
    }
    override suspend fun updateProfile(token: String, name: String): Pair<Int, ApiResponse> {
        println("Repository: updateProfile called with name: $name")
        return try {
            val result = apiService.updateProfile(token, name)
            println("Repository: updateProfile result - status: ${result.first}, success: ${result.second.success}")
            result
        } catch (e: Exception) {
            println("Repository: updateProfile exception - ${e.message}")
            throw e
        }
    }
    override suspend fun updatePasswordOnly(token: String, newPassword: String): Pair<Int, ApiResponse> {
        println("Repository: updatePasswordOnly called")
        return try {
            val result = apiService.updatePasswordOnly(token, newPassword)
            println("Repository: updatePasswordOnly result - status: ${result.first}, success: ${result.second.success}")
            result
        } catch (e: Exception) {
            println("Repository: updatePasswordOnly exception - ${e.message}")
            throw e
        }
    }
    override suspend fun bindDevice(key: String, email: String, token: String): Pair<Int, ApiResponse> {
        println("Repository: bindDevice called with key: $key, email: $email")
        return try {
            val result = apiService.bindDevice(key, email, token)
            println("Repository: bindDevice result - status: ${result.first}, success: ${result.second.success}")
            result
        } catch (e: Exception) {
            println("Repository: bindDevice exception - ${e.message}")
            throw e
        }
    }
    override suspend fun getBoundDevices(email: String, token: String): KeyListResponse {
        println("Repository: getBoundDevices called with email: $email")
        return try {
            val result = apiService.getBoundDevices(email, token)
            println("Repository: getBoundDevices result - keys: ${result.keys}")
            result
        } catch (e: Exception) {
            println("Repository: getBoundDevices exception - ${e.message}")
            KeyListResponse(success = false, message = e.message)
        }
    }
    override suspend fun unbindDevice(key: String, email: String, token: String): Pair<Int, ApiResponse> {
        println("Repository: unbindDevice called with key: $key, email: $email")
        return try {
            val result = apiService.unbindDevice(key, email, token)
            println("Repository: unbindDevice result - status: ${result.first}, success: ${result.second.success}")
            result
        } catch (e: Exception) {
            println("Repository: unbindDevice exception - ${e.message}")
            throw e
        }
    }
    override suspend fun getAirData(key: String, token: String): AirDataResponse {
        println("Repository: getAirData called with key: $key")
        return try {
            val result = apiService.getAirData(key, token)
            println("Repository: getAirData result - success: ${result.success}")
            result
        } catch (e: Exception) {
            println("Repository: getAirData exception - ${e.message}")
            AirDataResponse(success = false, message = e.message)
        }
    }
    override suspend fun getLightData(key: String, token: String): LightDataResponse {
        println("Repository: getLightData called with key: $key")
        return try {
            val result = apiService.getLightData(key, token)
            println("Repository: getLightData result - success: ${result.success}")
            result
        } catch (e: Exception) {
            println("Repository: getLightData exception - ${e.message}")
            LightDataResponse(success = false, message = e.message)
        }
    }
    override suspend fun getSoilData(key: String, token: String): SoilDataResponse {
        println("Repository: getSoilData called with key: $key")
        return try {
            val result = apiService.getSoilData(key, token)
            println("Repository: getSoilData result - success: ${result.success}")
            result
        } catch (e: Exception) {
            println("Repository: getSoilData exception - ${e.message}")
            SoilDataResponse(success = false, message = e.message)
        }
    }
    override suspend fun sendCommand(key: String, command: String, token: String): CommandResponse {
        println("Repository: sendCommand called with key: $key, command: $command")
        return try {
            val result = apiService.sendCommand(key, command, token)
            println("Repository: sendCommand result - success: ${result.success}")
            result
        } catch (e: Exception) {
            println("Repository: sendCommand exception - ${e.message}")
            CommandResponse(success = false, message = e.message)
        }
    }
    override suspend fun setLampStatus(key: String, isOn: Boolean, token: String): ApiResponse {
        println("Repository: setLampStatus called with key: $key, isOn: $isOn")
        return try {
            val result = apiService.setLampStatus(key, isOn, token)
            println("Repository: setLampStatus result - success: ${result.success}")
            result
        } catch (e: Exception) {
            println("Repository: setLampStatus exception - ${e.message}")
            ApiResponse(success = false, message = e.message)
        }
    }
    override suspend fun getPlantHistory(key: String, token: String): PlantHistoryResponse {
        println("Repository: getPlantHistory called with key: $key")
        return try {
            val result = apiService.getPlantHistory(key, token)
            println("Repository: getPlantHistory result - success: ${result.success}, data size: ${result.data?.size}")
            result
        } catch (e: Exception) {
            println("Repository: getPlantHistory exception - ${e.message}")
            PlantHistoryResponse(success = false, message = e.message)
        }
    }
    override suspend fun getPhotoInterval(key: String, token: String): PhotoIntervalResponse {
        return apiService.getPhotoInterval(key, token)
    }
    override suspend fun setPhotoInterval(key: String, intervalHours: Int, token: String): ApiResponse {
        return apiService.setPhotoInterval(key, intervalHours, token)
    }
    override suspend fun getPhotoTimes(key: String, token: String): PhotoTimesResponse {
        return apiService.getPhotoTimes(key, token)
    }
    override suspend fun takePhotoNow(key: String, token: String): ApiResponse {
        return apiService.takePhotoNow(key, token)
    }
    override suspend fun getPhotoUrl(key: String, time: String, token: String): PhotoUrlResponse {
        return apiService.getPhotoUrl(key, time, token)
    }
}