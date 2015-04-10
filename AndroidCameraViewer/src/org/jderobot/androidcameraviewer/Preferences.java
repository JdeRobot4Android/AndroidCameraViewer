package org.jderobot.androidcameraviewer;



import com.example.androidcameraviewer.R;

import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.os.Bundle;

public class Preferences extends PreferenceActivity implements OnPreferenceChangeListener {


  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      addPreferencesFromResource(R.xml.preference);

  }

@Override
public boolean onPreferenceChange(Preference preference, Object newValue) {
	// TODO Auto-generated method stub
	return false;
}

}
