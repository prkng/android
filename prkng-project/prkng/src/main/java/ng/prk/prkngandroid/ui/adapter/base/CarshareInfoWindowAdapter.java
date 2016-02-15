package ng.prk.prkngandroid.ui.adapter.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.views.MapView;

import ng.prk.prkngandroid.R;
import ng.prk.prkngandroid.model.ui.JsonSnippet;

public abstract class CarshareInfoWindowAdapter implements MapView.InfoWindowAdapter {

    private View view;
    private InfoWindowViewHolder viewHolder;

    public CarshareInfoWindowAdapter(LayoutInflater inflater) {
        this.view = inflater.inflate(R.layout.map_info_window_carshare_vehicle, null);
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        if (viewHolder == null) {
            this.viewHolder = new InfoWindowViewHolder(view);
        }

        final JsonSnippet snippet = JsonSnippet.fromJson(marker.getSnippet());

        viewHolder.title.setText(marker.getTitle());
        viewHolder.snippet.setText(getSubtitle(snippet));

        final String figure = getFigure(snippet);
        viewHolder.figure.setVisibility(TextUtils.isEmpty(figure) ? View.GONE : View.VISIBLE);
        viewHolder.figure.setText(figure);

        return view;
    }

    private class InfoWindowViewHolder {
        private TextView title;
        private TextView snippet;
        private TextView figure;

        public InfoWindowViewHolder(View view) {
            this.title = (TextView) view.findViewById(R.id.title);
            this.snippet = (TextView) view.findViewById(R.id.snippet);
            this.figure = (TextView) view.findViewById(R.id.figure);
        }
    }

    protected abstract String getSubtitle(JsonSnippet snippet);

    protected abstract String getFigure(JsonSnippet snippet);
}
