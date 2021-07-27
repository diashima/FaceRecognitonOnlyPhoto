package kz.tengrilab.facerecognitononlyphoto

import android.content.Context
import androidx.fragment.app.FragmentActivity

object Variables {
    const val url = "http://10.150.34.13"
    const val port = ":10100"
    const val headers = "Accept: application/json; version=1.0"
    const val headers2 = "Token "
    const val sharedPrefToken = "token"
    const val sharedPrefLogin = "login"
    const val sharedPrefAddressName = "AddressName"
    const val sharedPrefVPN = "IP_VPN"
    const val sharedPrefQrity = "IP_Qrity"

    fun loadCredentials(activity: FragmentActivity) : String? {
        val sharedPref = activity.getSharedPreferences(
            sharedPrefLogin,
            Context.MODE_PRIVATE
        )!!
        return sharedPref.getString(sharedPrefToken, null)
    }
}