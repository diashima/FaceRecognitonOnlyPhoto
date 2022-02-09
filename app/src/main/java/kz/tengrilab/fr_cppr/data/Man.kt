package kz.tengrilab.fr_cppr.data

import com.google.gson.annotations.SerializedName

data class Man(
    val distance: Double,
    @SerializedName("firstname")
    val firstName: String,
    val iin: String,
    @SerializedName("secondname")
    val secondName: String,
    val surname: String,
    @SerializedName("ud_number")
    val udNumber: String,
    @SerializedName("is_have_car")
    val isHaveCar: Boolean,
    @SerializedName("unique_id")
    val uniqueId: String
)