package com.v7idea.template.Tool;

import java.util.Objects;

/**
 * Created by mortal on 2017/9/27.
 */

public interface ListItem
{
    public String getItemID();
    public int getIconResourceID();
    public String getImagePath();
    public String getTitle();
    public String getSubTitle();
    public String getCode();
    public int getSerialNo();
    public int getNoticeNumber();
}
