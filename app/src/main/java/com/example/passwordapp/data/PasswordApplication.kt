package com.example.passwordapp.data

import android.app.Application
import com.example.passwordmanager.data.PasswordRoomDatabase

class PasswordApplication : Application(){
    val database: PasswordRoomDatabase by lazy { PasswordRoomDatabase.getDatabase(this) }
}