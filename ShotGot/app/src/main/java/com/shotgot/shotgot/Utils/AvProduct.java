package com.shotgot.shotgot.Utils;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by gsierra on 16/01/17.
 */
public class AvProduct {
    public String name;
    public String description;
    public User uploadId;
    public int id;
    public LatLng location;

    /**
     * TODO Type to be immediately changed , just for the fake google icons!
     */
    public String productIcon;

    public AvProduct(String productIcon, String name, String description, User uploadId, int id, LatLng location) {
        this.productIcon = productIcon;
        this.name = name;
        this.description = description;
        this.uploadId = uploadId;
        this.id = id;
        this.location = location;
    }


    public AvProduct() {
        this.productIcon = "";
        this.name = "";
        this.description = description;
        this.uploadId = uploadId;
        this.id = id;
        this.location = location;
    }
}
