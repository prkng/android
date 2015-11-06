package ng.prk.prkngandroid.model.base;

public abstract class GeoJSONFeature<V extends GeoJSONFeatureGeometry> {
    protected V geometry;
    protected String id;
    protected Object properties;
    protected String type;

    public V getGeometry() {
        return geometry;
    }

    public String getId() {
        return id;
    }

    public Object getProperties() {
        return properties;
    }

    public String getType() {
        return type;
    }
}
