package com.example.accidentnotificationapp.detection

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SensorModule {
    @Provides
    @Singleton
    @Accelerometer
    fun provideAccelerometerSensor(app: Application): MeasurableSensor {
        return AccelerometerSensor(app)
    }
    @Provides
    @Singleton
    @Gyroscope
    fun provideGyroscopeSensor(app: Application): MeasurableSensor {
        return GyroscopeSensor(app)
    }
}