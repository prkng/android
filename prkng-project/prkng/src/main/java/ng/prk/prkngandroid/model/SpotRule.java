package ng.prk.prkngandroid.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import ng.prk.prkngandroid.Const;

public class SpotRule {
    private SpotRuleAgenda agenda;
    private String code;
    private String description;
    @SerializedName(Const.ApiArgs.PAID_HOURLY_RATE)
    private float paidHourlyRate;
    @SerializedName(Const.ApiArgs.PERMIT_NO)
    private String permitNo;
    @SerializedName(Const.ApiArgs.RESTRICT_TYPES)
    private List<String> restrictTypes;
    @SerializedName(Const.ApiArgs.SEASON_END)
    private String seasonEnd;
    @SerializedName(Const.ApiArgs.SEASON_START)
    private String seasonStart;
    @SerializedName(Const.ApiArgs.SPECIAL_DAY)
    private String specialDays;
    // NOTE: needs to be wrapper type (Integer) to handle NULL value
    @SerializedName(Const.ApiArgs.TIME_MAX_PARKING)
    private Integer timeMaxParking;

    public SpotRuleAgenda getAgenda() {
        return agenda;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public float getPaidHourlyRate() {
        return paidHourlyRate;
    }

    public String getPermitNo() {
        return permitNo;
    }

    public List<String> getRestrictTypes() {
        return restrictTypes;
    }

    public String getSeasonEnd() {
        return seasonEnd;
    }

    public String getSeasonStart() {
        return seasonStart;
    }

    public String getSpecialDays() {
        return specialDays;
    }

    public int getTimeMaxParking() {
        return timeMaxParking == null ? Const.UNKNOWN_VALUE : timeMaxParking;
    }

    public boolean isTypePaid() {
        return (restrictTypes != null) &&
                restrictTypes.contains(Const.ApiValues.SPOT_TYPE_PAID)
                && !isSpecialRestrType();
    }

    public boolean isTypePermit() {
        return (restrictTypes != null) &&
                restrictTypes.contains(Const.ApiValues.SPOT_TYPE_PERMIT)
                && !isSpecialRestrType();
    }

    public boolean isTypeTimeMax() {
        return (timeMaxParking != null) && !isSpecialRestrType();
    }

    public boolean isTypeTimeMaxPaid() {
        return isTypePaid() && isTypeTimeMax();
    }

    private boolean isSpecialRestrType() {
        if (restrictTypes == null || restrictTypes.isEmpty()) {
            return false;
        }

        final List<String> regularTypes = new ArrayList<>();
        regularTypes.add(Const.ApiValues.SPOT_TYPE_PAID);
        regularTypes.add(Const.ApiValues.SPOT_TYPE_PERMIT);

        for (String type : restrictTypes) {
            if (!regularTypes.contains(type)) {
                return true;
            }
        }

        return false;
    }
}
