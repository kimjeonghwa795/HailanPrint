package com.hailan.HaiLanPrint.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.widget.EditText;

import com.hailan.HaiLanPrint.R;


/**
 * Created by yoghourt on 6/30/16.
 */


public class MultiFiltersEditText extends EditText {

    public MultiFiltersEditText(Context context) {
        super(context);
    }

    public MultiFiltersEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(context, attrs);
    }

    public MultiFiltersEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeSet(context, attrs);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MultiFiltersEditText);
        int maxInputNum = typedArray.getInt(R.styleable.MultiFiltersEditText_maxInputNum, Integer.MAX_VALUE);
        setMaxLength(maxInputNum);
    }

    public void setMaxLength(int length) {
        InputFilter curFilters[];
        InputFilter.LengthFilter lengthFilter;
        int idx;

        lengthFilter = new InputFilter.LengthFilter(length);

        curFilters = this.getFilters();
        if (curFilters != null) {
            for (idx = 0; idx < curFilters.length; idx++) {
                if (curFilters[idx] instanceof InputFilter.LengthFilter) {
                    curFilters[idx] = lengthFilter;
                    return;
                }
            }

            // since the length filter was not part of the list, but
            // there are filters, then add the length filter
            InputFilter newFilters[] = new InputFilter[curFilters.length + 1];
            System.arraycopy(curFilters, 0, newFilters, 0, curFilters.length);
            newFilters[curFilters.length] = lengthFilter;
            this.setFilters(newFilters);
        } else {
            this.setFilters(new InputFilter[] { lengthFilter });
        }
    }
}
