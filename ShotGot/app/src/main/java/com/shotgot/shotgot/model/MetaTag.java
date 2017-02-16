package com.shotgot.shotgot.model;

/**
 * Created by gsierra on 15/02/17.
 */

public class MetaTag<T> {

    T data;

    public MetaTag(T data) {
        super();
        this.data = data;
    }

    @Override
    public String toString() {
        return "MetaTag{" +
                "data=" + data +
                '}';
    }

}
