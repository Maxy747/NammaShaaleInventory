package com.example.nammashaaleinventory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammashaaleinventory.data.InventoryRepository
import com.example.nammashaaleinventory.data.UserEntity
import com.example.nammashaaleinventory.data.UserPreferencesRepository
import com.example.nammashaaleinventory.data.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoginViewModel(
    private val inventoryRepository: InventoryRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError = _loginError.asStateFlow()

    private val _registrationSuccess = MutableStateFlow(false)
    val registrationSuccess = _registrationSuccess.asStateFlow()

    val userSession: StateFlow<UserSession> = userPreferencesRepository.userSession
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserSession()
        )

    fun loginWithCredentials(username: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _loginError.value = null
            val user = inventoryRepository.getUserByUsername(username)
            if (user != null && user.password == password) {
                userPreferencesRepository.saveSession(
                    userId = user.userId,
                    userName = user.fullName,
                    loginUsername = user.username,
                    role = user.role,
                    department = user.department
                )
                onResult(true)
            } else {
                _loginError.value = "Invalid username or password"
                onResult(false)
            }
        }
    }

    fun registerTeacher(fullName: String, username: String, password: String, department: String) {
        viewModelScope.launch {
            _loginError.value = null
            if (inventoryRepository.checkUsernameExists(username)) {
                _loginError.value = "Username already exists"
                return@launch
            }

            val newUser = UserEntity(
                fullName = fullName,
                username = username,
                password = password,
                role = "Teacher",
                department = department
            )
            inventoryRepository.insertUser(newUser)
            _registrationSuccess.value = true
        }
    }

    fun resetRegistrationState() {
        _registrationSuccess.value = false
        _loginError.value = null
    }

    fun logout() {
        viewModelScope.launch {
            userPreferencesRepository.clearSession()
        }
    }

    fun getAllUsers() = inventoryRepository.getAllUsers()

    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            inventoryRepository.deleteUserById(userId)
        }
    }
}
