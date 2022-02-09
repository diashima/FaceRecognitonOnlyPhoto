package kz.tengrilab.fr_cppr.data

import com.google.gson.annotations.SerializedName

data class Face(
    val count: Int,
    val links: Links,
    val results: List<Result>
) {
    data class Links(
        val next: Any,
        val previous: Any
    )

    data class Result(
        @SerializedName("face_id")
        val faceId: String,
        @SerializedName("image_link")
        val imageLink: String,
        @SerializedName("results_path")
        val resultsPath: String,
        val timestamp: String,
        @SerializedName("unique_id")
        val uniqueId: String,
        @SerializedName("photo_path")
        val photoPath: String,
        @SerializedName("photo_original")
        val photoOriginal: String
    )
}