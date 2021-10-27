package com.example.passwordapp.viewmodel

import androidx.lifecycle.*
import com.example.passwordapp.data.password.Password
import com.example.passwordapp.data.password.PasswordDao
import kotlinx.coroutines.launch

/** Interacts with the database via the DAO, and provides data to the UI **/
class PasswordViewModel(private val passwordDao: PasswordDao) : ViewModel() {

    val allPasswords: LiveData<List<Password>> = passwordDao.getPasswords().asLiveData()

    /**
     * Add new password
     */
    fun addNewPassword(website: String, user: String, pass: String) {
        val newPassword = getNewPasswordEntry(website, user, pass)
        viewModelScope.launch {
            passwordDao.insert(newPassword)
        }
    }

    /**
     * Updates password
     */
    fun updatePassword(id: Int, website: String, user: String, pass: String) {
        val newPassword = getUpdatedPasswordEntry(id, website, user, pass)
        viewModelScope.launch {
            passwordDao.update(newPassword)
        }
    }

    /**
     * Delete password
     */
    fun deleteItem(password: Password) {
        viewModelScope.launch {
            passwordDao.delete(password)
        }
    }


    /** Check if user has valid input **/
    fun isEntryValid(website: String, user: String, pass: String): Boolean {
        if (website.isBlank() || user.isBlank() || pass.isBlank()) {
            return false
        }
        return true
    }

    /**
     * Returns a Password by id
     */
    fun retrievePassword(id: Int): LiveData<Password> {
        return passwordDao.getPassword(id).asLiveData()
    }

    /**
     * Get a new password input from user
     */
    private fun getNewPasswordEntry(website: String, user: String, pass: String): Password {
        return Password(
            websiteName = website,
            username = user,
            password = pass
        )
    }

    /**
     * Get updated password input from user. Used for updating existing password
     */
    private fun getUpdatedPasswordEntry(id: Int, website: String, user: String, pass: String): Password {
        return Password(
            id = id,
            websiteName = website,
            username = user,
            password = pass
        )
    }
}

/**
 * Creates the PasswordViewModel instance 
 */
class PasswordViewModelFactory(private val passwordDao: PasswordDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PasswordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PasswordViewModel(passwordDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
