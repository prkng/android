package ng.prk.prkngandroid.ui.thread.base;

import java.util.ArrayList;

import ng.prk.prkngandroid.io.PrkngApiError;
import ng.prk.prkngandroid.model.LotAttrs;
import ng.prk.prkngandroid.model.LotCurrentStatus;
import ng.prk.prkngandroid.model.StreetView;

public interface MarkerInfoUpdateListener {
    //    replace by setProperties
    @Deprecated
    void setRemainingTime(long time);

    //    replace by setProperties
    @Deprecated
    void setCurrentStatus(LotCurrentStatus status, int capacity);

    //    replace by setProperties
    @Deprecated
    void setDataset(ArrayList data);

    //    replace by setProperties
    @Deprecated
    void setAttributes(LotAttrs attrs, StreetView streetView);

    void onFailure(PrkngApiError e);

    //    void setProperties(GeoJSONFeatureProperties properties);
}
