package kz.tengrilab.facerecognitononlyphoto.data

data class Params(
    val date_from: String?,
    val date_to: String,
    val group_ids: List<String>?,
    val user_ids: List<String>?
)
