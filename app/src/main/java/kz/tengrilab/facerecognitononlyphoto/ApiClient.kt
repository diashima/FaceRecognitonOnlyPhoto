package kz.tengrilab.facerecognitononlyphoto

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private lateinit var retrofit: Retrofit
    //private const val cacheSize = (10 * 1024 * 1024).toLong()


    fun getRetrofitClient() : Retrofit {

        //val ip = loadIp(context)
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(Variables.url + Variables.port)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit
    }

    /*private fun loadIp(context: Context) : String? {
        val sharedPreferences = context.getSharedPreferences(Variables.sharedPrefIp, Context.MODE_PRIVATE)!!
        return sharedPreferences.getString(Variables.sharedPrefIp, Variables.url)
    }*/
}