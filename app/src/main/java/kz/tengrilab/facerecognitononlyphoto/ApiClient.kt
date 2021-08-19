package kz.tengrilab.facerecognitononlyphoto

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val baseUrl = Variables.url + Variables.port
    private lateinit var retrofit: Retrofit

    fun getRetrofitClient(context: Context) : Retrofit {
        val ip = loadIp(context)
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(ip + Variables.port)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit
    }

    private fun loadIp(context: Context) : String? {
        val sharedPreferences = context.getSharedPreferences(Variables.sharedPrefIp, Context.MODE_PRIVATE)!!
        return sharedPreferences.getString(Variables.sharedPrefIp, Variables.url)
    }
}