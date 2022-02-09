package kz.tengrilab.fr_cppr.data

import com.google.gson.annotations.SerializedName

data class CarDetailById(
    val success: Boolean,
    val data: ResultCarDetailsById,
    val message: String
) {
    data class ResultCarDetailsById(
        val id: Int,
        @SerializedName("car_number")
        val carNumber: String,
        val response: String,
        @SerializedName("timestamp_formatted")
        val timestamp: String,
        val user: Int,
        val username: String?
    )
}
