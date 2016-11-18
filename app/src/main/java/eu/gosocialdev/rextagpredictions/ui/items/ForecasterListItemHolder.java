package eu.gosocialdev.rextagpredictions.ui.items;

import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;

import eu.gosocialdev.rextagpredictions.R;
import eu.gosocialdev.rextagpredictions.ui.models.ForecasterItemModel;

/**
 * Created by Administrator on 11/17/2016.
 */

public class ForecasterListItemHolder extends RecyclerView.ViewHolder {
    private AppCompatCheckBox checkbox;
    private int mIndex;
    private ForecasterItemModel mItemData;

    public interface OnCheckedListener {
        public void onCheckedChanged(int index, boolean bChecked);
    }

    public OnCheckedListener mListener;

    public ForecasterListItemHolder(View itemView, OnCheckedListener listener) {
        super(itemView);
        this.mListener = listener;
        checkbox = (AppCompatCheckBox)itemView.findViewById(R.id.checkboxListItem);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mListener != null) {
                    mListener.onCheckedChanged(mIndex, b);
                }
            }
        });
    }

    public void setItemModel(ForecasterItemModel item, int index) {
        this.mItemData = item;
        this.mIndex = index;
        checkbox.setText(item.getText());
        checkbox.setChecked(item.isChecked());
    }
}
