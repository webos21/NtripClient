package kr.syszone.t20.bmts.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import kr.syszone.t20.bmts.model.AmbData;
import kr.syszone.t20.bmts.model.GpsData;
import kr.syszone.t20.bmts.model.MsgData;
import kr.syszone.t20.bmts.model.ObdData;

class DbHandler implements DbApi {

    private static final String TAG = DbHandler.class.getSimpleName();

    private SQLiteOpenHelper dbh;

    public DbHandler(DbOpenHelper dbh) {
        this.dbh = dbh;
    }

    public SQLiteDatabase getHandleRead() {
        return dbh.getReadableDatabase();
    }

    public SQLiteDatabase getHandleWrite() {
        return dbh.getWritableDatabase();
    }

    public List<MsgData> getMsgDataList() {
        List<MsgData> clList = new ArrayList<MsgData>();

        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor rset = db.rawQuery("SELECT * FROM " + DbConst.TB_MESSAGES + " ORDER BY timestamp", null);
        if (rset == null || rset.getCount() == 0) {
            return clList;
        }

        rset.moveToFirst();
        int total = rset.getCount();
        for (int i = 0; i < total; i++) {
            MsgData aRow = new MsgData(
            /* _id ------------ */rset.getLong(0),
            /* ts_add --------- */rset.getLong(1),
            /* req_type ------- */rset.getInt(2),
            /* sender --------- */rset.getString(3),
            /* receiver ------- */rset.getString(4),
            /* content -------- */rset.getString(5));
            clList.add(aRow);
            rset.moveToNext();
        }

        if (rset != null) {
            rset.close();
        }
        db.close();

        return clList;
    }

    public MsgData getMsgData(long id) {
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor rset = db.rawQuery("SELECT * FROM " + DbConst.TB_MESSAGES
                + " WHERE _id = " + id, null);
        if (rset == null || rset.getCount() == 0) {
            return null;
        }
        rset.moveToFirst();
        MsgData aRow = new MsgData(
            /* id ------------- */rset.getLong(0),
            /* ts_add --------- */rset.getLong(1),
            /* req_type ------- */rset.getInt(2),
            /* sender --------- */rset.getString(3),
            /* receiver ------- */rset.getString(4),
            /* content -------- */rset.getString(5));
        rset.close();
        db.close();
        return aRow;

    }

    public boolean updateMsgData(MsgData aRow) {
        SQLiteDatabase db = dbh.getWritableDatabase();
        Cursor rset = (aRow.getId() != null) ? db.rawQuery("SELECT * FROM " + DbConst.TB_MESSAGES
                + " WHERE _id = " + aRow.getId(), null) : null;
        if (rset == null || rset.getCount() == 0) {
            ContentValues cv = new ContentValues();
            cv.put("ts_add", aRow.getTimeStamp().getTime());
            cv.put("req_type", aRow.getType());
            cv.put("sender", aRow.getSender());
            cv.put("receiver", aRow.getReceiver());
            cv.put("content", aRow.getContent());
            Long id = db.insert(DbConst.TB_MESSAGES, null, cv);
            aRow.setId(id);
        } else {
            ContentValues cv = new ContentValues();
            cv.put("ts_add", aRow.getTimeStamp().getTime());
            cv.put("req_type", aRow.getType());
            cv.put("sender", aRow.getSender());
            cv.put("receiver", aRow.getReceiver());
            cv.put("content", aRow.getContent());
            db.update(DbConst.TB_MESSAGES, cv, " id = ? ",
                    new String[]{Long.toString(aRow.getId())});
        }

        if (rset != null) {
            rset.close();
        }
        db.close();

        if (DbConst.DEBUG) {
            debugDump(DbConst.TB_MESSAGES);
        }

        return true;
    }

    public int deleteMsgData(MsgData aRow) {
        SQLiteDatabase db = dbh.getWritableDatabase();
        int result = db.delete(DbConst.TB_MESSAGES, "_id = " + aRow.getId(), null);
        db.close();

        if (DbConst.DEBUG) {
            debugDump(DbConst.TB_MESSAGES);
        }

        return result;
    }

