package ru.piteravto.webviewwithassets

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.widget.Toast

fun Context.showToast(message: String, isShort: Boolean = true) {
    Handler(Looper.getMainLooper()).post {
        val length = if (isShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
        Toast.makeText(this, message, length).show()
    }
}

/** Класс для получения ссылки на контекст приложения */
class App : Application() {
    companion object {
        private lateinit var instance: App
        val context: Context
            get() = instance.applicationContext
        val resources: Resources get() = context.resources
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}