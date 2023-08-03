package com.example.locationtest
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LocationDao {
    @Insert
    suspend fun insert(locationData: LocationData)
    @Query("SELECT * FROM location_data")
    fun getLocations() : LiveData<List<LocationData>>
}
