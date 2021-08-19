package kz.tengrilab.facerecognitononlyphoto.carnumber

import kz.tengrilab.facerecognitononlyphoto.Variables
import kz.tengrilab.facerecognitononlyphoto.data.CarDetail
import retrofit2.Call
import retrofit2.http.*

interface GetCarDetailsInterface {
    @Headers(Variables.headers)
    @GET("/api/get-car-info")
    fun uploadImage(
        @Header("Authorization") header: String,
        @Query("car_number") carNumber: String,
    ): Call<CarDetail>
}