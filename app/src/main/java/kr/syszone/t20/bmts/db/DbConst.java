package kr.syszone.t20.bmts.db;

class DbConst {

    static final boolean DEBUG = true;

    static final String DB_NAME = "BMTS_DB.db";
    static final int DB_VERSION = 7;

    static final String TB_MESSAGES = "messages";

    static final String TB_GPS = "gps";
    static final String TB_OBD = "obd";
    static final String TB_AMB = "amb";
    static final String TB_IMU = "imu";

    static final String CREATE_TB_MESSAGES =
    /* Indent */"CREATE TABLE IF NOT EXISTS " + DbConst.TB_MESSAGES + " (" +
	/* Indent */"	_id              INTEGER    PRIMARY KEY   AUTOINCREMENT, " +
	/* Indent */"	ts_add           INTEGER, " +
	/* Indent */"	req_type         INTEGER, " +
	/* Indent */"	sender           TEXT, " +
	/* Indent */"	receiver         TEXT, " +
	/* Indent */"	content          TEXT " +
	/* Indent */");";

    static final String CREATE_TB_GPS =
    /* Indent */"CREATE TABLE IF NOT EXISTS " + DbConst.TB_GPS + " (" +
    /* Indent */"	id               INTEGER    PRIMARY KEY   AUTOINCREMENT, " +
    /* Indent */"	ts_gether        INTEGER, " +
    /* Indent */"	latitude         REAL, " +
    /* Indent */"	longitude        REAL, " +
    /* Indent */"	altitude         REAL, " +
    /* Indent */"	accuracy         REAL, " +
    /* Indent */"	speed            REAL, " +
    /* Indent */"	angle            REAL" +
    /* Indent */");";

    static final String CREATE_TB_OBD =
    /* Indent */"CREATE TABLE IF NOT EXISTS " + DbConst.TB_OBD + " (" +
    /* Indent */"	id               INTEGER    PRIMARY KEY   AUTOINCREMENT, " +
    /* Indent */"	ts_gether        INTEGER, " +
    /* Indent */"	rpm              INTEGER, " +
	/* Indent */"	speed            REAL, " +
	/* Indent */"	brake            INTEGER, " +
	/* Indent */"	throttle         INTEGER, " +
	/* Indent */"	dtc              TEXT, " +
	/* Indent */"	acc_distance     REAL, " +
	/* Indent */"	efficiency       REAL, " +
	/* Indent */"	battery_volt     REAL, " +
	/* Indent */"	temp_eng_oil     REAL, " +
	/* Indent */"	temp_coolant     REAL, " +
	/* Indent */"	temp_induct      REAL, " +
	/* Indent */"	temp_ambient     REAL, " +
	/* Indent */"	maf              REAL, " +
	/* Indent */"	amp              REAL " +
	/* Indent */");";

    static final String CREATE_TB_AMB =
	/* Indent */"CREATE TABLE IF NOT EXISTS " + DbConst.TB_AMB + " (" +
	/* Indent */"	id               INTEGER    PRIMARY KEY   AUTOINCREMENT, " +
	/* Indent */"	ts_gether        INTEGER, " +
    /* Indent */"	temperature      REAL," +
	/* Indent */"	humidity         REAL " +
	/* Indent */");";

    static final String CREATE_TB_IMU =
	/* Indent */"CREATE TABLE IF NOT EXISTS " + DbConst.TB_IMU + " (" +
	/* Indent */"	id               INTEGER    PRIMARY KEY   AUTOINCREMENT, " +
	/* Indent */"	ts_gether        INTEGER, " +
	/* Indent */"	acc_x            REAL, " +
	/* Indent */"	acc_y            REAL, " +
	/* Indent */"	acc_z            REAL, " +
	/* Indent */"	gyro_x           REAL, " +
	/* Indent */"	gyro_y           REAL, " +
	/* Indent */"	gyro_z           REAL " +
	/* Indent */");";

    static final String DROP_TB_MESSAGES =
	/* Indent */"DROP TABLE IF EXISTS " + DbConst.TB_MESSAGES + ";";

    static final String DROP_TB_GPS =
	/* Indent */"DROP TABLE IF EXISTS " + DbConst.TB_GPS + ";";

    static final String DROP_TB_OBD =
	/* Indent */"DROP TABLE IF EXISTS " + DbConst.TB_OBD + ";";

    static final String DROP_TB_AMB =
	/* Indent */"DROP TABLE IF EXISTS " + DbConst.TB_AMB + ";";

    static final String DROP_TB_IMU =
	/* Indent */"DROP TABLE IF EXISTS " + DbConst.TB_IMU + ";";

}
