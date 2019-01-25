package eu.wonderfulme.triptracker.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import eu.wonderfulme.triptracker.R;
import eu.wonderfulme.triptracker.utility.UtilsSharedPref;

class NumberPickerDialog extends Dialog {

    private final Context mContext;
    private NumberPicker mNumberPicker;
    private Button mApplyButton;
    private int mMax;
    private int mMin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_number_picker);
        mApplyButton = findViewById(R.id.btn_numberPicker_apply);
        mNumberPicker = findViewById(R.id.number_picker);
        buildNumberPicker();
        mApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int period = mNumberPicker.getValue();
                UtilsSharedPref.setRecordPeriodToSharedPref(mContext, period);
                dismiss();
            }
        });
    }

    NumberPickerDialog(@NonNull Context context, int min, int max) {
        super(context);
        mContext = context;
        mMin = min;
        mMax = max;
    }

    private void buildNumberPicker() {
        mNumberPicker.setMinValue(mMin);
        mNumberPicker.setMaxValue(mMax);
        mNumberPicker.setWrapSelectorWheel(false);
    }
}
