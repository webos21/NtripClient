package kr.syszone.t20.bmts.tmap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.location.Location;
import android.support.design.widget.Snackbar;
import android.widget.TextView;

import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapLabelInfo;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import kr.syszone.t20.bmts.R;
import kr.syszone.t20.bmts.model.MapPoint;

public class TMapEventHandler {

    private static final SimpleDateFormat SDF_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private static final String CURRENT_MAKER_ID = "CURRENT_POSITION_MARKER";
    private static final String CURRENT_TRACKER_ID = "CURRENT_TRACKER_LINE";

    private static int mCircleID;

    ArrayList<String> mArrayCircleID;

    private Activity activity;
    private TMapView tmapView;
    private TextView infoView;

    private TMapMarkerItem currentMarker;
    private Bitmap makerIcon;
    private Location lastLocation;

    private TMapPolyLine trackerLine;

    private int m_nCurrentZoomLevel = 0;
    private boolean m_bShowMapIcon = false;
    private boolean m_bTrafficeMode = false;
    private boolean m_bSightVisible = false;
    private boolean m_bTrackingMode = false;

    public TMapEventHandler(Activity act, TMapView mapView, TextView infoView) {
        this.activity = act;
        this.tmapView = mapView;
        this.infoView = infoView;

        this.mArrayCircleID = new ArrayList<String>();


        this.currentMarker = new TMapMarkerItem();
        this.makerIcon = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_location_arrow);

        currentMarker.setID(CURRENT_MAKER_ID);
        currentMarker.setIcon(makerIcon);
        tmapView.addMarkerItem(CURRENT_MAKER_ID, currentMarker);


        trackerLine = new TMapPolyLine();
        trackerLine.setLineWidth(3);
        trackerLine.setLineColor(Color.BLUE);
        tmapView.addTMapPolyLine(CURRENT_TRACKER_ID, trackerLine);

