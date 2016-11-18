package eu.gosocialdev.rextagpredictions;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import eu.gosocialdev.rextagpredictions.ui.ChartFragment;
import eu.gosocialdev.rextagpredictions.ui.SelectionMenuFragment;
import eu.gosocialdev.rextagpredictions.ui.models.SelectionMenuData;

public class MainActivity extends AppCompatActivity implements SelectionMenuFragment.ISelectionMenu {
    SelectionMenuFragment selectionMenu;
    ChartFragment chartFragment;
    ImageButton btnCollapse;
    View selectionMenuContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectionMenu = (SelectionMenuFragment) getSupportFragmentManager().findFragmentById(R.id.selection_menu_container);
        chartFragment = (ChartFragment) getSupportFragmentManager().findFragmentById(R.id.chartContainer);

        Date now = new Date();
        long today = TimeUnit.MILLISECONDS.toDays(now.getTime());
        selectionMenu.setListener(this);
        selectionMenu.setDateRange(today, today + 365);

        chartFragment.setData(selectionMenu.getSetting(), 80);

        btnCollapse = (ImageButton) findViewById(R.id.btnCollapse);
        selectionMenuContainer = findViewById(R.id.selection_menu_container);
        selectionMenuContainer.setVisibility(View.GONE);

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

    private void showSelectionMenu() {
        btnCollapse.setImageResource(android.R.drawable.arrow_up_float);
        selectionMenuContainer.setVisibility(View.VISIBLE);
        selectionMenuContainer.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.show_top_to_bottom));
        selectionMenuContainer.animate();
    }

    private void hideSelectionMenu() {
        btnCollapse.setImageResource(android.R.drawable.arrow_down_float);
        selectionMenuContainer.setVisibility(View.INVISIBLE);
        selectionMenuContainer.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.hide_bottom_to_top));
        selectionMenuContainer.animate();
    }

    @Override
    public void onReset(SelectionMenuData data) {
        hideSelectionMenu();
        chartFragment.setData(data, 80);
    }

    @Override
    public void onView(SelectionMenuData data) {
        hideSelectionMenu();
        chartFragment.setData(data, 80);
    }
}
