package ng.prk.prkngandroid.model.base;

import java.util.List;

public abstract class GeoJSONFeatureGeometry<V> {
    protected List<V> coordinates;
    protected String type;

    public List<V> getCoordinates() {
        return coordinates;
    }

    public String getType() {
        return type;
    }
}
