package hu.bme.aut.android.stats.model.stats

class PlayerStats {
    var steamID: String? = null
    var gameName: String? = null
    var stats: List<Stat>? = null
    //var stats: Map<String,String>? = null
    var achievements: List<Achievement>? = null
}