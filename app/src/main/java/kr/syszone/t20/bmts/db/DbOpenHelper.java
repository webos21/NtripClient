package kr.syszone.t20.bmts.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = DbOpenHelper.class.getSimpleName();

    private DbApi dbh;

    public DbOpenHelper(Context context) {
        super(context, DbConst.DB_NAME, null, DbConst.DB_VERSION);
    }

    public DbApi getHandler() {
        if (dbh == null) {
            dbh = new DbHandler(this);
        }
        return dbh;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (DbConst.DEBUG) {
            Log.d(TAG, "onCreate [" + db.getPath() + "]");
        }
        db.execSQL(DbConst.CREATE_TB_MESSAGES);
        db.execSQL(DbConst.CREATE_TB_GPS);
        db.execSQL(DbConst.CREATE_TB_OBD);
        db.execSQL(DbConst.CREATE_TB_AMB);
        // XXX Not Used
        //db.execSQL(CREATE_TB_IMU);
    }

    private void dropTables(SQLiteDatabase db) {
        db.execSQL(DbConst.DROP_TB_MESSAGES);
        db.execSQL(DbConst.DROP_TB_GPS);
        db.execSQL(DbConst.DROP_TB_OBD);
        db.execSQL(DbConst.DROP_TB_AMB);
        // XXX Not Used
        //db.execSQL(DROP_TB_IMU);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (DbConst.DEBUG) {
            Log.d(TAG, "onUpgrade [" + db.getPath() + "] oldVer = "
                    + oldVersion + ", newVer = " + newVersion);
        }
        if (oldVersion != newVersion) {
            dropTables(db);
            onCreate(db);
        }
    }
}
