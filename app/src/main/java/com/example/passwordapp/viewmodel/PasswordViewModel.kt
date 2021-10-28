package com.example.passwordapp.viewmodel

import android.util.Log
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.example.passwordapp.data.password.Password
import com.example.passwordapp.data.password.PasswordDao
import com.example.passwordapp.network.IconApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.Exception

enum class IconApiStatus { LOADING, ERROR, DONE }

/** Interacts with the database via the DAO, and provides data to the UI **/
class PasswordViewModel(private val passwordDao: PasswordDao) : ViewModel() {

    private val _status = MutableLiveData<IconApiStatus>()
    val status: LiveData<IconApiStatus> = _status

    private val _allPasswords: Flow<List<Password>> = passwordDao.getPasswords()
    val allPasswords: LiveData<List<Password>> = _allPasswords.asLiveData()

    /**
     * Add new password
     */
    fun addNewPassword(website: String, user: String, password: String) {
        try {
            viewModelScope.launch {
                val imgUrl = getImageUrl(website)
                val newPassword = getNewPasswordEntry(website, user, password, imgUrl)
                passwordDao.insert(newPassword)
                _status.value = IconApiStatus.DONE
            }
        } catch (e: Exception) {
            _status.value = IconApiStatus.ERROR
            Log.i("PasswordViewModel", "Did not receive data")
        }
    }

    // make suspend so we can call it in viewModelScope
    private suspend fun getImageUrl(website: String) : String {
        // fetch icon
        val iconResult = IconApi.retrofitService.getIcon(website, 1, "flat")
        Log.i("PasswordViewModel", iconResult.toString())
        // TODO: We got url, just loop through rasterSizes[] to get img url of size 256
        var imgUrl = ""
        if (iconResult.icons.isNotEmpty()){
            for (icons in iconResult.icons[0].rasterSizes) {
                Log.i("PasswordViewModel", "Icon Size: ${icons.size}")
                if (icons.size == 256) { // find correct imgUrl size
                    imgUrl =  icons.formats[0].previewUrl
                    break
                }
            }
        }
        return imgUrl
    }

    /**
     * Updates password
     */
    fun updatePassword(id: Int, website: String, user: String, pass: String) {
        viewModelScope.launch {
            val imgUrl = getImageUrl(website)
            val newPassword = getUpdatedPasswordEntry(id, website, user, pass, imgUrl)
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
    private fun getNewPasswordEntry(website: String, user: String, pass: String, imgUrl: String): Password {
        return Password(
            websiteName = website,
            username = user,
            password = pass,
            imgUrl = imgUrl
        )
    }

    /**
     * Get updated password input from user. Used for updating existing password
     */
    private fun getUpdatedPasswordEntry(id: Int, website: String, user: String, pass: String, imgUrl: String): Password {
        return Password(
            id = id,
            websiteName = website,
            username = user,
            password = pass,
            imgUrl = imgUrl
        )
    }

    fun getIcon(website: String) {
        try {
            viewModelScope.launch {
                val iconResult = IconApi.retrofitService.getIcon(website, 1, "flat")
                _status.value = IconApiStatus.DONE
                Log.i("PasswordViewModel", "Received Data $iconResult")
            }
        } catch (e: Exception) {
            _status.value = IconApiStatus.ERROR
            Log.i("PasswordViewModel", "Did not receive data")
        }
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
