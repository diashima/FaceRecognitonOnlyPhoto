package kz.tengrilab.facerecognitononlyphoto.auth

import kz.tengrilab.facerecognitononlyphoto.Variables
import kz.tengrilab.facerecognitononlyphoto.data.Auth
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthInterface {
    @Headers(Variables.headers)
    @POST("/api/login")
    fun getUser(@Body body: RequestBody): Call<Auth>
}