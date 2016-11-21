package eu.gosocialdev.rextagpredictions.ui.views;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import eu.gosocialdev.rextagpredictions.R;

/**
 * Created by Administrator on 11/18/2016.
 */

public class OilChartMakerView extends MarkerView {
    TextView txtDate, txtPrice;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    DecimalFormat decimalFormat = new DecimalFormat("###.0");
    public OilChartMakerView(Context context) {
        super(context, R.layout.view_chart_marker);

        txtDate = (TextView) findViewById(R.id.textDate);
        txtPrice = (TextView) findViewById(R.id.textPrice);

    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        long timeMillis = TimeUnit.DAYS.toMillis((long)e.getX());
        Date date = new Date(timeMillis);
        txtDate.setText(dateFormat.format(date));
        txtPrice.setText('$' + decimalFormat.format(e.getY()));

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(0, -getHeight()/2);
    }
}
