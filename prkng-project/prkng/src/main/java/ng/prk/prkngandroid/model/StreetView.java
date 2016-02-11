package ng.prk.prkngandroid.model;

import com.google.android.gms.maps.model.LatLng;

public class StreetView {
    private String id;
    private float head;
    private LatLng latLng;

    public String getId() {
        return id;
    }

    public float getHead() {
        return head;
    }

    public void setLatLng(double latitude, double longitude) {
        this.latLng = new LatLng(latitude, longitude);
    }

    public LatLng getLatLng() {
        return this.latLng;
    }

    @Override
    public String toString() {
        return "StreetView{" +
                "id='" + id + '\'' +
                ", head=" + head +
                ", latLng=" + latLng +
                '}';
    }
}
