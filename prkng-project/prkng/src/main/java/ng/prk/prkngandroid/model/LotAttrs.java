package ng.prk.prkngandroid.model;

import com.google.gson.annotations.SerializedName;

import ng.prk.prkngandroid.Const;

public class LotAttrs {
    private boolean card;
    @SerializedName(Const.ApiArgs.ACCESSIBLE)
    private boolean accessible;
    private boolean indoor;
    private boolean valet;

    public boolean isCard() {
        return card;
    }

    public boolean isAccessible() {
        return accessible;
    }

    public boolean isIndoor() {
        return indoor;
    }

    public boolean isValet() {
        return valet;
    }

    @Override
    public String toString() {
        return "LotAttrs{" +
                "card=" + card +
                ", accessible=" + accessible +
                ", indoor=" + indoor +
                ", valet=" + valet +
                '}';
    }
}
