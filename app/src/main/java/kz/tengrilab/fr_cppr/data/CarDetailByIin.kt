package kz.tengrilab.fr_cppr.data

data class CarDetailByIin(
    val `data`: Data,
    val message: String,
    val success: Boolean
) {
    data class Data(
        val car_info: List<CarInfo>?,
        val firstname: String?,
        val gr_code: String?,
        val id: Int,
        val lastname: String?,
        val secondname: String?,
        val ud_code: String,
        val udcode_last: Any,
        val uddate: String,
        val uddate_last: Any,
        val unique_id: String
    ) {
        data class CarInfo(
            val car_model: String,
            val car_number: String,
            val car_number_type: String,
            val car_year: String,
            val grcode: String,
            val id: Int,
            val iur: Any,
            val teh_passport: String,
            val teh_passport_date: String,
            val vin: String,
            val vu_end: String?,
            val vu_serial: String?,
            val vu_start: String?
        )
    }
}