    @Override
    public List<GpsData> getGpsDataList() {
        List<GpsData> clList = new ArrayList<GpsData>();

        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor rset = db.rawQuery("SELECT * FROM "
                + DbConst.TB_GPS, null);
        if (rset == null || rset.getCount() == 0) {
            return clList;
        }

        rset.moveToFirst();
        int total = rset.getCount();
        for (int i = 0; i < total; i++) {
            GpsData aRow = new GpsData(
            /* id ------------- */rset.getLong(0),
            /* ts_gether ------ */rset.getLong(1),
            /* latitude ------- */rset.getDouble(2),
            /* longitude ------ */rset.getDouble(3),
            /* altitude ------- */rset.getFloat(4),
            /* accuracy ------- */rset.getFloat(5),
            /* speed ---------- */rset.getFloat(6),
            /* angle ---------- */rset.getFloat(7));
            clList.add(aRow);
            rset.moveToNext();
        }

        if (rset != null) {
            rset.close();
        }
        db.close();

        return clList;
    }

    @Override
    public GpsData getGpsData(long id) {
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor rset = db.rawQuery("SELECT * FROM " + DbConst.TB_GPS
                + " WHERE id = " + id, null);
        if (rset == null || rset.getCount() == 0) {
            return null;
        }
        rset.moveToFirst();
        GpsData aRow = new GpsData(
            /* id ------------- */rset.getLong(0),
            /* ts_gether ------ */rset.getLong(1),
            /* latitude ------- */rset.getDouble(2),
            /* longitude ------ */rset.getDouble(3),
            /* altitude ------- */rset.getFloat(4),
            /* accuracy ------- */rset.getFloat(5),
            /* speed ---------- */rset.getFloat(6),
            /* angle ---------- */rset.getFloat(7));
        rset.close();
        db.close();
        return aRow;
    }

    @Override
    public boolean updateGpsData(GpsData aRow) {
        SQLiteDatabase db = dbh.getWritableDatabase();
        Cursor rset = (aRow.getId() != null) ? db.rawQuery("SELECT * FROM " + DbConst.TB_GPS
                + " WHERE id = " + aRow.getId(), null) : null;
        if (rset == null || rset.getCount() == 0) {
            ContentValues cv = new ContentValues();
            cv.put("ts_gether", aRow.getTimeStamp().getTime());
            cv.put("latitude", aRow.getLatitude());
            cv.put("longitude", aRow.getLongitude());
            cv.put("altitude", aRow.getAltitude());
            cv.put("accuracy", aRow.getAccuracy());
            cv.put("speed", aRow.getSpeed());
            cv.put("angle", aRow.getAngle());
            Long id = db.insert(DbConst.TB_GPS, null, cv);
            aRow.setId(id);
        } else {
            ContentValues cv = new ContentValues();
            cv.put("ts_gether", aRow.getTimeStamp().getTime());
            cv.put("latitude", aRow.getLatitude());
            cv.put("longitude", aRow.getLongitude());
            cv.put("altitude", aRow.getAltitude());
            cv.put("accuracy", aRow.getAccuracy());
            cv.put("speed", aRow.getSpeed());
            cv.put("angle", aRow.getAngle());
            db.update(DbConst.TB_GPS, cv, " id = ? ",
                    new String[]{Long.toString(aRow.getId())});
        }

        if (rset != null) {
            rset.close();
        }
        db.close();

        if (DbConst.DEBUG) {
            debugDump(DbConst.TB_GPS);
        }

        return true;
    }

    @Override
    public int deleteGpsData(GpsData aRow) {
        SQLiteDatabase db = dbh.getWritableDatabase();
        int result = db.delete(DbConst.TB_GPS, "id = " + aRow.getId(), null);
        db.close();

        if (DbConst.DEBUG) {
            debugDump(DbConst.TB_GPS);
        }

        return result;
    }

    @Override
    public List<ObdData> getObdDataList() {
        List<ObdData> clList = new ArrayList<ObdData>();

        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor rset = db.rawQuery("SELECT * FROM "
                + DbConst.TB_OBD, null);
        if (rset == null || rset.getCount() == 0) {
            return clList;
        }

        rset.moveToFirst();
        int total = rset.getCount();
        for (int i = 0; i < total; i++) {
            ObdData aRow = new ObdData(
            /* id ------------- */rset.getLong(0),
            /* ts_gether ------ */rset.getLong(1),
            /* rpm ------------ */rset.getInt(2),
            /* speed ---------- */rset.getFloat(3),
            /* brake ---------- */rset.getInt(4),
            /* throttle ------- */rset.getInt(5),
            /* dtc ------------ */rset.getString(6),
            /* acc_distance --- */rset.getFloat(7),
            /* efficiency ----- */rset.getFloat(8),
            /* battery_volt --- */rset.getFloat(9),
            /* temp_eng_oil --- */rset.getFloat(10),
            /* temp_coolant --- */rset.getFloat(11),
            /* temp_induct ---- */rset.getFloat(12),
            /* temp_ambient --- */rset.getFloat(13),
            /* maf ------------ */rset.getFloat(14),
			/* amp ------------ */rset.getFloat(15));
            clList.add(aRow);
            rset.moveToNext();
        }

        if (rset != null) {
            rset.close();
        }
        db.close();

        return clList;
    }

