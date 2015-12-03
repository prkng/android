package ng.prk.prkngandroid.model;

import java.util.ArrayList;
import java.util.List;

import ng.prk.prkngandroid.Const;

public class SpotRule {
    private String address;
    private SpotRuleAgenda agenda;
    private String code;
    private String description;
    private String permit_no;
    private List<String> restrict_types;
    private String season_end;
    private String season_start;
    private String special_days;
    // NOTE: needs to be wrapper type (Integer) to handle NULL value
    private Integer time_max_parking;

    public String getAddress() {
        return address;
    }

    public SpotRuleAgenda getAgenda() {
        return agenda;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getPermitNo() {
        return permit_no;
    }

    public List<String> getRestrictTypes() {
        return restrict_types;
    }

    public String getSeasonEnd() {
        return season_end;
    }

    public String getSeasonStart() {
        return season_start;
    }

    public String getSpecialDays() {
        return special_days;
    }

    public int getTimeMaxParking() {
        return time_max_parking == null ? Const.UNKNOWN_VALUE : time_max_parking;
    }

    public boolean isTypePaid() {
        return (restrict_types != null) &&
                restrict_types.contains(Const.ApiValues.SPOT_TYPE_PAID)
                && !isSpecialRestrType();
    }

    public boolean isTypePermit() {
        return (restrict_types != null) &&
                restrict_types.contains(Const.ApiValues.SPOT_TYPE_PERMIT)
                && !isSpecialRestrType();
    }

    public boolean isTypeTimeMax() {
        return (time_max_parking != null) && !isSpecialRestrType();
    }

    public boolean isTypeTimeMaxPaid() {
        return isTypePaid() && isTypeTimeMax();
    }

    private boolean isSpecialRestrType() {
        if (restrict_types == null || restrict_types.isEmpty()) {
            return false;
        }

        final List<String> regularTypes = new ArrayList<>();
        regularTypes.add(Const.ApiValues.SPOT_TYPE_PAID);
        regularTypes.add(Const.ApiValues.SPOT_TYPE_PERMIT);

        for (String type : restrict_types) {
            if (!regularTypes.contains(type)) {
                return true;
            }
        }

        return false;
    }
}
