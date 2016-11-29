package eu.gosocialdev.rextagpredictions;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import java.util.Date;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;

import eu.gosocialdev.rextagpredictions.ui.ChartFragment;
import eu.gosocialdev.rextagpredictions.ui.SelectionMenuFragment;
import eu.gosocialdev.rextagpredictions.ui.models.SelectionMenuData;

public class MainActivity extends AppCompatActivity implements SelectionMenuFragment.ISelectionMenu {
    static final String TAG = "MainActivity";

    SelectionMenuFragment selectionMenu;
    ChartFragment chartFragment;
    ImageButton btnCollapse;
    View selectionMenuContainer;

    boolean isOpenMenu = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();

        View chartContainer = findViewById(R.id.chartContainer);
        selectionMenuContainer = findViewById(R.id.selection_menu_container);

        if (savedInstanceState == null) {
            if (selectionMenuContainer!=null) {
                selectionMenu = new SelectionMenuFragment();
                selectionMenu.setArguments(getIntent().getExtras());

                Date now = new Date();
                long today = TimeUnit.MILLISECONDS.toDays(now.getTime());

                fm.beginTransaction().add(R.id.selection_menu_container, selectionMenu, "Menu").commit();

                selectionMenu.setDateRange(today-30, today + 30);
            }

            if (chartContainer!=null) {
                chartFragment = new ChartFragment();
                chartFragment.setArguments(getIntent().getExtras());
                fm.beginTransaction().add(R.id.chartContainer, chartFragment, "Chart").commit();
                chartFragment.setSetting(selectionMenu.getSetting());
            }
            selectionMenuContainer.setVisibility(View.GONE);
        } else {
            selectionMenu = (SelectionMenuFragment)fm.findFragmentByTag("Menu");
            chartFragment = (ChartFragment)fm.findFragmentByTag("Chart");
        }

        selectionMenu.setListener(this);
        btnCollapse = (ImageButton) findViewById(R.id.btnCollapse);
        findViewById(R.id.selection_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int visibility = selectionMenuContainer.getVisibility();
                if (visibility == View.GONE || visibility == View.INVISIBLE) {
                    showSelectionMenu();
                } else {
                    hideSelectionMenu();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);

        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(TAG, "landscape");
        } else if(config.orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.d(TAG, "portrait");
        }

        selectionMenu.updateConfiguration(config);
    }

    private void showSelectionMenu() {
        btnCollapse.setImageResource(android.R.drawable.arrow_up_float);
        selectionMenuContainer.setVisibility(View.VISIBLE);
        selectionMenuContainer.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.show_top_to_bottom));
        selectionMenuContainer.animate();
    }

    private void hideSelectionMenu() {
        btnCollapse.setImageResource(android.R.drawable.arrow_down_float);

        selectionMenuContainer.post(new Runnable() {
            @Override
            public void run() {
                selectionMenuContainer.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.hide_bottom_to_top));
                selectionMenuContainer.animate();
                selectionMenuContainer.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onReset(SelectionMenuData data) {
        hideSelectionMenu();
        chartFragment.setData(data);
    }

    @Override
    public void onView(SelectionMenuData data) {
        hideSelectionMenu();
        chartFragment.setData(data);
    }
}
