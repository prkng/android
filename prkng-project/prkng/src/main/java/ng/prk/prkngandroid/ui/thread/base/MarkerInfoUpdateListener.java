package ng.prk.prkngandroid.ui.thread.base;

import java.util.ArrayList;

import ng.prk.prkngandroid.io.PrkngApiError;
import ng.prk.prkngandroid.model.LotAttrs;
import ng.prk.prkngandroid.model.LotCurrentStatus;

public interface MarkerInfoUpdateListener {
    void setRemainingTime(long time);

    void setCurrentStatus(LotCurrentStatus status, int capacity);

    void setDataset(ArrayList data);

    void onFailure(PrkngApiError e);

    void setAttributes(LotAttrs attrs);
}
