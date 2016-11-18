package eu.gosocialdev.rextagpredictions.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

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
import eu.gosocialdev.rextagpredictions.ui.models.SelectionMenuData;
import eu.gosocialdev.rextagpredictions.ui.views.OilChartMakerView;

public class ChartFragment extends BaseFragment implements OnChartValueSelectedListener, OnChartGestureListener {
    private LineChart mChart;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_chart, container, false);

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

        mChart.setPinchZoom(true);

        // x-axis
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setTypeface(mTfLight);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setCenterAxisLabels(true);
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

        // x-axis limit line
        LimitLine todayLine = new LimitLine(100f, "Now");
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

        return view;
    }

    public void setData(SelectionMenuData setting, float range) {
        ArrayList<Entry> values = new ArrayList<Entry>();
        ArrayList<Entry> values2 = new ArrayList<Entry>();

        float from = setting.startDate;

        // count = days
        float to = setting.endDate;

        // increment by 1 hour
        for (float x = from; x < to; x++) {

            float y = getRandom(range, 50);
            values.add(new Entry(x, y)); // add one entry per hour

            y = getRandom(range, 50);
            values2.add(new Entry(x, y));
        }

        LineData data;

        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
            data = mChart.getData();
            ((LineDataSet)data.getDataSetByIndex(0)).setValues(values);
            ((LineDataSet)data.getDataSetByIndex(1)).setValues(values2);

            data.notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.invalidate();
        } else {
            // create a dataset and give it a type
            LineDataSet set1 = new LineDataSet(values, "Hart Energy Forecaster Index");
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(ColorTemplate.getHoloBlue());
            set1.setValueTextColor(ColorTemplate.getHoloBlue());
            set1.setLineWidth(1.5f);
            set1.setDrawCircles(true);
            set1.setCircleColor(ColorTemplate.getHoloBlue());
            set1.setDrawValues(false);
            set1.setFillAlpha(65);
            set1.setFillColor(ColorTemplate.getHoloBlue());
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setDrawCircleHole(true);

            LineDataSet set2 = new LineDataSet(values2, "ABN AMRO");
            set2.setAxisDependency(YAxis.AxisDependency.LEFT);
            set2.setColor(Color.RED);
            set2.setValueTextColor(Color.RED);
            set2.setLineWidth(1.5f);
            set2.setDrawCircles(true);
            set2.setCircleColor(Color.RED);
            set2.setDrawValues(false);
            set2.setFillAlpha(65);
            set2.setFillColor(Color.RED);
            set2.setHighLightColor(Color.rgb(244, 117, 117));
            set2.setDrawCircleHole(true);

            data = new LineData(set1, set2);
            data.setValueTextColor(Color.WHITE);
            data.setValueTextSize(9f);
            // set data
            mChart.setData(data);
        }

        mChart.animate();
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
}
