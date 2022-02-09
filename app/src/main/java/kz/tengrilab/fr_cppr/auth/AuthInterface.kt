package kz.tengrilab.fr_cppr.auth

import kz.tengrilab.fr_cppr.Variables
import kz.tengrilab.fr_cppr.data.Auth
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthInterface {
    @Headers(Variables.headers)
    @POST("/api/login")
    fun getUser(@Body body: RequestBody): Call<Auth>
}