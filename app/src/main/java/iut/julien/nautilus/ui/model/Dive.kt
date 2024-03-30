package iut.julien.nautilus.ui.model

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
