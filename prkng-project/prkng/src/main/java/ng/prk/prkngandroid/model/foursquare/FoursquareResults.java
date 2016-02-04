package ng.prk.prkngandroid.model.foursquare;


import java.util.ArrayList;
import java.util.List;

public class FoursquareResults {
//    Object meta;
    Response response;

    public Response getResponse() {
        return response;
    }

    public List<MiniVenue> getVenues() {
        return response == null ? new ArrayList<MiniVenue>() :
                response.getMiniVenues();
    }
}
