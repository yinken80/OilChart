package eu.gosocialdev.rextagpredictions.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import eu.gosocialdev.rextagpredictions.R;
import eu.gosocialdev.rextagpredictions.ui.items.ForecasterListItemHolder;
import eu.gosocialdev.rextagpredictions.ui.models.ForecasterItemModel;

/**
 * Created by Administrator on 11/17/2016.
 */

public class ForecasterListAdapter extends RecyclerView.Adapter<ForecasterListItemHolder> implements ForecasterListItemHolder.OnCheckedListener {
    ArrayList<ForecasterItemModel> forecasters;

    public ForecasterListAdapter(ArrayList<ForecasterItemModel> data) {
        this.forecasters = data;
    }

    @Override
    public ForecasterListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forecaster_list, parent, false);
        ForecasterListItemHolder vh = new ForecasterListItemHolder(view, this);
        return vh;
    }

    @Override
    public void onBindViewHolder(ForecasterListItemHolder holder, int position) {
        if (holder != null) {
            holder.setItemModel(forecasters.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        return forecasters.size();
    }

    @Override
    public void onCheckedChanged(int index, boolean bChecked) {
        forecasters.get(index).setChecked(bChecked);
    }

    public ArrayList<ForecasterItemModel> selectedItems() {
        ArrayList<ForecasterItemModel> items = new ArrayList<ForecasterItemModel>();
        for (int  i = 0; i < forecasters.size(); i++) {
            if (forecasters.get(i).isChecked()) {
                items.add(forecasters.get(i));
            }
        }
        return items;
    }
}
