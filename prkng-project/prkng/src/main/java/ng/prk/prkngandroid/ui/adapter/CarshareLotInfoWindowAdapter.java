package ng.prk.prkngandroid.ui.adapter;

import android.view.LayoutInflater;

import ng.prk.prkngandroid.model.ui.JsonSnippet;
import ng.prk.prkngandroid.ui.adapter.base.CarshareInfoWindowAdapter;

public class CarshareLotInfoWindowAdapter extends CarshareInfoWindowAdapter {

    public CarshareLotInfoWindowAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    protected String getSubtitle(JsonSnippet snippet) {
        return snippet.getTitle();
    }

    @Override
    protected String getFigure(JsonSnippet snippet) {
        return String.valueOf(snippet.getAvailable());
    }

}
