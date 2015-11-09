package ng.prk.prkngandroid.model.base;

import ng.prk.prkngandroid.model.GeoJSONFeatureProperties;

public abstract class GeoJSONFeature<V extends GeoJSONFeatureGeometry> {
    protected V geometry;
    protected String id;
    protected GeoJSONFeatureProperties properties;
    protected String type;

    public V getGeometry() {
        return geometry;
    }

    public String getId() {
        return id;
    }

    public GeoJSONFeatureProperties getProperties() {
        return properties;
    }

    public String getType() {
        return type;
    }
}
