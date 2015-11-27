package ng.prk.prkngandroid.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ng.prk.prkngandroid.R;

public class AgendaListFragment extends Fragment {
    private RecyclerView vRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.fragment_agenda_list, container, false);

        vRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        vRecyclerView.setLayoutManager(layoutManager);
        vRecyclerView.setAdapter(null);

        return view;
    }
}
