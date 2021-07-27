package kz.tengrilab.facerecognitononlyphoto.results

import kz.tengrilab.facerecognitononlyphoto.Variables
import kz.tengrilab.facerecognitononlyphoto.data.Face
import kz.tengrilab.facerecognitononlyphoto.data.Man
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

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
}