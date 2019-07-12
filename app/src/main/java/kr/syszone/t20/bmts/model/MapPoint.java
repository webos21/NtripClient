package kr.syszone.t20.bmts.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.skt.Tmap.TMapPoint;

public class MapPoint implements Parcelable {

    private double latitude;
    private double longitude;

    public MapPoint() {
    }

    public MapPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public MapPoint(MapPoint src) {
        this.latitude = src.getLatitude();
        this.longitude = src.getLongitude();
    }

    public MapPoint(TMapPoint tp) {
        this.latitude = tp.getLatitude();
        this.longitude = tp.getLongitude();
    }

    protected MapPoint(Parcel in) {
        readFromParcel(in);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }

    private void readFromParcel(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<MapPoint> CREATOR = new Creator<MapPoint>() {
        @Override
        public MapPoint createFromParcel(Parcel in) {
            return new MapPoint(in);
        }

        @Override
        public MapPoint[] newArray(int size) {
            return new MapPoint[size];
        }
    };

}
