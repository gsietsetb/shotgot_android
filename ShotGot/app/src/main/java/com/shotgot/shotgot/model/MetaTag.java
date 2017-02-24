package com.shotgot.shotgot.model;

/**
 * Created by gsierra on 15/02/17.
 */

public class MetaTag<T> {

    private final Class<T> classOfId;
    private final long value;
    T data;

    public MetaTag(Class<T> classOfId, long value, T data) {
        this.classOfId = classOfId;
        this.value = value;
        this.data = data;
    }

//    class MetaTagInstanceCreator implements InstanceCreator<MetaModel<?>>{
//        public MetaTag<?> createInstance(Type type) {
//            Type[] typeParameters = ((ParameterizedType)type).getActualTypeArguments();
//            Type idType = typeParameters[0]; // Id has only one parameterized type T
//            return Id.get((Class)idType, 0L);
//        }
//    }

    @Override
    public String toString() {
        return "MetaTag{" +
                "data=" + data +
                '}';
    }

}
