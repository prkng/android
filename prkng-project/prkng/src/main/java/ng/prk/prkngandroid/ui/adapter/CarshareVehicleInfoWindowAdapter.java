package ng.prk.prkngandroid.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.model.ui.JsonSnippet;
import ng.prk.prkngandroid.ui.adapter.base.CarshareInfoWindowAdapter;

public class CarshareVehicleInfoWindowAdapter extends CarshareInfoWindowAdapter {

    private String fuelTemplate;
    private String namePartnerIdTemplate;

    public CarshareVehicleInfoWindowAdapter(LayoutInflater inflater) {
        super(inflater);
        this.fuelTemplate = inflater.getContext()
                .getString(R.string.carshare_vehicle_fuel_percentage);
        this.namePartnerIdTemplate = inflater.getContext()
                .getString(R.string.carshare_vehicle_name_partner_id);
    }

    @Override
    protected String getSubtitle(JsonSnippet snippet) {
        final String partnerId = snippet.getPartnerId();
        if (TextUtils.isEmpty(partnerId)) {
            return snippet.getTitle();
        } else {
            return String.format(namePartnerIdTemplate,
                    snippet.getTitle(),
                    partnerId);
        }
    }

    @Override
    protected String getFigure(JsonSnippet snippet) {
        final int fuel = snippet.getFuel();
        if (fuel == Const.UNKNOWN_VALUE) {
            return null;
        } else {
            return String.format(fuelTemplate, snippet.getFuel());
        }
    }

}
