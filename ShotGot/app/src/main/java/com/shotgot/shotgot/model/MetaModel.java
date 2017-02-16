package com.shotgot.shotgot.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by gsierra on 8/02/17.
 */

public class MetaModel {

    @SerializedName("id")
    String mId;
    @SerializedName("cv_api")
    private String mCv_api;
    @SerializedName("type")
    private String mType;
    @SerializedName("data")
    private ArrayList mData;

    public MetaModel(String id, String cv_api, String mType, ArrayList data) {
        this.mId = id;
        this.mCv_api = cv_api;
        this.mType = mType;
        this.mData = data;
    }

    public boolean isColor() {
        if (this.mType.equals(MetaType.CLRs))
            return true;
        return false;
    }

    public boolean isNotEmpty() {
        if (!this.mData.equals("undefined"))
            return true;
        return false;
    }

    public String getmCv_api() {
        return mCv_api;
    }

    public void setmCv_api(String mCv_api) {
        this.mCv_api = mCv_api;
    }

    public ArrayList getData() {
//        if(isColor())
        return mData;
    }

    public void setData(ArrayList data) {
        this.mData = data;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    @Override
    public String toString() {
        return "MetaModel{" +
                "id='" + mId + '\'' +
                ", mCv_api='" + mCv_api + '\'' +
                ", mType='" + mType + '\'' +
                ", data=" + mData +
                '}';
    }

    /**
     * Supported Computer Vision APIs
     */
    public enum VisionAPI {
        GOGL, CLRF, CSHT, IMGG, BLPR, AMZN, MSFT, IBM
    }

    /**
     * Supported TYPES according the kind of wrapped data.
     * Those ending with -s are meant to be array tipes
     */
    public enum MetaType {
        TAGs, CLRs, OCRs, LGO, TXT
    }
}
