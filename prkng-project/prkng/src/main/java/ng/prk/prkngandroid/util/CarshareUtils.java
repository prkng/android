package ng.prk.prkngandroid.util;

import android.content.Context;
import android.content.res.Resources;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;

public class CarshareUtils implements
        Const.CarshareCompanies {

    public static String getCompanyName(Context context, String company) {
        final Resources res = context.getResources();

        if (CAR2GO.equals(company)) {
            return res.getString(R.string.carshare_company_car2go);
        } else if (AUTOMOBILE.equals(company)) {
            return res.getString(R.string.carshare_company_automobile);
        } else if (COMMUNAUTO.equals(company)) {
            return res.getString(R.string.carshare_company_communauto);
        } else if (ZIPCAR.equals(company)) {
            return res.getString(R.string.carshare_company_zipcar);
        }

        return null;
    }

    public static int getCompanyMapIcon(String company) {
        if (CAR2GO.equals(company)) {
            return R.drawable.ic_maps_carshare_car2go;
        } else if (AUTOMOBILE.equals(company)) {
            return R.drawable.ic_maps_carshare_automobile;
        } else if (COMMUNAUTO.equals(company)) {
            return R.drawable.ic_maps_carshare_communauto;
        } else if (ZIPCAR.equals(company)) {
            return R.drawable.ic_maps_carshare_zipcar;
        }

        return 0;
    }

//    public static int getCompanySettingsIcon(String company) {
//        if (CAR2GO.equals(company)) {
//            return R.drawable.ic_prefs_car2go;
//        } else if (AUTOMOBILE.equals(company)) {
//            return R.drawable.ic_prefs_communauto;
//        } else if (COMMUNAUTO.equals(company)) {
//            return R.drawable.ic_prefs_communauto;
//        } else if (ZIPCAR.equals(company)) {
//            return R.drawable.ic_prefs_zipcar;
//        }
//
//        return 0;
//    }

}
