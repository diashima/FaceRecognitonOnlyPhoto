package kz.tengrilab.fr_cppr.data

data class ResultCar(
    val links: Any,
    val count: Int,
    val results: List<ResultCarDetails>
){
    data class ResultCarDetails(
        val id: Int,
        val car_number: String,
        val response: String,
        val timestamp: String,
        val user_id: Int,
        val user_firstname: String?,
        val user_lastname: String?,
        val user_middlename: String?,
        val username: String?
    )
}
