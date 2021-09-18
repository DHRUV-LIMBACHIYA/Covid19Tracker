package com.example.covid19tracker.Utils;

import android.text.TextUtils;

import androidx.databinding.ObservableField;

/**
 * Created by Naria Sachin on Jan, 15 2020 12:45.
 */

public class ObservableString extends ObservableField<String> {
    public ObservableString(String s) {
        set(s);
    }

    public ObservableString() {
    }

    @Override
    public String get() {
        return (super.get() == null ? "" : super.get());
    }

    /**
     * Method for get trimmed data
     *
     * @return trimmed data
     */
    public String getTrimmed() {
        String stringData = get();
        return (!TextUtils.isEmpty(stringData) && !TextUtils.isEmpty(stringData.trim()) ? stringData.trim() : "");
    }

    /**
     * Get String length
     *
     * @return length
     */
    public int getTrimmedLength() {
        String trimmedData = getTrimmed();
        return (!TextUtils.isEmpty(trimmedData) ? trimmedData.length() : 0);
    }

    /**
     * Check is Empty String
     *
     * @return is Empty
     */
    public boolean isEmptyData() {
        return (TextUtils.isEmpty(getTrimmed()));
    }
}
