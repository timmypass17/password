package com.example.passwordapp.data.password

import androidx.room.*
import com.example.passwordapp.data.password.Password
import kotlinx.coroutines.flow.Flow

@Dao
interface PasswordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(password: Password)

    @Update
    suspend fun update(password: Password)

    @Delete
    suspend fun delete(password: Password)

    @Query("SELECT * from password WHERE id = :id")
    fun getPassword(id: Int): Flow<Password>

    @Query("SELECT * from password ORDER BY website ASC")
    fun getItems(): Flow<List<Password>>
}