    @Override
    public ObdData getObdData(long id) {
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor rset = db.rawQuery("SELECT * FROM " + DbConst.TB_OBD
                + " WHERE id = " + id, null);
        if (rset == null || rset.getCount() == 0) {
            return null;
        }
        rset.moveToFirst();
        ObdData aRow = new ObdData(
            /* id ------------- */rset.getLong(0),
            /* ts_gether ------ */rset.getLong(1),
            /* rpm ------------ */rset.getInt(2),
            /* speed ---------- */rset.getFloat(3),
            /* brake ---------- */rset.getInt(4),
            /* throttle ------- */rset.getInt(5),
			/* dtc ------------ */rset.getString(6),
			/* acc_distance --- */rset.getFloat(7),
			/* efficiency ----- */rset.getFloat(8),
			/* battery_volt --- */rset.getFloat(9),
			/* temp_eng_oil --- */rset.getFloat(10),
			/* temp_coolant --- */rset.getFloat(11),
			/* temp_induct ---- */rset.getFloat(12),
			/* temp_ambient --- */rset.getFloat(13),
			/* maf ------------ */rset.getFloat(14),
			/* amp ------------ */rset.getFloat(15));
        rset.close();
        db.close();
        return aRow;
    }

    @Override
    public boolean updateObdData(ObdData aRow) {
        SQLiteDatabase db = dbh.getWritableDatabase();
        Cursor rset = (aRow.getId() != null) ? db.rawQuery("SELECT * FROM " + DbConst.TB_OBD
                + " WHERE id = " + aRow.getId(), null) : null;
        if (rset == null || rset.getCount() == 0) {
            ContentValues cv = new ContentValues();
            cv.put("ts_gether", aRow.getTimeStamp().getTime());
            cv.put("rpm", aRow.getRpm());
            cv.put("speed", aRow.getSpeed());
            cv.put("brake", aRow.getBrake());
            cv.put("throttle", aRow.getThrottle());
            cv.put("dtc", aRow.getDtc());
            cv.put("acc_distance", aRow.getAccDistance());
            cv.put("efficiency", aRow.getEfficiency());
            cv.put("battery_volt", aRow.getBatteryVolt());
            cv.put("temp_eng_oil", aRow.getTempEngOil());
            cv.put("temp_coolant", aRow.getTempCoolant());
            cv.put("temp_induct", aRow.getTempInduct());
            cv.put("temp_ambient", aRow.getTempAmbient());
            cv.put("maf", aRow.getMaf());
            cv.put("amp", aRow.getAmp());
            Long id = db.insert(DbConst.TB_OBD, null, cv);
            aRow.setId(id);
        } else {
            ContentValues cv = new ContentValues();
            cv.put("ts_gether", aRow.getTimeStamp().getTime());
            cv.put("rpm", aRow.getRpm());
            cv.put("speed", aRow.getSpeed());
            cv.put("brake", aRow.getBrake());
            cv.put("throttle", aRow.getThrottle());
            cv.put("dtc", aRow.getDtc());
            cv.put("acc_distance", aRow.getAccDistance());
            cv.put("efficiency", aRow.getEfficiency());
            cv.put("battery_volt", aRow.getBatteryVolt());
            cv.put("temp_eng_oil", aRow.getTempEngOil());
            cv.put("temp_coolant", aRow.getTempCoolant());
            cv.put("temp_induct", aRow.getTempInduct());
            cv.put("temp_ambient", aRow.getTempAmbient());
            cv.put("maf", aRow.getMaf());
            cv.put("amp", aRow.getAmp());
            db.update(DbConst.TB_OBD, cv, " id = ? ",
                    new String[]{Long.toString(aRow.getId())});
        }

        if (rset != null) {
            rset.close();
        }
        db.close();

        if (DbConst.DEBUG) {
            debugDump(DbConst.TB_OBD);
        }

        return true;
    }

