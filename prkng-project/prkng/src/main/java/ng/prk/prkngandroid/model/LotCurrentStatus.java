package ng.prk.prkngandroid.model;

public class LotCurrentStatus {
    private float mainPrice;
    private float hourlyPrice;
    private long remainingMillis;
    private boolean isFree;

    public LotCurrentStatus(float mainPrice, float hourlyPrice, long remainingMillis, boolean isFree) {
        this.mainPrice = mainPrice;
        this.hourlyPrice = hourlyPrice;
        this.remainingMillis = remainingMillis;
        this.isFree = isFree;
    }

    public LotCurrentStatus(long remainingMillis) {
        this.remainingMillis = remainingMillis;
        this.isFree = false;
    }

    public float getMainPrice() {
        return mainPrice;
    }

    public int getMainPriceRounded() {
        return Math.round(mainPrice);
    }

    public float getHourlyPrice() {
        return hourlyPrice;
    }

    public int getHourlyPriceRounded() {
        return Math.round(hourlyPrice);
    }

    public long getRemainingMillis() {
        return remainingMillis;
    }

    public boolean isFree() {
        return isFree;
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
