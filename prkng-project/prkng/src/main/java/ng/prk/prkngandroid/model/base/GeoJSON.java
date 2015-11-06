package ng.prk.prkngandroid.model.base;

import java.util.List;

public abstract class GeoJSON<V extends GeoJSONFeature> {
    protected List<V> features;
    protected String type;

    public List<V> getFeatures() {
        return features;
    }

    public String getType() {
        return type;
    }
}
