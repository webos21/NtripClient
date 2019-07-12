package kr.syszone.t20.bmts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import kr.syszone.t20.bmts.settings.SettingsMainFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (bundle == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsMainFragment()).commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuitem) {
        switch (menuitem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuitem);
        }
    }

}
