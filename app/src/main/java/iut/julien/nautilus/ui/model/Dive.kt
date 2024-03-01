package iut.julien.nautilus.ui.model

data class Dive(
    private var diveId: Int,
    private var diveDate: String,
    private var diveTime: String,
    private var diveDepth: Int,
    private var diveLocation: String,
    private var diveNumberDivers: Int
) {

    fun getDiveId(): Int {
        return diveId
    }

    fun setDiveId(diveId: Int) {
        this.diveId = diveId
    }

    fun getDiveDate(): String {
        return diveDate
    }

    fun setDiveDate(diveDate: String) {
        this.diveDate = diveDate
    }

    fun getDiveTime(): String {
        return diveTime
    }

    fun setDiveTime(diveTime: String) {
        this.diveTime = diveTime
    }

    fun getDiveDepth(): Int {
        return diveDepth
    }

    fun setDiveDepth(diveDepth: Int) {
        this.diveDepth = diveDepth
    }

    fun getDiveLocation(): String {
        return diveLocation
    }

    fun setDiveLocation(diveLocation: String) {
        this.diveLocation = diveLocation
    }

    fun getDiveNumberDivers(): Int {
        return diveNumberDivers
    }

    fun setDiveNumberDivers(diveNumberDivers: Int) {
        this.diveNumberDivers = diveNumberDivers
    }
}