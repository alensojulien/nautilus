package iut.julien.nautilus.ui.model

/**
 * Data class representing a simple object from the database. (e.g. a boat, a person, etc.)
 *
 * @param id The object's ID.
 * @param name The object's name.
 */
data class DatabaseObject(
    val id: String = "",
    val name: String = ""
) {
    override fun toString(): String {
        return this.name
    }
}
