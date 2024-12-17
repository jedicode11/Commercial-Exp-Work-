package de.dimedis.mobileentry.fragments.util

import java.util.*
/**
 * Created by Softeq Development Corporation
 * http://www.softeq.com
 */
open class DecoderBarCode(code: String?) {
    protected var codeA: String? = null
    protected var codeB: String? = null

    init {
        val st = StringTokenizer(code)
        if (st.hasMoreTokens()) codeA = st.nextToken()
        if (st.hasMoreTokens()) codeB = st.nextToken()
    }
}