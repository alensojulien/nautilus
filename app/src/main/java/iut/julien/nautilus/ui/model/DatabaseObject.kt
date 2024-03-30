package iut.julien.nautilus.ui.model

data class DatabaseObject(
    val id: String = "",
    val name: String = ""
) {
    override fun toString(): String {
        return this.name
    }
}
