package kr.syszone.t20.bmts.control;

import android.location.Location;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import kr.syszone.t20.bmts.model.AmbData;
import kr.syszone.t20.bmts.model.GpsData;
import kr.syszone.t20.bmts.model.ObdData;
import kr.syszone.t20.bmts.tmap.TMapEventHandler;
import com.gmail.webos21.ntrip.lib.comm.ConnectState;

public class DataGetherer {

    private static final SimpleDateFormat SDF_PARSER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());

    private TMapEventHandler tmapHandler;
    private EngLib engLib;

    public DataGetherer() {
    }

    public GpsData getGpsData() {
        GpsData gd = null;
        if (tmapHandler != null) {
            Location loc = tmapHandler.getLastLocation();
            if (loc != null) {
                float kmh = loc.getSpeed() * 3600.0F / 1000.0F;
                gd = new GpsData((Long) null, loc.getTime(), loc.getLatitude(), loc.getLongitude(), (float) loc.getAltitude(), loc.getAccuracy(), kmh, loc.getBearing());
            }
        }
        if (engLib != null && engLib.getConnectionState() == ConnectState.ON) {
            String gpsData = engLib.getDLogGps();
            String[] gpsDataArr = (gpsData == null || gpsData.length() == 0) ? null : gpsData.split(",");
            if (gpsData != null && gpsData.length() > 0 && gpsDataArr != null && gpsDataArr.length == 7) {
                try {
                    Date ts = SDF_PARSER.parse(gpsDataArr[0]);
                    Double lat = Double.parseDouble(gpsDataArr[1]);
                    Double lon = Double.parseDouble(gpsDataArr[2]);
                    Float alt = Float.parseFloat(gpsDataArr[3]);
                    Float acc = Float.parseFloat(gpsDataArr[4]);
                    Float s = Float.parseFloat(gpsDataArr[5]);
                    Float ang = Float.parseFloat(gpsDataArr[6]);

                    gd = new GpsData((Long) null, ts.getTime(), lat, lon, alt, acc, s, ang);
                } catch (ParseException e) {
                    e.printStackTrace();
                    gd = null;
                }
            }
        }
        return gd;
    }

    public ObdData getObdData() {
        ObdData od = null;
        if (engLib != null && engLib.getConnectionState() == ConnectState.ON) {
            String obdData = engLib.getDLogObd();
            String[] obdDataArr = (obdData == null || obdData.length() == 0) ? null : obdData.split(",");
            if (obdData != null && obdData.length() > 0 && obdDataArr != null && obdDataArr.length == 43) {
                try {
                    Date ts = SDF_PARSER.parse(obdDataArr[0]);
                    Integer rpm = Integer.parseInt(obdDataArr[30]);
                    Float speed = Float.parseFloat(obdDataArr[32]);
                    Integer brake = Integer.parseInt(obdDataArr[38]);
                    Integer throttle = Integer.parseInt(obdDataArr[12]);
                    String dtc = obdDataArr[28];
                    Float accDistance = Float.parseFloat(obdDataArr[6]);
                    Float efficiency = Float.parseFloat(obdDataArr[8]);
                    Float batteryVolt = Float.parseFloat(obdDataArr[16]);
                    Float tempEngOil = Float.parseFloat(obdDataArr[10]);
                    Float tempCoolant = Float.parseFloat(obdDataArr[14]);
                    Float tempInduct = Float.parseFloat(obdDataArr[18]);
                    Float tempAmbient = Float.parseFloat(obdDataArr[20]);
                    Float maf = Float.parseFloat(obdDataArr[24]);
                    Float amp = Float.parseFloat(obdDataArr[26]);

                    od = new ObdData((Long) null, ts.getTime(), rpm, speed, brake, throttle, dtc, accDistance, efficiency, batteryVolt, tempEngOil, tempCoolant, tempInduct, tempAmbient, maf, amp);
                } catch (ParseException e) {
                    e.printStackTrace();
                    od = null;
                }
            }
        }
        return od;
    }

    public AmbData getAmbData() {
        AmbData ad = null;
        if (engLib != null && engLib.getConnectionState() == ConnectState.ON) {
            String tnhData = engLib.getDLogTnh();
            String[] tnhDataArr = (tnhData == null || tnhData.length() == 0) ? null : tnhData.split(",");
            if (tnhData != null && tnhData.length() > 0 && tnhDataArr != null && tnhDataArr.length == 3) {
                try {
                    Date ts = SDF_PARSER.parse(tnhDataArr[0]);
                    Float t = Float.parseFloat(tnhDataArr[1]);
                    Float h = Float.parseFloat(tnhDataArr[2]);

                    ad = new AmbData((Long) null, ts.getTime(), t, h);
                } catch (ParseException e) {
                    e.printStackTrace();
                    ad = null;
                }
            }
        }
        return ad;
    }

    public void setTmapHandler(TMapEventHandler tmapHandler) {
        this.engLib = null;
        this.tmapHandler = tmapHandler;
    }

    public void setEngLib(EngLib engLib) {
        this.tmapHandler = null;
        this.engLib = engLib;
    }

}
