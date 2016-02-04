package ng.prk.prkngandroid.model.foursquare;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class Location {
    String address;
    String city;
    String postalCode;
    String country;
    double lat;
    double lng;
    int distance;

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    public double getLatitude() {
        return lat;
    }

    public double getLongitude() {
        return lng;
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "Location{" +
                "address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", country='" + country + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", distance=" + distance +
                '}';
    }
}
