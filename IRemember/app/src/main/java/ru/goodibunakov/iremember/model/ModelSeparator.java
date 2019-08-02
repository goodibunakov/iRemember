package ru.goodibunakov.iremember.model;

import ru.goodibunakov.iremember.R;

public class ModelSeparator implements Item {

    public static final int TYPE_OVERDUE = R.string.separator_overdue;
    public static final int TYPE_TODAY = R.string.separator_today;
    public static final int TYPE_TOMORROW = R.string.separator_tomorrow;
    public static final int TYPE_FUTURE = R.string.separator_future;

    private int type;

    public ModelSeparator(int type) {
        this.type = type;
    }

    public boolean isTask() {
        return false;
    }

    @Override
    public String toString(ModelTask modelTask) {
        return "";
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
