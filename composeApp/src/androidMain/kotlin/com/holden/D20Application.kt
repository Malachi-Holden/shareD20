package com.holden

import android.app.Application
import com.holden.di.initKoin

class D20Application: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}