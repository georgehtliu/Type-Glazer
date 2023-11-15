import kotlinx.serialization.Serializable

@Serializable
data class Challenge(val challengeID: Int, val fromUserID: Int, val toUserID: Int, val textID: Int, val raceID: Int)