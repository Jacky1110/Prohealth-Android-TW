package com.v7idea.template.Tool;

public class DebugLog {

    public static boolean isOpenLog = false;

    private static final String projectName = "healthkit";

    public static void i(String tag, String showString)
    {
        if(isOpenLog)android.util.Log.i(projectName, tag + " " + showString);
    }

    public static void d(String tag, String showString)
    {
        if(isOpenLog)android.util.Log.d(projectName, tag + " " + showString);
    }

    public static void e(String tag, String showString)
    {
        if(isOpenLog)android.util.Log.e(projectName, tag + " " + showString);
    }
}
