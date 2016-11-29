package eu.gosocialdev.rextagpredictions.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Selection;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import eu.gosocialdev.rextagpredictions.R;
import eu.gosocialdev.rextagpredictions.ui.adapters.ForecasterListAdapter;
import eu.gosocialdev.rextagpredictions.ui.base.BaseFragment;
import eu.gosocialdev.rextagpredictions.ui.models.ForecasterItemModel;
import eu.gosocialdev.rextagpredictions.ui.models.SelectionMenuData;
import eu.gosocialdev.rextagpredictions.ui.views.CustomSpinner;
import larpon.android.view.RangeSeekBar;

/**
 * Created by Administrator on 11/17/2016.
 */

public class SelectionMenuFragment extends BaseFragment implements View.OnClickListener, RangeSeekBar.RangeSeekBarListener {
    String[] forecasters = new String[]{"Hart Energy Forecaster Index", "ABN AMRO",
            "Bank of America", "Barclays", "Bernstein Research", "Bloomberg", "BNP Paribas",
            "Capital Economics", "CitiGroup"};

    String[] oilTypes = new String[]{"Global Oil Price Avg", "West Texas Inter.(WTI)", "Brent"};

    //UI component values
    RecyclerView    mForecasterListView;
    CustomSpinner   mOilTypeSpinner;
    View            btnReset, btnView;
    RangeSeekBar    mDateRanger;
    TextView        mDateRangeText;
    RelativeLayout  mRLayoutForecasters;

    // property values
    ArrayList<ForecasterItemModel> mForecasters;
    long mMinDate, mMaxDate;

    // value containing overall settings
    SelectionMenuData setting = new SelectionMenuData();

    // litener
    ISelectionMenu mListener;

