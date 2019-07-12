package kr.syszone.t20.bmts.db;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import kr.syszone.t20.bmts.model.AmbData;
import kr.syszone.t20.bmts.model.GpsData;
import kr.syszone.t20.bmts.model.MsgData;
import kr.syszone.t20.bmts.model.ObdData;

public interface DbApi {

    public SQLiteDatabase getHandleRead();

    public SQLiteDatabase getHandleWrite();


    public List<MsgData> getMsgDataList();

    public MsgData getMsgData(long id);

    public boolean updateMsgData(MsgData aRow);

    public int deleteMsgData(MsgData aRow);


    public List<GpsData> getGpsDataList();

    public GpsData getGpsData(long id);

    public boolean updateGpsData(GpsData aRow);

    public int deleteGpsData(GpsData aRow);


    public List<ObdData> getObdDataList();

    public ObdData getObdData(long id);

    public boolean updateObdData(ObdData aRow);

    public int deleteObdData(ObdData aRow);


    public List<AmbData> getAmbDataList();

    public AmbData getAmbData(long id);

    public boolean updateAmbData(AmbData aRow);

    public int deleteAmbData(AmbData aRow);


    public void debugDump(String tableName);

}
