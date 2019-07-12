package kr.syszone.t20.bmts.control;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Date;

import kr.syszone.t20.bmts.tmap.TMapEventHandler;

public class GpsListener implements LocationListener {

    private TMapEventHandler tmapHandler;
    private TextView speedView;

    public GpsListener(TMapEventHandler tmapHandler, TextView speedView) {
        this.tmapHandler = tmapHandler;
        this.speedView = speedView;
    }

    public void onLocationChanged(Location location) {
        int kmh = (int) (location.getSpeed() * 3600.0F / 1000.0F);
        if (LocationManager.NETWORK_PROVIDER.equals(location.getProvider())) {
            if (location.getAccuracy() > 20.0F) {
                return;
            }
        }
        if (LocationManager.GPS_PROVIDER.equals(location.getProvider())) {
            if (location.getAccuracy() > 15.0F) {
                return;
            }
        }
        tmapHandler.setCurrentMarker(location);
        speedView.setText(kmh + "km");
    }

    public void onStatusChanged(String s, int i, Bundle b) {
    }

    public void onProviderDisabled(String s) {
    }

    public void onProviderEnabled(String s) {
    }

}
