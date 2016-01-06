package ng.prk.prkngandroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.mapbox.mapboxsdk.geometry.LatLng;

import ng.prk.prkngandroid.Const;

public class LatLong extends LatLng {
    public static final Parcelable.Creator<LatLng> CREATOR = new Parcelable.Creator<LatLng>() {
        public LatLng createFromParcel(Parcel in) {
            return new LatLong(in);
        }

        public LatLng[] newArray(int size) {
            return new LatLng[size];
        }
    };

    protected LatLong(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
    }

    @SerializedName(Const.ApiArgs.GEO_LAT)
    private double latitude;
    @SerializedName(Const.ApiArgs.GEO_LNG)
    private double longitude;

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }
}
