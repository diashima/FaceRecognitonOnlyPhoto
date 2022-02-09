package kz.tengrilab.fr_cppr

import android.app.Application
import android.content.Context
import kz.tengrilab.fr_cppr.data.Face

class App : Application() {

    companion object {
        var resultList : List<Face.Result>? = null
        var ctx: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        ctx = applicationContext
    }
}