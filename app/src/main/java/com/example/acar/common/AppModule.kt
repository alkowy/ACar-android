package com.example.acar.common

import android.content.Context
import android.location.Geocoder
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule{

    @Provides
    fun provideAuthRepo(): FirebaseAuth{
        return FirebaseAuth.getInstance()
    }
    @Provides
    fun provideGoogleApisRepo(): GoogleApiRepository{
        return GoogleApiRepository()
    }

}