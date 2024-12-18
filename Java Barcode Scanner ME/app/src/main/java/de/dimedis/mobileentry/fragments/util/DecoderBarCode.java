package de.dimedis.mobileentry.fragments.util;

import java.util.StringTokenizer;

public class DecoderBarCode {

    protected String codeA;
    protected String codeB;

    public DecoderBarCode(String code) {
        StringTokenizer st = new StringTokenizer(code);
        if (st.hasMoreTokens()) codeA = st.nextToken();
        if (st.hasMoreTokens()) codeB = st.nextToken();
    }
}
