package kz.tengrilab.fr_cppr.data

data class CarDetail(
    val message: String,
    val success: Boolean,
    val `data`: Data
) {
    data class Data(
        val car_model: String,
        val car_number: String,
        val car_number_type: String,
        val car_year: String,
        val fiz: Fiz?,
        val grcode: String,
        val iur: String,
        val teh_passport: String,
        val teh_passport_date: String,
        val vin: String,
        val vu_end: String,
        val vu_serial: String,
        val vu_start: String
    ) {
        data class Fiz(
            val firstname: String,
            val gr_code: String,
            val id: Int,
            val lastname: String,
            val secondname: String?,
            val ud_code: String,
            val udcode_last: Any,
            val uddate: String,
            val uddate_last: Any,
            val unique_id: String
        )
    }
}