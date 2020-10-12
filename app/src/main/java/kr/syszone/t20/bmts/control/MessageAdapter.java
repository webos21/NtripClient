package kr.syszone.t20.bmts.control;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import androidx.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import kr.syszone.t20.bmts.R;
import kr.syszone.t20.bmts.settings.SettingsConst;

public class MessageAdapter extends CursorAdapter {

    private static final SimpleDateFormat SDF_TS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    private String phoneNo;

    public MessageAdapter(Context context, Cursor cursor) {
        super(context, cursor);

        // Initialize the SharedPreference
        SharedPreferences shPref = PreferenceManager.getDefaultSharedPreferences(context);
        phoneNo = shPref.getString(SettingsConst.KEY_PHONE_NUMBER, "KR01062053117");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.row_msg_dummy, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Extract properties from cursor
        Date ts = new Date(cursor.getLong(cursor.getColumnIndexOrThrow("ts_add")));
        int reqType = cursor.getInt(cursor.getColumnIndexOrThrow("req_type"));
        String receiver = cursor.getString(cursor.getColumnIndexOrThrow("receiver"));
        String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));

        TextView tvMsg = null;
        if (phoneNo.equals(receiver) && reqType == 0) {
            if (view.findViewById(R.id.rowMsgRecv) == null) {
                ViewGroup vg = (ViewGroup) view;
                vg.removeAllViews();
                LayoutInflater.from(context).inflate(R.layout.row_msg_recv, vg, true);
            }
        } else {
            if (view.findViewById(R.id.rowMsgSend) == null) {
                ViewGroup vg = (ViewGroup) view;
                vg.removeAllViews();
                LayoutInflater.from(context).inflate(R.layout.row_msg_send, vg, true);
            }
        }

        tvMsg = (TextView) view.findViewById(R.id.tv_msg);
        String msg = "[" + SDF_TS.format(ts) + "]\n" + content;

        // Populate fields with extracted properties
        tvMsg.setText(msg);
    }
}
