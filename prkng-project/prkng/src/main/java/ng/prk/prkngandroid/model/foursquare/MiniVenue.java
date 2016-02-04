package ng.prk.prkngandroid.model.foursquare;


public class MiniVenue {
    String id;
    String name;
    Location location;
//    Object categories;


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "MiniVenue{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", location=" + location +
                '}';
    }
}
