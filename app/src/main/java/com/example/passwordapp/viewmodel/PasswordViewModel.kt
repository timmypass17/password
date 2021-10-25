package com.example.passwordapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.passwordapp.data.password.Password
import com.example.passwordapp.data.password.PasswordDao
import kotlinx.coroutines.launch

/** Interacts with the database via the DAO, and provides data to the UI **/
class PasswordViewModel(private val passwordDao: PasswordDao) : ViewModel() {

    private fun insertPassword(password: Password) {
        viewModelScope.launch {
            passwordDao.insert(password)
        }
    }

    private fun getNewPasswordEntry(website: String, user: String, pass: String): Password {
        return Password(
            websiteName = website,
            username = user,
            password = pass
        )
    }

    /** Add new password **/
    fun addNewPassword(website: String, user: String, pass: String) {
        val newPassword = getNewPasswordEntry(website, user, pass)
        insertPassword(newPassword)
    }

    /** Check if user has valid input **/
    fun isEntryValid(website: String, user: String, pass: String): Boolean {
        if (website.isBlank() || user.isBlank() || pass.isBlank()) {
            return false
        }
        return true
    }
}

/** Creates the PasswordViewModel instance **/
class PasswordViewModelFactory(private val passwordDao: PasswordDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PasswordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PasswordViewModel(passwordDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}