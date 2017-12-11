package com.test.myapplication;

import android.text.TextUtils;

/**
 * Created by Hendry Setiadi
 */

public class StringUtil {
    public static String omitPunctuationAndDoubleSpace(String stringToReplace){
        if (TextUtils.isEmpty(stringToReplace)){
            return "";
        }
        else {
            return stringToReplace.replaceAll("\\r|\\n", " ")
                    .replaceAll("\\s+", " ")
                    .replaceAll("[^0-9a-zA-Z ]", "")
                    .trim();
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static String removeLeadingZero(String s) {
        while (s.length() > 1 && s.indexOf("0")==0) {
            s = s.substring(1);
        }
        return s;
    }

    public static String addLeadingZero(String s, int length) {
        while (s.length() < length) {
            s = "0" + s;
        }
        return s;
    }

    public static String removeAllNonNumeric(String s){
        return s.replaceAll("[^\\d]", "");
    }
}
