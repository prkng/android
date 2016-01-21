package ng.prk.prkngandroid.ui.adapter;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.model.BusinessInterval;
import ng.prk.prkngandroid.model.BusinessIntervalList;
import ng.prk.prkngandroid.model.LotAttrs;
import ng.prk.prkngandroid.model.RestrInterval;
import ng.prk.prkngandroid.util.CalendarUtils;

public class LotAgendaListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "LotAgendaAdapter";
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_FOOTER = 2;

    private final Context context;
    private final int itemLayout;
    private final int today;
    private BusinessIntervalList mDataset;
    private LotAttrs mFooterAttrs;

    public LotAgendaListAdapter(Context context, int itemLayout) {
        this.context = context;
        this.itemLayout = itemLayout;
        this.today = CalendarUtils.getIsoDayOfWeek();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getHeaderSize()) {
            return TYPE_HEADER;
        } else if (position >= getItemCount() - getFooterSize()) {
            return TYPE_FOOTER;
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER: {
                final View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
                return new AgendaHeaderViewHolder(v);
            }
            case TYPE_FOOTER: {
                final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_footer_lot_agenda, parent, false);
                return new AgendaFooterViewHolder(v);
            }
            default: {
                final View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
                return new AgendaViewHolder(v);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int type = getItemViewType(position);
        switch (type) {
            case TYPE_HEADER:
                onBindHeaderViewHolder((AgendaHeaderViewHolder) holder, null);
                break;
            case TYPE_FOOTER:
                onBindFooterViewHolder((AgendaFooterViewHolder) holder, mFooterAttrs);
                break;
            default:
                onBindItemViewHolder((AgendaViewHolder) holder, position);
                break;
        }
    }

    public void onBindItemViewHolder(AgendaViewHolder holder, int pos) {
        final int position = pos - getHeaderSize();
        if (mDataset == null || mDataset.get(position) == null) {
            return;
        }

        final BusinessInterval businessInterval = mDataset.get(position);

        // Get current values
        final int dayOfWeek = businessInterval.getDayOfWeek();
        final String day = CalendarUtils.getDayOfWeekName(context.getResources(), dayOfWeek);
        final boolean isAllDay = businessInterval.isAllDay();
        final String timeStart = CalendarUtils.getTimeFromMillis(context, businessInterval.getStartMillis());
        final String timeEnd = CalendarUtils.getTimeFromMillis(context, businessInterval.getEndMillis());

        holder.vDay.setText(day);

        if (isAllDay) {
            holder.vTime.setText(businessInterval.isClosed() ?
                    R.string.all_day_closed : R.string.all_day);
        } else {
            holder.vTime.setText(
                    context.getResources().getString(R.string.time_start_end,
                            timeStart,
                            timeEnd));
        }
    }

    public void onBindHeaderViewHolder(AgendaHeaderViewHolder holder, Object o) {
    }

    public void onBindFooterViewHolder(AgendaFooterViewHolder holder, LotAttrs attrs) {
        if (attrs == null) {
            holder.itemView.setVisibility(View.GONE);
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
            // Set attributes opacity
            if (attrs.isIndoor()) {
                holder.vIndoor.setVisibility(View.VISIBLE);
                holder.vOutdoor.setVisibility(View.GONE);
            } else {
                holder.vOutdoor.setVisibility(View.VISIBLE);
                holder.vIndoor.setVisibility(View.GONE);
            }
            holder.vCard.setAlpha(getAlphaFromAttr(attrs.isCard()));
            holder.vAccessible.setAlpha(getAlphaFromAttr(attrs.isAccessible()));
            holder.vValet.setAlpha(getAlphaFromAttr(attrs.isValet()));
        }
    }

    private float getAlphaFromAttr(boolean enabled) {
        return enabled ? 1f : Const.UiConfig.LOT_INFO_ATTRS_OPACITY;
    }

    private int getRowBackground(RestrInterval interval) {
        int color;
        if (interval.getDayOfWeek() != today) {
            color = R.color.agenda_item_background;
        } else {
            if (interval.isBefore(CalendarUtils.todayMillis())) {
                color = R.color.agenda_item_background;
            } else {
                color = R.color.agenda_highlight_background;
            }
        }

        return ContextCompat.getColor(context, color);
    }

    @Override
    public int getItemCount() {
        return (mDataset == null) ? 0 : mDataset.size() + getHeaderSize() + getFooterSize();
    }

    private int getHeaderSize() {
//        return (mDataset == null) ? 0 : 1;
        return 0;
    }

    private int getFooterSize() {
        return (mDataset == null) ? 0 : 1;
    }

    public void swapDataset(BusinessIntervalList data) {
        mDataset = data;
        notifyDataSetChanged();
    }

    public void setFooterAttrs(LotAttrs attrs) {
        this.mFooterAttrs = attrs;
    }

    public static class AgendaViewHolder extends RecyclerView.ViewHolder {

        private TextView vDay;
        private TextView vTime;

        public AgendaViewHolder(View itemView) {
            super(itemView);

            this.vDay = (TextView) itemView.findViewById(R.id.day);
            this.vTime = (TextView) itemView.findViewById(R.id.time);
        }
    }

    public static class AgendaHeaderViewHolder extends RecyclerView.ViewHolder {
        public AgendaHeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class AgendaFooterViewHolder extends RecyclerView.ViewHolder {
        private TextView vIndoor;
        private TextView vOutdoor;
        private TextView vCard;
        private TextView vAccessible;
        private TextView vValet;

        public AgendaFooterViewHolder(View itemView) {
            super(itemView);

            this.vIndoor = (TextView) itemView.findViewById(R.id.attr_indoor);
            this.vOutdoor = (TextView) itemView.findViewById(R.id.attr_outdoor);
            this.vCard = (TextView) itemView.findViewById(R.id.attr_card);
            this.vAccessible = (TextView) itemView.findViewById(R.id.attr_accessible);
            this.vValet = (TextView) itemView.findViewById(R.id.attr_valet);
        }
    }
}
