package de.dimedis.mobileentry.backend.response

import com.google.gson.annotations.SerializedName

class Keys {
    @SerializedName(F1)
    var f1: String? = null

    @SerializedName(F2)
    var f2: String? = null

    @SerializedName(F3)
    var f3: String? = null

    @SerializedName(F4)
    var f4: String? = null

    fun f1(): String? {
        return f1
    }

    val getF2: String?
        get() = f2

//    fun getF2(): String? {
//        return f2
//    } this.f3 = f3

    val getF3: String?
        get() = f3

    val getF4: String?
        get() = f4

    fun f1(f1: String?) {
        this.f1 = f1
    }

    fun f2(f2: String?) {
        this.f2 = f2
    }

    fun f3(f3: String?) {
        this.f3 = f3
    }

    fun f4(f4: String?) {
        this.f4 = f4
    }

    companion object {
        const val F1 = "f1"
        const val F2 = "f2"
        const val F3 = "f3"
        const val F4 = "f4"
    }
} //end of class
