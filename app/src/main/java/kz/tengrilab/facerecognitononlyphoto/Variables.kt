package kz.tengrilab.facerecognitononlyphoto

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.fragment.app.FragmentActivity

object Variables {
    //const val imageUrl = "http://10.150.34.13"
    //const val url = "http://10.150.34.13"
    const val url = "http://10.77.6.62"
    //const val imageUrl = "http://10.77.6.62"
    //const val url = "http://10.180.13.32"
    const val port = ":10150"
    const val headers = "Accept: application/json; version=1.0"
    const val headers2 = "Token "
    const val sharedPrefToken = "token"
    const val sharedPrefLogin = "login"
    const val saveLogin = "saveLogin"
    const val sharedPrefId = "id"
    const val sharedPrefIp = "ip"

    fun loadCredentials(activity: FragmentActivity) : String? {
        val sharedPref = activity.getSharedPreferences(
            sharedPrefLogin,
            Context.MODE_PRIVATE
        )!!
        return sharedPref.getString(sharedPrefToken, null)
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context) : String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
}