    public SelectionMenuFragment() {
        mForecasters = new ArrayList<ForecasterItemModel>();
        //TEST DATA
        for (int i = 0; i < forecasters.length; i++) {
            ForecasterItemModel item = new ForecasterItemModel();
            item.setText(forecasters[i]);
            if (i == 0) {
                item.setChecked(true);
            }
            mForecasters.add(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selection_menu, container, false);
        mRLayoutForecasters = (RelativeLayout) view.findViewById(R.id.rlayout_forecasters);

        mOilTypeSpinner = (CustomSpinner) view.findViewById(R.id.spinOilType);
        mForecasterListView = (RecyclerView) view.findViewById(R.id.forecaster_list);

        mDateRanger = (RangeSeekBar) view.findViewById(R.id.dateRangeSeekBar);
        mDateRangeText = (TextView) view.findViewById(R.id.textDateRange);

        btnReset =  view.findViewById(R.id.btnReset);
        btnView = view.findViewById(R.id.btnView);
        btnReset.setOnClickListener(this);
        btnView.setOnClickListener(this);

        //initialize oil types spinner
        ArrayAdapter<String> oilTypesAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_spinner_selected, oilTypes);
        oilTypesAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        mOilTypeSpinner.setAdapter(oilTypesAdapter);
        mOilTypeSpinner.setSpinnerEventsListener(new CustomSpinner.OnSpinnerEventsListener() {
            @Override
            public void onSpinnerOpened() {
                mOilTypeSpinner.setSelected(true);
            }

            @Override
            public void onSpinnerClosed() {
                mOilTypeSpinner.setSelected(false);
            }
        });

        //initialize forecaster list recyclerview
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mForecasterListView.setLayoutManager(layoutManager);

        ForecasterListAdapter adapter = new ForecasterListAdapter(mForecasters);
        mForecasterListView.setAdapter(adapter);

        //initialize date ranger
        mDateRanger.setListener(this);
        mDateRanger.setLimitThumbRange(false);

        updateViews();

        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id) {
            case R.id.btnReset:
                reset();
                break;
            case R.id.btnView:
                view();
                break;
        }
    }

    @Override
    public void onCreate(RangeSeekBar rangeSeekBar, int index, float value) {
        updateViews();
    }

    @Override
    public void onSeek(RangeSeekBar rangeSeekBar, int index, float value) {
        if (index == 0) {
            setting.startDate = (long)value;
        } else {
            setting.endDate = (long)value;
        }
        updateDateText();
    }

    @Override
    public void onSeekStart(RangeSeekBar rangeSeekBar, int index, float value) {

    }

    @Override
    public void onSeekStop(RangeSeekBar rangeSeekBar, int index, float value) {
        if (index == 0) {
            setting.startDate = (long)value;
        } else {
            setting.endDate = (long)value;
        }
        updateDateText();
    }

    @Override
    public void updateConfiguration(Configuration config) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)mRLayoutForecasters.getLayoutParams();
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            params.height = 0;
            params.weight = 1;
            mRLayoutForecasters.setLayoutParams(params);
        } else if (config.orientation == Configuration.ORIENTATION_LANDSCAPE){
            params.height = 200;
            params.weight = 0;
            mRLayoutForecasters.setLayoutParams(params);
        }
    }

    Handler mHandler = new Handler();

    /**
     * Set the listener that listens settings' changes
     */
    public void setListener(ISelectionMenu listener) {
        this.mListener = listener;
    }
    /**
     * Set the maximum and minimum value of the date range bar
     */
    public void setDateRange(long minDate, long maxDate) {
        this.mMinDate = minDate;
        this.mMaxDate = maxDate;

        setting.oilType = 0;
        setting.forecasters = mForecasters;
        setting.startDate = minDate;
        setting.endDate = maxDate;

        updateViews();
    }

    private void updateViews() {
        if (mDateRanger == null)
            return;

        mDateRanger.setScaleRangeMin(mMinDate);
        mDateRanger.setScaleRangeMax(mMaxDate);
        RangeSeekBar.Thumb left = mDateRanger.getThumbAt(0);
        RangeSeekBar.Thumb right = mDateRanger.getThumbAt(1);
        left.setValue(this.setting.startDate);
        right.setValue(this.setting.endDate);

        updateDateText();
    }

    /**
     * Return setting value
     */
    public SelectionMenuData getSetting() {
        setting.forecasters = selectedItems();
        return setting;
    }

    private ArrayList<ForecasterItemModel> selectedItems() {
        if (mForecasters == null)
            return null;
        ArrayList<ForecasterItemModel> items = new ArrayList<ForecasterItemModel>();
        for (int  i = 0; i < mForecasters.size(); i++) {
            if (mForecasters.get(i).isChecked()) {
                items.add(mForecasters.get(i));
            }
        }
        return items;
    }

    /**
     * Set setting value
     */
    public void setSetting(SelectionMenuData setting) {
        this.setting = setting;
    }
    /**
     * Update the label of date range textview
     */
    private void updateDateText() {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy");
        Date date = new Date();
        date.setTime(TimeUnit.DAYS.toMillis(setting.startDate));
        String startDateStr = formatter.format(date);

        date.setTime(TimeUnit.DAYS.toMillis(setting.endDate));
        String endDateStr = formatter.format(date);

        mDateRangeText.setText(startDateStr + " - " + endDateStr);
    }
    /**
     * Reset all settings
     */
    private void reset() {
        setting.oilType = 0;
        setting.startDate = mMinDate;
        setting.endDate = mMaxDate;
        mDateRanger.getThumbAt(0).setValue(mMinDate);
        mDateRanger.getThumbAt(1).setValue(mMaxDate);
        if (mListener != null) {
            mListener.onReset(setting);
        }

        updateDateText();
    }

    private void view() {
        setting.oilType = mOilTypeSpinner.getSelectedItemPosition();
        setting.forecasters = selectedItems();
        if (mListener != null) {
            mListener.onView(setting);
        }
    }

    public interface ISelectionMenu {
        public void onReset(SelectionMenuData data);
        public void onView(SelectionMenuData data);
    }
}
