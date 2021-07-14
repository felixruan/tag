package org.etocrm.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;

public class StringUtil {

    public static String valueOf(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    public static String humpToLine2(String str) {
        Pattern humpPattern = compile("[A-Z]");
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
