package iut.julien.nautilus.ui.model

/**
 * Data class representing a dive from the database.
 *
 * @param diveId The dive's ID.
 * @param diveDate The dive's date.
 * @param diveTime The dive's time.
 * @param diveDepth The dive's depth.
 * @param diveLocation The dive's location.
 * @param diveNumberDivers The number of divers in the dive.
 * @param diveMaxNumberDivers The maximum number of divers in the dive.
 * @param diveDivers The list of divers in the dive.
 * @param diveDiversID The list of divers' IDs in the dive.
 * @param isLiked The dive's like status.
 */
data class Dive(
    var diveId: String,
    var diveDate: String,
    var diveTime: String,
    var diveDepth: String,
    var diveLocation: String,
    var diveNumberDivers: String,
    var diveMaxNumberDivers: String,
    var diveDivers: ArrayList<Diver> = ArrayList(),
    var diveDiversID: ArrayList<String> = ArrayList(),
    var isLiked: Boolean = false
) {
    override fun toString(): String {
        return "Dive(diveId=$diveId, diveDate='$diveDate', diveTime='$diveTime', diveDepth=$diveDepth, diveLocation='$diveLocation', diveNumberDivers=$diveNumberDivers, diveMaxNumberDivers=$diveMaxNumberDivers)"
    }
}
