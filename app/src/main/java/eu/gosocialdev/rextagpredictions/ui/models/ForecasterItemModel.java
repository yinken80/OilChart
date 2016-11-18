package eu.gosocialdev.rextagpredictions.ui.models;

/**
 * Created by Administrator on 11/17/2016.
 */

public class ForecasterItemModel {
    private String text;
    private boolean checked;

    public ForecasterItemModel() {
        checked = false;
        text = "";
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean bChecked) {
        this.checked = bChecked;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
