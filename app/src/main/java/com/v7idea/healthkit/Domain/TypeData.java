package com.v7idea.healthkit.Domain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TypeData {
    ArrayList<TypeData> typeDataArrayList = new ArrayList<TypeData>();
    String TypeID = "";
    String TypeName = "";

    public TypeData() {

    }
    public TypeData(String typeID, String typeName) {
        TypeID = typeID;
        TypeName = typeName;
    }

    public String getTypeID() {
        return TypeID;
    }

    public void setTypeID(String typeID) {
        TypeID = typeID;
    }

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String typeName) {
        TypeName = typeName;
    }

    public ArrayList<TypeData> parseData(String string) {
        try {

            if (string != null && !string.isEmpty()) {
                JSONArray jsonArray = new JSONArray(string);
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String jasonString = jsonArray.optString(i);
                        try {
                            JSONObject jsonObject = new JSONObject(jasonString);
                            String typeID = jsonObject.optString("typeID");
                            String typeName = jsonObject.optString("typeName");
                            typeDataArrayList.add(new TypeData(typeID, typeName));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return typeDataArrayList;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}

