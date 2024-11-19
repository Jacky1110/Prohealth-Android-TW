package com.v7idea.template.Tool;

/**
 * Created by mortal on 2017/9/26.
 */

public class BasicV7HttpResult implements V7HttpResult
{
    private int responseCode = -1;
    private String resultString = null;
    private String exceptionString = null;

    public void setResponseCode(int responseCode)
    {
        this.responseCode = responseCode;
    }

    public void setResultString(String resultString)
    {
        this.resultString = resultString;
    }

    public void setExceptionString(String exceptionString)
    {
        this.exceptionString = exceptionString;
    }

    @Override
    public int getResponseCode()
    {
        return responseCode;
    }

    @Override
    public String getResultString()
    {
        return resultString;
    }

    public String getExceptionString()
    {
        return exceptionString;
    }


}
