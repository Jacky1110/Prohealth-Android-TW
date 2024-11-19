package com.v7idea.template.Tool;

/**
 * Created by mortal on 2017/9/26.
 */

public class V7BaseNameValuePair implements V7NameValuePair
{
    private String strName = null;
    private String strValue = null;

    public V7BaseNameValuePair(String name, String value) {
        strName = name;
        strValue = value;
    }

    @Override
    public String getName() {
        return strName;
    }

    @Override
    public String getValue() {
        return strValue;
    }

    @Override
    public String toString() {
        return strName +"="+ strValue;
    }
}
