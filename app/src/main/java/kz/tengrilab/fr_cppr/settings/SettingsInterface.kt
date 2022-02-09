package kz.tengrilab.fr_cppr.settings

import kz.tengrilab.fr_cppr.Variables
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface SettingsInterface {
    /*@Multipart
    @Headers(Variables.headers)
    @POST("/api/adm/change-user-password/{id}")
    fun changePassword(
        @Header("Authorization") header: String,
        @Path("id") id: Int,
        @Part old_password: MultipartBody.Part,
        @Part password: MultipartBody.Part,
        @Part password2: MultipartBody.Part
    ): Call<ResponseBody>*/



    @Headers(Variables.headers)
    @PUT("/api/adm/change-user-password/{id}")
    fun changePassword(
        @Header("Authorization") header: String,
        @Path("id") id: Int,
        @Body body: RequestBody
    ): Call<ResponseBody?>
}