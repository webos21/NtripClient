package kr.syszone.t20.bmts;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.webos21.android.patch.PRNGFixes;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;
import java.util.Timer;

import kr.syszone.t20.bmts.control.DataGetherer;
import kr.syszone.t20.bmts.control.GpsListener;
import kr.syszone.t20.bmts.model.AmbData;
import kr.syszone.t20.bmts.model.GpsData;
import kr.syszone.t20.bmts.model.MapPoint;
import kr.syszone.t20.bmts.model.ObdData;
import kr.syszone.t20.bmts.settings.SettingsConst;
import kr.syszone.t20.bmts.task.GetherTask;
import kr.syszone.t20.bmts.task.LoginTask;
import kr.syszone.t20.bmts.task.MsgGetTask;
import kr.syszone.t20.bmts.task.TransferTask;
import kr.syszone.t20.bmts.tmap.TMapEventHandler;
import kr.syszone.t20.bmts.widget.PreviewDialog;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "MainActivity";

    private static final String TMAP_KEY = "7ec69d17-9d4b-44fd-bb0c-f48f78a2b4dc"; // 발급받은 appKey

    private static final String KEY_LAST_LAT = "last_latitude";
    private static final String KEY_LAST_LON = "last_longitude";

    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 0; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds

    private static final int ACTION_CAMERA = 1;
    private static final int ACTION_PERM_LOC = 2;
    private static final int ACTION_PERM_CAM = 3;

    private NavigationView navigationView;

    private Timer taskTimer;
    private DataGetherer getherer;

    private TMapView tmapView;
    private TMapEventHandler tmapHandler;

    private LocationManager locationManager;
    private GpsListener gpsListener;

    private AlertDialog exitDialog;
    private TextView infoView;
    private TextView speedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Prevent Screen Saver
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Allocate the Main-Layout
        setContentView(R.layout.activity_main);

        // Android SecureRandom Fix!!! (No Dependency)
        PRNGFixes.apply();

        // Set Tool-Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set Floating-Action-Bar : info
        FloatingActionButton fab_info = (FloatingActionButton) findViewById(R.id.fab_info);
        if (fab_info != null) {
            fab_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (speedView.getVisibility() == View.GONE) {
                        infoView.setVisibility(View.VISIBLE);
                        speedView.setVisibility(View.VISIBLE);
                        //tmapView.setTrafficInfo(true);
                    } else {
                        infoView.setVisibility(View.GONE);
                        speedView.setVisibility(View.GONE);
                        //tmapView.setTrafficInfo(false);
                    }
                }
            });
        }

        // Set Floating-Action-Bar : map
        FloatingActionButton fab_map = (FloatingActionButton) findViewById(R.id.fab_map);
        if (fab_map != null) {
            fab_map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACTION_PERM_LOC);
                        return;
                    }
                    if (tmapHandler.getLastLocation() == null && locationManager != null) {
                        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null) {
                            tmapView.setCenterPoint(loc.getLongitude(), loc.getLatitude());
                        }
                    }
                }
            });
        }

        // Set Drawer-Layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            //noinspection deprecation
            drawer.setDrawerListener(toggle);
            toggle.syncState();
        }

        // Set Navigation-View
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        // Get Info View
        infoView = (TextView) findViewById(R.id.info_place);
        infoView.setText("Searching...");

        // Get Speed View
        speedView = (TextView) findViewById(R.id.speed_place);
        speedView.setText("0km");

        // Get Exit Dialog
        exitDialog = createExitDialog();

        // Make Data-Getherer
        getherer = new DataGetherer();

        // Initialize the SharedPreference
        SharedPreferences shPref = PreferenceManager.getDefaultSharedPreferences(this);
        shPref.registerOnSharedPreferenceChangeListener(this);

        String phoneNo = shPref.getString(SettingsConst.KEY_PHONE_NUMBER, null);
        if (phoneNo == null || phoneNo.length() == 0) {
            PreferenceManager.setDefaultValues(this, R.xml.settings_bmts, false);
        }

        // Set TMapView
        tmapView = new TMapView(this);
        tmapView.setSKTMapApiKey(TMAP_KEY);
        tmapView.setTMapLogoPosition(TMapView.TMapLogoPositon.POSITION_BOTTOMRIGHT);
        //tmapView.setTrafficInfo(true);
        tmapHandler = new TMapEventHandler(this, tmapView, infoView);

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.fragment_place);
        rl.removeAllViews();
        rl.addView(tmapView);

        // Check permission and Initialize the location manager
        initLocationManager();

        // Set the Timer Scheduler
        resetTimer(shPref);

        if (savedInstanceState == null) {
            // Start Login
            new LoginTask(this).execute();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            showExitDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_call:
                showDial();
                return true;
            case R.id.action_sms:
                showMessages();
                return true;
            case R.id.action_camera:
                showCamera();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_cur_pos:
                break;
            case R.id.nav_call:
                showDial();
                break;
            case R.id.nav_sms:
                showMessages();
                break;
            case R.id.nav_camera:
                showCamera();
                break;
            case R.id.nav_settings:
                showSettings();
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        navigationView.getMenu().getItem(0).setChecked(true);

        return true;
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart!!!!!!!!!!!!!");

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACTION_PERM_LOC);
        }

        SharedPreferences shPref = PreferenceManager.getDefaultSharedPreferences(this);
        String lat = shPref.getString(KEY_LAST_LAT, null);
        String lon = shPref.getString(KEY_LAST_LON, null);
        if (lat != null && lat.length() > 0 && lon != null && lon.length() > 0) {
            tmapView.setCenterPoint(Double.parseDouble(lon), Double.parseDouble(lat));
        }
        navigationView.getMenu().getItem(0).setChecked(true);

        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop!!!!!!!!!!!!!");

        SharedPreferences shPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (shPref != null && tmapHandler != null && tmapHandler.getLastLocation() != null) {
            storeLastLocation();
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "onDestroy!!!!!!!!!!!!!");

        if (taskTimer != null) {
            taskTimer.cancel();
            taskTimer = null;
        }
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(gpsListener);
            locationManager = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "[onActivityResult] requestCode = " + requestCode + " / resultCode = " + resultCode);
        if (ACTION_CAMERA == requestCode && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if (imageBitmap != null) {
                showPreviewDialog(imageBitmap);
            }
        }
        if (requestCode == ACTION_PERM_LOC) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String toastMsg = "이 앱은 [" + Manifest.permission.ACCESS_FINE_LOCATION + "] 권한이 필수 입니다!!!";
                Toast.makeText(MainActivity.this, toastMsg, Toast.LENGTH_LONG).show();
            } else {
                initLocationManager();
            }
        }
        if (requestCode == ACTION_PERM_CAM) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                String toastMsg = "이 앱은 [" +
                        Manifest.permission.CAMERA + " / " +
                        Manifest.permission.READ_EXTERNAL_STORAGE + " / " +
                        Manifest.permission.WRITE_EXTERNAL_STORAGE + "] 권한이 필수 입니다!!!";
                Toast.makeText(MainActivity.this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == ACTION_PERM_LOC) {
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        String toastMsg = "이 앱은 [" + Manifest.permission.ACCESS_FINE_LOCATION + "] 권한이 필수 입니다!!!";
                        Toast.makeText(MainActivity.this, toastMsg, Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                        startActivityForResult(intent, ACTION_PERM_LOC);
                    } else {
                        initLocationManager();
                    }
                }
            }
        }
        if (requestCode == ACTION_PERM_CAM) {
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (Manifest.permission.CAMERA.equals(permission)) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        String toastMsg = "이 앱은 [" + Manifest.permission.CAMERA + "] 권한이 필수 입니다!!!";
                        Toast.makeText(MainActivity.this, toastMsg, Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                        startActivityForResult(intent, ACTION_PERM_CAM);
                    }
                }
                if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission)) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        String toastMsg = "이 앱은 [" + Manifest.permission.READ_EXTERNAL_STORAGE + "] 권한이 필수 입니다!!!";
                        Toast.makeText(MainActivity.this, toastMsg, Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                        startActivityForResult(intent, ACTION_PERM_CAM);
                    }
                }
                if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        String toastMsg = "이 앱은 [" + Manifest.permission.WRITE_EXTERNAL_STORAGE + "] 권한이 필수 입니다!!!";
                        Toast.makeText(MainActivity.this, toastMsg, Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                        startActivityForResult(intent, ACTION_PERM_CAM);
                    }
                }
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences shPref, String key) {
        Log.i(TAG, "onSharedPreferenceChanged : key = " + key);
        if (SettingsConst.KEY_GETHER_DATA.equals(key) || SettingsConst.KEY_TRANSFER_DATA.equals(key)
                || SettingsConst.KEY_GETHER_PERIOD.equals(key) || SettingsConst.KEY_TRANSFER_PERIOD.equals(key)
                || SettingsConst.KEY_GETHER_DLOG.equals(key)) {
            resetTimer(shPref);
        }
    }

    private AlertDialog createExitDialog() {
        // Initialize Exit Dialog (No Dependency)
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.main_information);
        builder.setMessage(R.string.main_exit_msg);
        builder.setPositiveButton(R.string.main_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.main_btn_cancel, null);
        return builder.create();
    }

    private void initLocationManager() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        MainActivity.this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        MainActivity.this.gpsListener = new GpsListener(tmapHandler, speedView);
        MainActivity.this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES,
                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, gpsListener);
        MainActivity.this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES,
                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, gpsListener);
    }

    private void showExitDialog() {
        exitDialog.show();
    }

    private void showDial() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        if (intent != null && intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Error: 전화 기능이 없습니다!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showMessages() {
        Intent intent = new Intent(this, MessagesActivity.class);
        startActivity(intent);
    }

    private void showCamera() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    ACTION_PERM_CAM);
            return;
        }
        if (tmapHandler.getLastLocation() == null) {
            Toast.makeText(this, "위치 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent != null && takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, ACTION_CAMERA);
        }
    }

    private void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void showPreviewDialog(Bitmap bitmap) {
        GpsData gd = new GpsData();
        Location loc = tmapHandler.getLastLocation();

        PreviewDialog pd = new PreviewDialog(this, bitmap, loc);
        pd.show();
    }

    private void storeLastLocation() {
        SharedPreferences shPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (shPref != null) {
            SharedPreferences.Editor prefEditor = shPref.edit();
            prefEditor.putString(KEY_LAST_LAT, Double.toString(tmapHandler.getLastLocation().getLatitude()));
            prefEditor.putString(KEY_LAST_LON, Double.toString(tmapHandler.getLastLocation().getLongitude()));
            prefEditor.commit();
        }
    }

    private void resetTimer(SharedPreferences shPref) {
        if (taskTimer != null) {
            taskTimer.cancel();
            taskTimer = null;
        }

        taskTimer = new Timer();

        boolean getherData = shPref.getBoolean(SettingsConst.KEY_GETHER_DATA, false);
        boolean getherDlog = shPref.getBoolean(SettingsConst.KEY_GETHER_DLOG, false);
        int getherPeriod = Integer.parseInt(shPref.getString(SettingsConst.KEY_GETHER_PERIOD, "-1"));
        boolean transferServer = shPref.getBoolean(SettingsConst.KEY_TRANSFER_DATA, false);
        int messagePeriod = Integer.parseInt(shPref.getString(SettingsConst.KEY_MESSAGE_PERIOD, "-1"));
        int transferPeriod = Integer.parseInt(shPref.getString(SettingsConst.KEY_TRANSFER_PERIOD, "-1"));

        if (getherDlog) {
            B2CSApp app = (B2CSApp) getApplication();
            EngLib engLib = app.getEngLib();
            getherer.setEngLib(engLib);
        } else {
            getherer.setTmapHandler(tmapHandler);
        }

        if (getherData && getherPeriod > 0) {
            taskTimer.schedule(new GetherTask(this, getherer), getherPeriod * 1000, getherPeriod * 1000);
        }
        if (transferServer && messagePeriod > 0) {
            taskTimer.schedule(new MsgGetTask(this), messagePeriod * 1000, messagePeriod * 1000);
        }
        if (transferServer && transferPeriod > 0) {
            taskTimer.schedule(new TransferTask(this), transferPeriod * 1000, transferPeriod * 1000);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        B2CSApp app = (B2CSApp) getApplication();
        if (app != null) {
            byte[] aesKey = app.getAesKey();
            String accessToken = app.getAccessToken();
            if (aesKey != null && accessToken != null) {
                outState.putByteArray("AES_KEY", aesKey);
                outState.putString("ACCESS_TOKEN", accessToken);
            }

            ArrayList<GpsData> gpsList = (ArrayList<GpsData>) app.getGpsList();
            if (gpsList != null) {
                outState.putParcelableArrayList("GPS_LIST", gpsList);
            }
            ArrayList<ObdData> obdList = (ArrayList<ObdData>) app.getObdList();
            if (obdList != null) {
                outState.putParcelableArrayList("OBD_LIST", obdList);
            }
            ArrayList<AmbData> ambList = (ArrayList<AmbData>) app.getAmbList();
            if (ambList != null) {
                outState.putParcelableArrayList("AMB_LIST", ambList);
            }
        }

        ArrayList<TMapPoint> points = tmapHandler.getLinePoints();
        if (points != null && points.size() > 0) {
            ArrayList<MapPoint> mps = new ArrayList<MapPoint>();
            for (TMapPoint tp : points) {
                mps.add(new MapPoint(tp));
            }
            outState.putParcelableArrayList("POINT_LIST", mps);
        }

        Location loc = tmapHandler.getLastLocation();
        if (loc != null) {
            outState.putParcelable("LAST_LOCATION", loc);
        }

        int zoomLevel = tmapView.getZoomLevel();
        outState.putInt("ZOOM_LEVEL", zoomLevel);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        B2CSApp app = (B2CSApp) getApplication();

        byte[] aesKey = savedInstanceState.getByteArray("AES_KEY");
        String accessToken = savedInstanceState.getString("ACCESS_TOKEN");
        if (aesKey != null && accessToken != null) {
            app.setAuthInfo(aesKey, accessToken);
        }

        ArrayList<GpsData> gpsList = savedInstanceState.getParcelableArrayList("GPS_LIST");
        if (gpsList != null) {
            app.setGpsList(gpsList);
        }
        ArrayList<ObdData> obdList = savedInstanceState.getParcelableArrayList("OBD_LIST");
        if (obdList != null) {
            app.setObdList(obdList);
        }
        ArrayList<AmbData> ambList = savedInstanceState.getParcelableArrayList("AMB_LIST");
        if (ambList != null) {
            app.setAmbList(ambList);
        }

        ArrayList<MapPoint> mps = savedInstanceState.getParcelableArrayList("POINT_LIST");
        if (mps != null && mps.size() > 0) {
            tmapHandler.setLinePoints(mps);
        }

        Location loc = savedInstanceState.getParcelable("LAST_LOCATION");
        if (loc != null) {
            tmapHandler.setCurrentMarker(loc);
        }

        int zoomLevel = savedInstanceState.getInt("ZOOM_LEVEL");
        tmapView.setZoomLevel(zoomLevel);

    }

}
