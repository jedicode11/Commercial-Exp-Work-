package de.dimedis.mobileentry.model

open class Item {
    var functionName: String? = null
    var ItemString: String? = null
    var iconRes = 0

    constructor() {}
    constructor(t: String?, iconResId: Int, functionName: String?) {
        ItemString = t
        this.functionName = functionName
        iconRes = iconResId
    }

    val getItemString: String?
        get() = ItemString

    val getIconRes: Int
        get() = iconRes

    var position = 0

    @JvmName("setPosition1")
    fun setPosition(position: Int) {
        this.position = position
    }

    val getPosition: Int
        get() = position
}