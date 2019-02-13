package com.wisedu.emap.itpub.util;

public class XssUtils {
    public static String xssEncode(String s) {  
        if (s == null || s.isEmpty()) {  
            return s;  
        } 
        StringBuilder sb = new StringBuilder(s.length() + 16);  
        for (int i = 0; i < s.length(); i++) {  
            char c = s.charAt(i);  
            switch (c) {  
            case '>':  
                sb.append("&gt;");// 转义大于号  
                break;  
            case '<':  
                sb.append("&lt;");// 转义小于号  
                break;  
            case '\'':  
                sb.append("&apos;");// 转义单引号  
                break;  
            case '\"':  
                sb.append("&quot;");// 转义双引号  
                break;  
            case '&':  
                sb.append("&amp;");// 转义&  
                break;  
            case '#':  
                sb.append("＃");// 转义#  
                break;  
            default:  
                sb.append(c);  
                break;  
            }  
        }  
        return sb.toString();  
    }  
}
