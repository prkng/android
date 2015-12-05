package ng.prk.prkngandroid.ui.adapter;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ng.prk.prkngandroid.Const;
import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.model.RestrInterval;
import ng.prk.prkngandroid.model.RestrIntervalsList;
import ng.prk.prkngandroid.util.CalendarUtils;

public class AgendaListAdapter extends RecyclerView.Adapter<AgendaListAdapter.AgendaViewHolder> {
    private static final String TAG = "AgendaListAdapter";

    private final Context context;
    private final int itemLayout;
    private final int today;
    private RestrIntervalsList mDataset;

    public AgendaListAdapter(Context context, int itemLayout) {
        this.context = context;
        this.itemLayout = itemLayout;
        this.today = CalendarUtils.getIsoDayOfWeek();
    }

    @Override
    public AgendaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new AgendaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AgendaViewHolder holder, int position) {
        if (mDataset == null || mDataset.get(position) == null) {
            return;
        }

        final RestrInterval parkingRestrPeriod = mDataset.get(position);

        // Get current values
        final int dayOfWeek = parkingRestrPeriod.getDayOfWeek();
        final String day = CalendarUtils.getDayOfWeekName(context.getResources(), dayOfWeek);
        final boolean isAllDay = parkingRestrPeriod.isAllDay();
        final String timeStart = CalendarUtils.getTimeFromMillis(context, parkingRestrPeriod.getStartMillis());
        final String timeEnd = CalendarUtils.getTimeFromMillis(context, parkingRestrPeriod.getEndMillis());
        final int timeMax = parkingRestrPeriod.getTimeMaxMinutes();
        final int typeIcon = getRestrTypeIcon(parkingRestrPeriod.getType());

        if (timeMax != Const.UNKNOWN_VALUE) {
            // TODO: replace with ninepatch
            holder.vDay.setText(day + " (" + timeMax + ")");
        } else {
            holder.vDay.setText(day);
        }

        holder.vRestrType.setImageResource(typeIcon);
        if (isAllDay) {
            holder.vTimeStart.setText(R.string.all_day);
            holder.vTimeEnd.setVisibility(View.GONE);
        } else {
            holder.vTimeStart.setText(timeStart);
            holder.vTimeEnd.setText(timeEnd);
            holder.vTimeEnd.setVisibility(View.VISIBLE);
        }

        holder.itemView.setBackgroundColor(getRowBackground(parkingRestrPeriod));
    }

    private int getRestrTypeIcon(int type) {
        switch (type) {
            case Const.ParkingRestrType.ALL_TIMES:
                return R.drawable.ic_restr_type_all_times;
            case Const.ParkingRestrType.PAID:
                return R.drawable.ic_restr_type_paid;
            case Const.ParkingRestrType.TIME_MAX:
                return R.drawable.ic_restr_type_time_max;
            case Const.ParkingRestrType.TIME_MAX_PAID:
                return R.drawable.ic_restr_type_time_max_paid;
            case Const.ParkingRestrType.NONE:
                return R.drawable.ic_restr_type_none;
        }

        return 0;
    }

    private int getRowBackground(RestrInterval interval) {
        int color;
        if (interval.getDayOfWeek() != today) {
            color = R.color.agenda_item_background;
        } else {
            if (interval.isBefore(CalendarUtils.todayMillis())) {
                color = R.color.agenda_item_background;
            } else {
                color = R.color.agenda_today_item_background ;
            }
        }

        return ContextCompat.getColor(context, color);
    }

    @Override
    public int getItemCount() {
        return (mDataset == null) ? 0 : mDataset.size();
    }

    public void swapDataset(RestrIntervalsList data) {
        mDataset = data;
        notifyDataSetChanged();
    }

    public static class AgendaViewHolder extends RecyclerView.ViewHolder {

        private TextView vDay;
        private ImageView vRestrType;
        private TextView vTimeStart;
        private TextView vTimeEnd;

        public AgendaViewHolder(View itemView) {
            super(itemView);

            this.vDay = (TextView) itemView.findViewById(R.id.day);
            this.vRestrType = (ImageView) itemView.findViewById(R.id.restr_type);
            this.vTimeStart = (TextView) itemView.findViewById(R.id.time_start);
            this.vTimeEnd = (TextView) itemView.findViewById(R.id.time_end);
        }
    }

}
