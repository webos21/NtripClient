package kr.syszone.t20.bmts;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import kr.syszone.t20.bmts.db.DbApi;
import kr.syszone.t20.bmts.db.DbOpenHelper;
import kr.syszone.t20.bmts.model.AmbData;
import kr.syszone.t20.bmts.model.GpsData;
import kr.syszone.t20.bmts.model.ObdData;

public class B2CSApp extends Application {

    // private EngLib engLib;
    private DbApi dbh;

    private List<GpsData> gpsList;
    private List<ObdData> obdList;
    private List<AmbData> ambList;

    private byte[] aesKey;
    private String accessToken;

    @Override
    public void onCreate() {
        super.onCreate();

        gpsList = new ArrayList<GpsData>();
        obdList = new ArrayList<ObdData>();
        ambList = new ArrayList<AmbData>();

        DbOpenHelper doh = new DbOpenHelper(this);
        dbh = doh.getHandler();

        // engLib = EngLibFactory.getLibrary();
    }

    @Override
    public void onTerminate() {
        Log.i("Application", "onTerminate!!!!!!!!!!!!!");
//        if (engLib != null) {
//            EngLibFactory.releaseLibrary();
//            engLib = null;
//        }
        if (dbh != null) {
            dbh = null;
        }
        super.onTerminate();
    }

    public DbApi getDbHandle() {
        return dbh;
    }

//    public EngLib getEngLib() {
//        return engLib;
//    }

    public void setGpsList(List<GpsData> gpsList) {
        this.gpsList = gpsList;
    }

    public List<GpsData> getGpsList() {
        return gpsList;
    }

    public void setObdList(List<ObdData> obdList) {
        this.obdList = obdList;
    }

    public List<ObdData> getObdList() {
        return obdList;
    }

    public void setAmbList(List<AmbData> ambList) {
        this.ambList = ambList;
    }

    public List<AmbData> getAmbList() {
        return ambList;
    }

    public void setAuthInfo(byte[] aesKey, String accessToken) {
        this.aesKey = aesKey;
        this.accessToken = accessToken;
    }

    public byte[] getAesKey() {
        return aesKey;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
