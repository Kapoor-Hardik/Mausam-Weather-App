package com.example.mausamweather.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mausamweather.data.local.entity.WeatherEntity

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeather(weatherEntity: WeatherEntity): Long

    @Query("SELECT * FROM weather_cache WHERE cityId = :cityId")
    fun getWeatherByCityId(cityId: Long): WeatherEntity?

    @Query("SELECT * FROM weather_cache WHERE LOWER(cityName) = LOWER(:cityName) LIMIT 1")
    fun getWeatherByCityName(cityName: String): WeatherEntity?

    @Query("SELECT * FROM weather_cache ORDER BY cachedTimestamp DESC")
    fun getAllCachedWeather(): List<WeatherEntity>

    @Query("SELECT * FROM weather_cache ORDER BY cachedTimestamp DESC LIMIT 5")
    fun getRecentCachedCities(): List<WeatherEntity>

    @Query("DELETE FROM weather_cache WHERE cityId = :cityId")
    fun deleteWeatherByCityId(cityId: Long): Int

    @Query("DELETE FROM weather_cache")
    fun clearAllCache(): Int
}
