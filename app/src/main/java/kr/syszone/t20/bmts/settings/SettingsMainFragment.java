package kr.syszone.t20.bmts.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import kr.syszone.t20.bmts.R;

public class SettingsMainFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    private EditTextPreference phoneNo;
    private EditTextPreference serverAddr;
    private EditTextPreference serverPort;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.settings_bmts);

        phoneNo = (EditTextPreference) findPreference(SettingsConst.KEY_PHONE_NUMBER);
        serverAddr = (EditTextPreference) findPreference(SettingsConst.KEY_SERVER_ADDRESS);
        serverPort = (EditTextPreference) findPreference(SettingsConst.KEY_SERVER_PORT);

        phoneNo.setOnPreferenceChangeListener(this);
        serverAddr.setOnPreferenceChangeListener(this);
        serverPort.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        phoneNo.setSummary(sp.getString(phoneNo.getKey(), SettingsConst.DEF_PHONE_NUMBER));
        serverAddr.setSummary(sp.getString(serverAddr.getKey(), SettingsConst.DEF_SERVER_ADDRESS));
        serverPort.setSummary(sp.getString(serverPort.getKey(), SettingsConst.DEF_SERVER_PORT));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference instanceof EditTextPreference) {
            String stringValue = newValue.toString();
            preference.setSummary(stringValue);
            return true;
        }
        return false;
    }

}
