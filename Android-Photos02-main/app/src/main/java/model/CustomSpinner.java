package model;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import android.widget.AdapterView;
import android.widget.Spinner;

public class CustomSpinner extends AppCompatSpinner {
    @Nullable
    private OnItemSelectedListener customItemSelectedListener;

    public CustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        if (customItemSelectedListener != null) {
            customItemSelectedListener.onItemSelected(null, null, position, 0);
        }
    }

    public void setCustomItemSelectedListener(@Nullable OnItemSelectedListener listener) {
        this.customItemSelectedListener = listener;
    }
}