    @Override
    public int deleteObdData(ObdData aRow) {
        SQLiteDatabase db = dbh.getWritableDatabase();
        int result = db.delete(DbConst.TB_OBD, "id = " + aRow.getId(), null);
        db.close();

        if (DbConst.DEBUG) {
            debugDump(DbConst.TB_OBD);
        }

        return result;
    }

    @Override
    public List<AmbData> getAmbDataList() {
        List<AmbData> clList = new ArrayList<AmbData>();

        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor rset = db.rawQuery("SELECT * FROM "
                + DbConst.TB_AMB, null);
        if (rset == null || rset.getCount() == 0) {
            return clList;
        }

        rset.moveToFirst();
        int total = rset.getCount();
        for (int i = 0; i < total; i++) {
            AmbData aRow = new AmbData(
            /* id ------------- */rset.getLong(0),
            /* ts_gether ------ */rset.getLong(1),
            /* temperature ---- */rset.getFloat(2),
            /* humidity ------- */rset.getFloat(3));
            clList.add(aRow);
            rset.moveToNext();
        }

        if (rset != null) {
            rset.close();
        }
        db.close();

        return clList;
    }

    @Override
    public AmbData getAmbData(long id) {
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor rset = db.rawQuery("SELECT * FROM " + DbConst.TB_AMB
                + " WHERE id = " + id, null);
        if (rset == null || rset.getCount() == 0) {
            return null;
        }
        rset.moveToFirst();
        AmbData aRow = new AmbData(
            /* id ------------- */rset.getLong(0),
            /* ts_gether ------ */rset.getLong(1),
            /* temperature ---- */rset.getFloat(2),
            /* humidity ------- */rset.getFloat(3));
        rset.close();
        db.close();
        return aRow;
    }

    @Override
    public boolean updateAmbData(AmbData aRow) {
        SQLiteDatabase db = dbh.getWritableDatabase();
        Cursor rset = (aRow.getId() != null) ? db.rawQuery("SELECT * FROM " + DbConst.TB_AMB
                + " WHERE id = " + aRow.getId(), null) : null;
        if (rset == null || rset.getCount() == 0) {
            ContentValues cv = new ContentValues();
            cv.put("ts_gether", aRow.getTimeStamp().getTime());
            cv.put("temperature", aRow.getTemperature());
            cv.put("humidity", aRow.getHumidity());
            Long id = db.insert(DbConst.TB_AMB, null, cv);
            aRow.setId(id);
        } else {
            ContentValues cv = new ContentValues();
            cv.put("ts_gether", aRow.getTimeStamp().getTime());
            cv.put("temperature", aRow.getTemperature());
            cv.put("humidity", aRow.getHumidity());
            db.update(DbConst.TB_AMB, cv, " id = ? ",
                    new String[]{Long.toString(aRow.getId())});
        }

        if (rset != null) {
            rset.close();
        }
        db.close();

        if (DbConst.DEBUG) {
            debugDump(DbConst.TB_AMB);
        }

        return true;
    }

    @Override
    public int deleteAmbData(AmbData aRow) {
        SQLiteDatabase db = dbh.getWritableDatabase();
        int result = db.delete(DbConst.TB_AMB, "id = " + aRow.getId(), null);
        db.close();

        if (DbConst.DEBUG) {
            debugDump(DbConst.TB_AMB);
        }

        return result;
    }

    @Override
    public void debugDump(String tableName) {
        SQLiteDatabase db = dbh.getReadableDatabase();

        String q = "SELECT * FROM " + tableName;
        q += DbConst.TB_MESSAGES.equals(tableName) ? " ORDER BY _id " : " ORDER BY id ";
        q += " DESC LIMIT 5";
        Cursor rset = db.rawQuery(q, null);
        if (rset == null) {
            return;
        }

        int nCol = rset.getColumnCount();
        int nRow = rset.getCount();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nCol; i++) {
            sb.append(rset.getColumnName(i)).append('(').append(i)
                    .append(')').append(',');
        }
        if (DbConst.DEBUG) {
            Log.d(TAG, sb.toString());
        }

        sb.delete(0, sb.length());

        rset.moveToFirst();
        for (int r = 0; r < nRow; r++) {
            for (int c = 0; c < nCol; c++) {
                sb.append(rset.getString(c)).append(',');
            }
            sb.append('\n');
            rset.moveToNext();
        }
        if (DbConst.DEBUG) {
            Log.d(TAG, sb.toString());
        }

        rset.close();
        db.close();
    }
}
