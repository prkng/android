package ng.prk.prkngandroid.model;

public class DeviceData {
    private String lang;
    private String device_type = "android";
    private String device_id;

    public DeviceData(String lang, String device_id) {
        this.lang = lang;
        this.device_id = device_id;
    }
}
