package kr.syszone.t20.bmts;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import kr.syszone.t20.bmts.control.MessageAdapter;
import kr.syszone.t20.bmts.db.DbApi;
import kr.syszone.t20.bmts.model.MsgData;
import kr.syszone.t20.bmts.settings.SettingsConst;
import kr.syszone.t20.bmts.task.MsgPostTask;

public class MessagesActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MessagesActivity";

    private static final String DB_QUERY = "SELECT * FROM messages ORDER BY ts_add";

    private ListView listView;
    private EditText edMsg;
    private ImageButton btnSend;

    private String tid;
    private SQLiteDatabase db;
    private MessageAdapter dbAdapter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_messages);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences shPref = PreferenceManager.getDefaultSharedPreferences(this);
        this.tid = shPref.getString(SettingsConst.KEY_PHONE_NUMBER, null);

        listView = (ListView) findViewById(R.id.dlg_m_list);
        edMsg = (EditText) findViewById(R.id.dlg_m_edit);
        btnSend = (ImageButton) findViewById(R.id.dlg_m_send);

        B2CSApp app = (B2CSApp) getApplication();
        DbApi dbh = app.getDbHandle();
        db = dbh.getHandleRead();

        Cursor rset = db.rawQuery(DB_QUERY, null);

        dbAdapter = new MessageAdapter(this, rset);
        listView.setAdapter(dbAdapter);

        btnSend.setOnClickListener(this);
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

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart!!!!!!!!!!!!!");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        scrollToLast();

        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop!!!!!!!!!!!!!");

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (dbAdapter != null) {
            dbAdapter.getCursor().close();
            dbAdapter = null;
        }

        if (db != null) {
            db.close();
            db = null;
        }

        Log.i(TAG, "onDestroy!!!!!!!!!!!!!");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(TAG, "onNewIntent!!!!!!!!!!!!!");
        refreshData();
        super.onNewIntent(intent);
    }

    @Override
    public void onClick(View view) {
        int vId = view.getId();
        switch (vId) {
            case R.id.dlg_m_send:
                postMsg();
                break;
            default:
                break;
        }
    }

    private void postMsg() {
        String content = edMsg.getText().toString();

        if (tid == null || tid.length() == 0) {
            Toast.makeText(this, "전화번호 설정이 되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (content == null || content.length() == 0) {
            edMsg.requestFocus();
            Toast.makeText(this, "내용을 작성해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (db != null) {
            dbAdapter.getCursor().close();
            db.close();
            db = null;
        }

        //MsgData md = new MsgData(null, System.currentTimeMillis(), 1, tid, tid, content);
        MsgData md = new MsgData(null, System.currentTimeMillis(), 0, tid, "server", content);

        B2CSApp app = (B2CSApp) getApplication();
        DbApi dbh = app.getDbHandle();
        dbh.updateMsgData(md);

        db = dbh.getHandleRead();
        Cursor rset = db.rawQuery("SELECT * FROM messages ORDER BY ts_add", null);
        dbAdapter.changeCursor(rset);

        edMsg.setText("");
        hideSoftKeyboard();

        new MsgPostTask(this, md).execute();

        scrollToLast();
    }

    private void refreshData() {
        if (db != null) {
            dbAdapter.getCursor().close();
            db.close();
            db = null;
        }

        B2CSApp app = (B2CSApp) getApplication();
        DbApi dbh = app.getDbHandle();

        db = dbh.getHandleRead();
        Cursor rset = db.rawQuery("SELECT * FROM messages ORDER BY ts_add", null);
        dbAdapter.changeCursor(rset);
    }

    private void scrollToLast() {
        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView.setSelection(dbAdapter.getCount() - 1);
            }
        }, 500);
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View focus = getCurrentFocus();
        if (focus != null) {
            imm.hideSoftInputFromWindow(focus.getWindowToken(), 0);
        }
    }
}
