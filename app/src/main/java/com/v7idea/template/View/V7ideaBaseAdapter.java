package com.v7idea.template.View;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.v7idea.template.Tool.ListItem;

import java.util.ArrayList;

/**
 * Created by mortal on 16/8/13.
 */
public abstract class V7ideaBaseAdapter extends BaseAdapter
{
    protected ArrayList<ListItem> dataArray = null;

    protected int selectedPosition = -1;

    public void setSelectedPosition(int intPosition)
    {
        selectedPosition = intPosition;
    }

    public int getSelectedPosition()
    {
        return selectedPosition;
    }

    public void setData(ArrayList<ListItem> dataArray)
    {
        this.dataArray = dataArray;
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        if(dataArray != null)
        {
            return dataArray.size();
        }
        else
        {
            return 0;
        }
    }

    @Override
    public ListItem getItem(int position)
    {
        if(dataArray != null && position > -1 && position < dataArray.size())
        {
            return dataArray.get(position);
        }
        else
        {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return getItemView( position, convertView, parent);
    }


    public abstract View getItemView(int position, View convertView, ViewGroup parent);
}
