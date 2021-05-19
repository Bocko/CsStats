package hu.bme.aut.android.stats.model.inventory

class DescriptionItem {
    var icon_url: String? = null
    var name: String? = null
    var market_hash_name: String? = null
    var market_name: String? = null
    var type: String? = null
    var descriptions: List<DescriptionDescItem>? = null
    var actions: List<DescActionsItem>? = null
    var tags: List<TagItem>? = null
}