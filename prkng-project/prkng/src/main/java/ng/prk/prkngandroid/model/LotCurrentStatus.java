package ng.prk.prkngandroid.model;

public class LotCurrentStatus {
    private float mainPrice;
    private float hourlyPrice;
    private long remainingMillis;

    public LotCurrentStatus(float mainPrice, float hourlyPrice, long remainingMillis) {
        this.mainPrice = mainPrice;
        this.hourlyPrice = hourlyPrice;
        this.remainingMillis = remainingMillis;
    }

    @Override
    public String toString() {
        return "LotCurrentStatus{" +
                "mainPrice=" + mainPrice +
                ", hourlyPrice=" + hourlyPrice +
                ", remainingMillis=" + remainingMillis +
                '}';
    }
}
