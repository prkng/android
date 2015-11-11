package ng.prk.prkngandroid.util;

import ng.prk.prkngandroid.Const;

public class MapUtils {

    public static double getMinZoomPerType(int type) {
        switch (type) {
            case Const.MapSections.OFF_STREET:
                return Const.UiConfig.LOTS_MIN_ZOOM;
            case Const.MapSections.CARSHARE_SPOTS:
            case Const.MapSections.ON_STREET:
                return Const.UiConfig.SPOTS_MIN_ZOOM;
            case Const.MapSections.CARSHARE_VEHICLES:
                return Const.UiConfig.CARSHARE_VEHICLES_MIN_ZOOM;
        }

        return Const.UiConfig.DEFAULT_ZOOM;
    }

    public static boolean isMinZoom(double zoom, int type) {
        final double minZoom = getMinZoomPerType(type);

        return Double.compare(minZoom, zoom) <= 0;
    }
}