        initView();
    }

    private void initView() {

        tmapView.setOnApiKeyListener(new TMapView.OnApiKeyListenerCallback() {
            @Override
            public void SKTMapApikeySucceed() {
                LogManager.printLog("MainActivity SKTMapApikeySucceed");
            }

            @Override
            public void SKTMapApikeyFailed(String errorMsg) {
                LogManager.printLog("MainActivity SKTMapApikeyFailed " + errorMsg);
            }
        });

        tmapView.setOnEnableScrollWithZoomLevelListener(new TMapView.OnEnableScrollWithZoomLevelCallback() {
            @Override
            public void onEnableScrollWithZoomLevelEvent(float zoom, TMapPoint centerPoint) {
                LogManager.printLog("MainActivity onEnableScrollWithZoomLevelEvent " + zoom + " " + centerPoint.getLatitude() + " " + centerPoint.getLongitude());
            }
        });

        tmapView.setOnDisableScrollWithZoomLevelListener(new TMapView.OnDisableScrollWithZoomLevelCallback() {
            @Override
            public void onDisableScrollWithZoomLevelEvent(float zoom, TMapPoint centerPoint) {
                LogManager.printLog("MainActivity onDisableScrollWithZoomLevelEvent " + zoom + " " + centerPoint.getLatitude() + " " + centerPoint.getLongitude());
            }
        });

        tmapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
            @Override
            public boolean onPressUpEvent(ArrayList<TMapMarkerItem> markerlist, ArrayList<TMapPOIItem> poilist, TMapPoint point, PointF pointf) {
                LogManager.printLog("MainActivity onPressUpEvent " + markerlist.size());
                return false;
            }

            @Override
            public boolean onPressEvent(ArrayList<TMapMarkerItem> markerlist, ArrayList<TMapPOIItem> poilist, TMapPoint point, PointF pointf) {
                LogManager.printLog("MainActivity onPressEvent " + markerlist.size());

                for (int i = 0; i < markerlist.size(); i++) {
                    TMapMarkerItem item = markerlist.get(i);
                    LogManager.printLog("MainActivity onPressEvent " + item.getName() + " " + item.getTMapPoint().getLatitude() + " " + item.getTMapPoint().getLongitude());
                }
                return false;
            }
        });

        tmapView.setOnLongClickListenerCallback(new TMapView.OnLongClickListenerCallback() {
            @Override
            public void onLongPressEvent(ArrayList<TMapMarkerItem> markerlist, ArrayList<TMapPOIItem> poilist, TMapPoint point) {
                LogManager.printLog("MainActivity onLongPressEvent " + markerlist.size());
                Snackbar.make(tmapView, "" + point.getLatitude() + ", " + point.getLongitude(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        tmapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem markerItem) {
                String strMessage = "ID: " + markerItem.getID() + " " + "Title " + markerItem.getCalloutTitle();
                DialogMessage.showAlertDialog(activity, "Callout Right Button", strMessage);
            }
        });

        tmapView.setOnClickReverseLabelListener(new TMapView.OnClickReverseLabelListenerCallback() {
            @Override
            public void onClickReverseLabelEvent(TMapLabelInfo findReverseLabel) {
                if (findReverseLabel != null && !"0".equals(findReverseLabel.id)) {
                    Snackbar.make(tmapView, findReverseLabel.labelName + "(" + findReverseLabel.labelLat + ", " + findReverseLabel.labelLon + ")", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        m_nCurrentZoomLevel = -1;
        m_bShowMapIcon = false;
        m_bTrafficeMode = false;
        m_bSightVisible = false;
        m_bTrackingMode = false;
    }

    public void addTMapCircle(double latitude, double longitude) {
        LogManager.printLog("addTMapCircle = " + longitude + ", " + latitude);

        TMapCircle circle = new TMapCircle();

        circle.setRadius(3);
        circle.setLineColor(Color.BLUE);
        circle.setCircleWidth(10.0F);
        circle.setRadiusVisible(false);

        TMapPoint point = new TMapPoint(latitude, longitude);
        circle.setCenterPoint(point);

        String strID = String.format(Locale.getDefault(), "circle%d", mCircleID++);
        tmapView.addTMapCircle(strID, circle);
        mArrayCircleID.add(strID);
    }

    public void removeTMapCircle() {
        if (mArrayCircleID.size() <= 0)
            return;

        String strCircleID = mArrayCircleID.get(mArrayCircleID.size() - 1);
        tmapView.removeTMapCircle(strCircleID);
        mArrayCircleID.remove(mArrayCircleID.size() - 1);
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setCurrentMarker(Location location) {
        lastLocation = location;

        String message = String.format(Locale.getDefault(), "[GPS]\n시간:%s\n취득: %s\n위도: %.07f\n경도: %.07f\n고도: %.02f\n정확: %.02f\n속도: %.02f\n방향: %.02f",
                SDF_FORMAT.format(location.getTime()), location.getProvider(), location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getAccuracy(), location.getSpeed(), location.getBearing());
        infoView.setText(message);

        TMapPoint tmPoint = new TMapPoint(location.getLatitude(), location.getLongitude());

        TMapMarkerItem marker = tmapView.getMarkerItemFromID(CURRENT_MAKER_ID);
        marker.setIcon(genRotatedMarkerIcon(location.getBearing()));
        marker.setTMapPoint(tmPoint);

        TMapPolyLine tracker = tmapView.getPolyLineFromID(CURRENT_TRACKER_ID);
        tracker.addLinePoint(tmPoint);

        tmapView.setCenterPoint(location.getLongitude(), location.getLatitude());
    }

    public ArrayList<TMapPoint> getLinePoints() {
        TMapPolyLine tracker = tmapView.getPolyLineFromID(CURRENT_TRACKER_ID);
        return tracker.getLinePoint();
    }

    public void setLinePoints(ArrayList<MapPoint> points) {
        TMapPolyLine tracker = tmapView.getPolyLineFromID(CURRENT_TRACKER_ID);
        for (MapPoint mp : points) {
            tracker.addLinePoint(new TMapPoint(mp.getLatitude(), mp.getLongitude()));
        }
    }


    private Bitmap genRotatedMarkerIcon(float rotate) {
        int w = makerIcon.getWidth();
        int h = makerIcon.getHeight();

        Matrix mtx = new Matrix();
        mtx.setRotate(rotate, w / 2, h / 2);

        return Bitmap.createBitmap(makerIcon, 0, 0, w, h, mtx, true);
    }

}
