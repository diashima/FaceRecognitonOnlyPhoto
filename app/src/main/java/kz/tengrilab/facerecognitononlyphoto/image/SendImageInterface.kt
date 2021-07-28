package kz.tengrilab.facerecognitononlyphoto.image

import kz.tengrilab.facerecognitononlyphoto.Variables
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface SendImageInterface {
    @Multipart
    @Headers(Variables.headers)
    @POST("/api/load-crops")
    fun uploadImage(
        @Header("Authorization") header: String,
        @Part file: MultipartBody.Part,
        @Part code: MultipartBody.Part
    ): Call<ResponseBody>

    @Multipart
    @Headers(Variables.headers)
    @POST("/api/load-origin")
    fun uploadOrigin(
        @Header("Authorization") header: String,
        @Part file: MultipartBody.Part,
        @Part code: MultipartBody.Part
    ): Call<ResponseBody>

    @Multipart
    @Headers(Variables.headers)
    @POST("/producer/detect-faces")
    fun uploadMultipleImage(
        @Header("Authorization") header: String,
        @Part surveyImage: Array<MultipartBody.Part?>
    ): Call<ResponseBody>
}