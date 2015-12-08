package ng.prk.prkngandroid.ui.adapter;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.model.RestrInterval;
import ng.prk.prkngandroid.model.RestrIntervalsList;
import ng.prk.prkngandroid.util.CalendarUtils;

public class LotAgendaListAdapter extends RecyclerView.Adapter<LotAgendaListAdapter.AgendaViewHolder> {
    private static final String TAG = "LotAgendaAdapter";

    private final Context context;
    private final int itemLayout;
    private final int today;
    private RestrIntervalsList mDataset;

    public LotAgendaListAdapter(Context context, int itemLayout) {
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

        final RestrInterval dailyPeriod = mDataset.get(position);

        // Get current values
        final int dayOfWeek = dailyPeriod.getDayOfWeek();
        final String day = CalendarUtils.getDayOfWeekName(context.getResources(), dayOfWeek);
        final boolean isAllDay = dailyPeriod.isAllDay();
        final String timeStart = CalendarUtils.getTimeFromMillis(context, dailyPeriod.getStartMillis());
        final String timeEnd = CalendarUtils.getTimeFromMillis(context, dailyPeriod.getEndMillis());

        holder.vDay.setText(day);

        if (isAllDay) {
            holder.vTime.setText(R.string.all_day);
        } else {
            holder.vTime.setText(
                    context.getResources().getString(R.string.time_start_end,
                            timeStart,
                            timeEnd));
        }
    }

    private int getRowBackground(RestrInterval interval) {
        int color;
        if (interval.getDayOfWeek() != today) {
            color = R.color.agenda_item_background;
        } else {
            if (interval.isBefore(CalendarUtils.todayMillis())) {
                color = R.color.agenda_item_background;
            } else {
                color = R.color.agenda_today_item_background;
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
        private TextView vTime;

        public AgendaViewHolder(View itemView) {
            super(itemView);

            this.vDay = (TextView) itemView.findViewById(R.id.day);
            this.vTime = (TextView) itemView.findViewById(R.id.time);
        }
    }

}
