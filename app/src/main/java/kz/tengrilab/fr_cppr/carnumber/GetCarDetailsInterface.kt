package kz.tengrilab.fr_cppr.carnumber

import kz.tengrilab.fr_cppr.Variables
import kz.tengrilab.fr_cppr.data.CarDetail
import kz.tengrilab.fr_cppr.data.CarDetailById
import kz.tengrilab.fr_cppr.data.CarDetailByIin
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
    ): Call<CarDetailByIin>

    @Headers(Variables.headers)
    @GET("/api/get-requested-car-info")
    fun sendCarId(
        @Header("Authorization") header: String,
        @Query("id") id: Int,
    ): Call<CarDetailById>
}