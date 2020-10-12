package kr.syszone.t20.bmts.widget;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Environment;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import kr.syszone.t20.bmts.R;
import kr.syszone.t20.bmts.model.GpsData;
import kr.syszone.t20.bmts.task.FtpUploadTask;

public class PreviewDialog extends AlertDialog implements DialogInterface,
        DialogInterface.OnShowListener {

    private static final SimpleDateFormat SDF_FNAME = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

    private Activity act;
    private ViewGroup thisView;
    private ImageView imgView;
    private Button btnOk;

    private Bitmap bitmap;
    private GpsData gpsData;

    public PreviewDialog(Activity act, Bitmap bitmap, Location l) {
        super(act);

        this.act = act;
        this.bitmap = bitmap;
        if (l != null) {
            this.gpsData = new GpsData(0L, l.getTime(), l.getLatitude(), l.getLongitude(), ((float) l.getAltitude()), l.getAccuracy(), l.getSpeed(), l.getBearing());
        }

        thisView = (ViewGroup) getLayoutInflater().inflate(R.layout.dialog_preview, null);
        imgView = (ImageView) thisView.findViewById(R.id.dlg_pv_img);
        imgView.setImageBitmap(bitmap);

        setTitle("미리보기");
        setView(thisView);
        setButton(DialogInterface.BUTTON_POSITIVE, "전송", (OnClickListener) null);
        setButton(DialogInterface.BUTTON_NEGATIVE, "취소", (OnClickListener) null);

        setOnShowListener(this);
    }

    @Override
    public void onShow(DialogInterface dialogInterface) {
        btnOk = getButton(DialogInterface.BUTTON_POSITIVE);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File f = storeTarget();
                BufferedOutputStream bos = null;
                try {
                    bos = new BufferedOutputStream(new FileOutputStream(f));
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                new FtpUploadTask(act, f, gpsData).execute();
                dismiss();
            }
        });
    }

    private File storeTarget() {
        String fileName = "P_" + SDF_FNAME.format(new Date()) + ".jpg";
        return new File(getStoreDir(), fileName);
    }


    private File getStoreDir() {
        File cacheDir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String mntPath = Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + act.getPackageName();
            cacheDir = new File(mntPath, "cache");
        } else {
            cacheDir = act.getCacheDir();
        }
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

}
