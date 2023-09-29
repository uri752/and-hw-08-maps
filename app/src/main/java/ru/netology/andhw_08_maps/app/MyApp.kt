package ru.netology.andhw_08_maps.app

import android.app.Application
import android.util.Log
import com.yandex.mapkit.MapKitFactory
import ru.netology.andhw_08_maps.BuildConfig

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
    }
}