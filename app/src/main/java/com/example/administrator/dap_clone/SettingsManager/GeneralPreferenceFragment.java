package com.example.administrator.dap_clone.SettingsManager;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.example.administrator.dap_clone.ConstantValues;
import com.example.administrator.dap_clone.R;

/**
 * Created by Administrator on 03/20/2017.
 */

public class GeneralPreferenceFragment extends PreferenceFragment {

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_general);

		// Bind the summaries of EditText/List/Dialog/Ringtone preferences
		// to their values. When their values change, their summaries are
		// updated to reflect the new value, per the Android Design
		// guidelines.
		SettingUtils.bindPreferenceSummaryToValue(findPreference(ConstantValues.SETTING_NUMBER_THREAD_DOWNLOAD));
	}
}
