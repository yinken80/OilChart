package eu.gosocialdev.rextagpredictions.ui;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import eu.gosocialdev.rextagpredictions.R;
import eu.gosocialdev.rextagpredictions.ui.base.BaseFragment;
import eu.gosocialdev.rextagpredictions.ui.models.ForecasterItemModel;
import eu.gosocialdev.rextagpredictions.ui.models.SelectionMenuData;
import eu.gosocialdev.rextagpredictions.ui.views.OilChartMakerView;

public class ChartFragment extends BaseFragment implements OnChartValueSelectedListener, OnChartGestureListener, View.OnClickListener {
    private LineChart mChart;
    private FloatingActionButton mBtnDownload;

    private String[] colors = {"#f44336", "#e91e63", "#9c27b0", "#673ab7", "#3f51b5", "#2196f3", "#03a9f4", "#00bcd4",
                                "#009688", "#4caf50", "#8bc34a", "#cddc39", "#ffeb3b", "#ffc107", "#ff9800", "#ff5722",
                                "#795548", "#9e9e9e", "#607d8b"};
    long mToday;
    SelectionMenuData setting;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_chart, container, false);

        mBtnDownload = (FloatingActionButton) view.findViewById(R.id.btnDownload);
        mBtnDownload.setOnClickListener(this);

        mChart = (LineChart) view.findViewById(R.id.chart);
        //enable touch gestures
        mChart.setTouchEnabled(true);
        mChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleXEnabled(true);
        mChart.setScaleYEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        mChart.setPinchZoom(false);

        // x-axis
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setTypeface(mTfLight);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setCenterAxisLabels(false);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            private SimpleDateFormat mDayFormat = new SimpleDateFormat("MMM dd");
            private SimpleDateFormat mYearFormat = new SimpleDateFormat("yyyy");
            private SimpleDateFormat mMonthFormat = new SimpleDateFormat("MMM");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                long millis = TimeUnit.DAYS.toMillis((long)value);
                Date date = new Date(millis);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                String label = "";
                if (month == Calendar.JANUARY && day == 1) {
                    label = mYearFormat.format(date);
                } else if (day == 1) {
                    label = mMonthFormat.format(date);
                } else {
                    label = mDayFormat.format(date);
                }

                return label;
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });
        xAxis.setLabelRotationAngle(-45);
        xAxis.setAvoidFirstLastClipping(true);

        // x-axis limit line
        Date now = new Date();
        mToday = TimeUnit.MILLISECONDS.toDays(now.getTime());

        LimitLine todayLine = new LimitLine(mToday, "Now");
        todayLine.setLineWidth(4f);
        todayLine.setLineColor(ColorTemplate.rgb("#e88b8b"));
        todayLine.enableDashedLine(10f, 10f, 0f);
        todayLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        todayLine.setTextSize(10f);

        xAxis.addLimitLine(todayLine);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);
        //mChart.setViewPortOffsets(0f, 0f, 0f, 0f);

        mChart.getAxisRight().setEnabled(false);
        mChart.getDescription().setEnabled(false);

        OilChartMakerView mv = new OilChartMakerView(getContext());
        mv.setChartView(mChart);
        mChart.setMarker(mv);

        // add data
        //setData(365, 30);
        //mChart.animateX(500);

        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);

        setData(this.setting);

        return view;
    }

    public void setData(SelectionMenuData setting) {
        if (mChart == null || this.setting == null)
            return;
        float from = setting.startDate;
        float range = 80;
        float to = setting.endDate;

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

        ArrayList<ForecasterItemModel> forecasters = setting.forecasters;
        int colorCount = colors.length;

        ArrayList<Entry> values = getRandomValues(from, mToday, 40);
        int color = ColorTemplate.getHoloBlue();

        LineDataSet set = new LineDataSet(values, "Actual oil price");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(color);
        set.setValueTextColor(color);
        set.setLineWidth(2f);
        set.setDrawCircles(false);
        set.setCircleColor(color);
        set.setDrawValues(false);
        set.setFillAlpha(65);
        set.setFillColor(color);
        set.setHighLightColor(color);
        set.setDrawCircleHole(true);

        dataSets.add(set);

        for (int i = 0; i < forecasters.size(); i++) {
            values = getRandomValues(from, to, 50);

            color = ColorTemplate.rgb(colors[i%colorCount]);
            set = new LineDataSet(values, forecasters.get(i).getText());
            set.setAxisDependency(YAxis.AxisDependency.LEFT);
            set.setColor(color);
            set.setValueTextColor(color);
            set.setLineWidth(2f);
            set.setDrawCircles(false);
            set.setCircleColor(color);
            set.setDrawValues(false);
            set.setFillAlpha(65);
            set.setFillColor(color);
            set.setHighLightColor(color);
            set.setDrawCircleHole(true);

            dataSets.add(set);
        }

        LineData data;
        mChart.clear();
        data = new LineData(dataSets);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);
        // set data
        mChart.setData(data);
        mChart.animate();
    }

    private ArrayList<Entry> getRandomValues(float from, float to, float range) {
        ArrayList<Entry> values = new ArrayList<Entry>();
        // increment by 1 day
        for (float x = from; x < to; x++) {
            float y = getRandom(range, 50);
            values.add(new Entry(x, y)); // add one entry per hour
        }

        return values;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            mChart.highlightValues(null);
    }

    @Override
    public void onChartGestureMove(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onClick(View view) {
        int btnId = view.getId();
        switch (btnId) {
            case R.id.btnDownload:
            {
                String filename = "OilChart_" + new Date().toString();
                boolean bSuccess = mChart.saveToGallery(filename, 100);
                if (bSuccess) {
                    Toast.makeText(getContext(), "File saved to the gallery as " + filename +".jpg", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    @Override
    public void updateConfiguration(Configuration config) {

    }

    public void setSetting(SelectionMenuData setting) {
        this.setting = setting;

        setData(setting);
    }
}
