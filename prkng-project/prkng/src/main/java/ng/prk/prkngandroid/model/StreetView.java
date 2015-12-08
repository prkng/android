package ng.prk.prkngandroid.model;

public class StreetView {
    private String id;
    private float head;

    public String getId() {
        return id;
    }

    public float getHead() {
        return head;
    }

    @Override
    public String toString() {
        return "StreetView{" +
                "id='" + id + '\'' +
                ", head=" + head +
                '}';
    }
}
