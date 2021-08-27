package kz.tengrilab.facerecognitononlyphoto.carnumber

import kz.tengrilab.facerecognitononlyphoto.Variables
import kz.tengrilab.facerecognitononlyphoto.data.CarDetail
import retrofit2.Call
import retrofit2.http.*

interface GetCarDetailsInterface {
    @Headers(Variables.headers)
    @GET("/api/get-car-info")
    fun sendCarNumber(
        @Header("Authorization") header: String,
        @Query("car_number") carNumber: String,
    ): Call<CarDetail>

    @Headers(Variables.headers)
    @GET("/api/get-person-info")
    fun sendPersonIin(
        @Header("Authorization") header: String,
        @Query("iin") carNumber: String,
    ): Call<CarDetail>
}