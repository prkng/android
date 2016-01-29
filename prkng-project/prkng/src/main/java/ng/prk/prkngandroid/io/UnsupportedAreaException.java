package ng.prk.prkngandroid.io;

public class UnsupportedAreaException extends PrkngApiError {

    public UnsupportedAreaException() {
        super(404, "Area is not supported");
    }
}
