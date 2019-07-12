package kr.syszone.t20.bmts.task;

import android.app.Activity;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import kr.syszone.t20.bmts.B2CSApp;
import kr.syszone.t20.bmts.control.DataGetherer;
import kr.syszone.t20.bmts.db.DbApi;
import kr.syszone.t20.bmts.model.AmbData;
import kr.syszone.t20.bmts.model.GpsData;
import kr.syszone.t20.bmts.model.ObdData;

public class GetherTask extends TimerTask {

    private static final long TS_DIFF = 1000; // 1 seconds

    private Activity act;
    private DataGetherer getherer;

    private GpsData lastGps;

    public GetherTask(Activity act, DataGetherer getherer) {
        this.act = act;
        this.getherer = getherer;
    }

    @Override
    public void run() {
        B2CSApp app = (B2CSApp) act.getApplication();
        DbApi dbh = app.getDbHandle();

        List<GpsData> gpsList = app.getGpsList();
        List<ObdData> obdList = app.getObdList();
        List<AmbData> ambList = app.getAmbList();

        Date tsNow = new Date();

        GpsData gd = getherer.getGpsData();
        ObdData od = getherer.getObdData();
        AmbData ad = getherer.getAmbData();

        synchronized (app) {
            if (gd != null) {
                long timeDiff = tsNow.getTime() - gd.getTimeStamp().getTime();
                if (timeDiff < 1000) {
                    dbh.updateGpsData(gd);
                    gpsList.add(gd);
                }
            }
            if (od != null) {
                dbh.updateObdData(od);
                obdList.add(od);
            }
            if (ad != null) {
                dbh.updateAmbData(ad);
                ambList.add(ad);
            }
        }
    }

}
