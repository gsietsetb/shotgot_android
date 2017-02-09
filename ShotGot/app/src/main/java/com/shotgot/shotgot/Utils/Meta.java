package com.shotgot.shotgot.Utils;

import android.graphics.Color;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by gsierra on 8/02/17.
 */

public class Meta {
//    {"respTag":{"id":"ByhcvN_ux","API":"Cloudsight","type":"Text","data":"white plastic storage bin"}}

    /**
     * Supported APIs
     */
    public static final String API_GOOGLE = "Google";
    public static final String API_CLARIFAI = "Clarifai";
    public static final String API_CLOUDSIGHT = "Cloudsight";
    /**
     * Not yet implemented
     */
    public static final String API_BLIPPAR = "BLIPPAR";
    public static final String API_AMAZON = "AMAZON";
    public static final String API_IBM = "IBM";
    /**
     * TYPES are encoded somehow with its parent's API int
     */
    public static final String TYPE_LABELS = "Labels";
    public static final String TYPE_COLORS = "Colors";
    public static final String TYPE_OCR = "OCR";
    public static final String TYPE_LOGO = "Logo";
    public static final String TYPE_DESCR = "Descr";
    /**
     * TYPES are encoded somehow with its parent's API int
     */
    public static final String KEY_ID = "id";
    public static final String KEY_API = "API";
    public static final String KEY_TYPE = "type";
    public static final String KEY_DATA = "data";
    public String id;
    public String API;
    public String type;
    public String data;
    public ArrayList<Integer> colorArray;
    public String[] data_array;

    /***/
    public Meta(JSONObject input) {
        try {
            this.id = input.getString(KEY_ID);
            this.API = input.getString(KEY_API);
            this.type = input.getString(KEY_TYPE);
            switch (this.type) {
                case TYPE_LOGO:
                case TYPE_OCR:
                case TYPE_LABELS: //ToBe substracted...
                case TYPE_DESCR:
                    this.data = input.getString(KEY_DATA);
                    break;
                case TYPE_COLORS:
                    this.colorArray = new ArrayList<>();
                    JSONArray jArray = input.getJSONArray(KEY_DATA);
                    if (jArray != null) {
                        for (int i = 0; i < jArray.length(); i++) {
                            Log.d("API: ", jArray.getString(i));
                            this.colorArray.add(Color.parseColor(jArray.getString(i)));
                        }
                    }
                    break;
                /*case TYPE_LABELS:
                    *TODO implement more accurately
                    this.data=input.getString("data");
                    break;*/
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isCorrect() {
        switch (this.API) {
            case API_GOOGLE:
                if (this.type.equals(TYPE_DESCR))
                    return false;
            case API_CLARIFAI:
                if (!this.type.equals(TYPE_COLORS) ||
                        !this.type.equals(TYPE_LABELS))
                    return false;
            case API_CLOUDSIGHT:
                if (!this.type.equals(TYPE_DESCR))
                    return false;
        }
        return true;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Meta{" +
                "id='" + id + '\'' +
                ", API='" + API + '\'' +
                ", type='" + type + '\'' +
                ", data='" + data + '\'' +
                ", data_array=" + Arrays.toString(data_array) +
                '}';
    }
}
