package de.dimedis.mobileentry.fragments.drag_and_drop.util

import de.dimedis.mobileentry.model.Item

class EmptyTargetItem : Item {
    constructor() {
        functionName = ""
        ItemString = ""
    }

    constructor(position: Int) {
        functionName = ""
        ItemString = ""
        setPosition(position)
    }
}