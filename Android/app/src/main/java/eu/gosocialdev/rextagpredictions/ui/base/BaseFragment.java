package eu.gosocialdev.rextagpredictions.ui.base;

import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by Administrator on 11/17/2016.
 */

public abstract class BaseFragment extends Fragment {
    protected String[] mMonths = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };

    protected Typeface mTfRegular;
    protected Typeface mTfLight;

    public BaseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTfRegular = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-Light.ttf");
    }

    protected float getRandom(float range, float startsfrom) {
        return (float) (Math.random() * range) + startsfrom;
    }

    abstract public void updateConfiguration(Configuration config);
}
