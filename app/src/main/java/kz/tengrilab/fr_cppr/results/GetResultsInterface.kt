package kz.tengrilab.fr_cppr.results

import kz.tengrilab.fr_cppr.Variables
import kz.tengrilab.fr_cppr.data.Face
import kz.tengrilab.fr_cppr.data.Man
import kz.tengrilab.fr_cppr.data.ResultCar
import retrofit2.Call
import retrofit2.http.*

interface GetResultsInterface {
    @Headers(Variables.headers)
    @GET("/api/get-face-results-old?")
    fun getResults(
        @Header("Authorization") header: String,
        @Query("page") page: String
    ) : Call<Face>

    @Headers(Variables.headers)
    @GET("/api/get-face-details?")
    fun getDetails(
        @Header("Authorization") header: String,
        @Query("results_path") resultPath: String,
        @Query("unique_id") uniqueId: String,
        @Query("face_id") faceId: String,
        @Query("top") top: String
    ) : Call<List<Man>>


    @Headers(Variables.headers)
    @POST("/api/car-info-search")
    fun getCarResults(
        @Header("Authorization") header: String,
        @Query("page_size") pageSize: Int,
        @Query("page") page: Int
    ) : Call<ResultCar>
}