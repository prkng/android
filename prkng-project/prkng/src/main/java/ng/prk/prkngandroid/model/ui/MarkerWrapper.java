package ng.prk.prkngandroid.model.ui;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;

public class MarkerWrapper extends AnnotationWrapper {
    private MarkerOptions markerOptions;
    private Marker marker;
    private int price;
    private boolean isHidden;

    public MarkerWrapper(String featureId, String title, int type) {
        super(featureId, title, type);
    }

    private MarkerWrapper(Builder builder) {
        super(builder.featureId, builder.title, builder.type);

        this.markerOptions = builder.markerOptions;
        this.marker = builder.marker;
        this.price = builder.price;
        this.isHidden = builder.isHidden;
    }

    public MarkerOptions getMarkerOptions() {
        return markerOptions;
    }

    public void setMarkerOptions(MarkerOptions markerOptions) {
        this.markerOptions = markerOptions;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public static class Builder extends AnnotationWrapper.Builder {

        private MarkerOptions markerOptions;
        private Marker marker;
        private int price;
        private boolean isHidden;

        public Builder(String featureId) {
            super(featureId);
        }

        public Builder markerOptions(MarkerOptions markerOptions) {
            this.markerOptions = markerOptions;
            this.title = markerOptions.getTitle();
//            this.featureId = markerOptions.getSnippet();
            return this;
        }

        public Builder marker(Marker marker) {
            this.marker = marker;
            return this;
        }

        public Builder price(int price) {
            this.price = price;
            return this;
        }

        public Builder hidden() {
            this.isHidden = true;
            return this;
        }

        public Builder visible() {
            this.isHidden = false;
            return this;
        }

        @Override
        public MarkerWrapper build() {
            return new MarkerWrapper(this);
        }
    }
}
