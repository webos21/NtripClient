package kr.syszone.t20.bmts.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class MovData implements Parcelable {

    private Long id;
    private Date timeStamp;
    private String path;
    private GpsData gps;

    public MovData() {
    }

    public MovData(Long id, Long ts, String path, GpsData gps) {
        this.id = id;
        this.timeStamp = new Date(ts);
        this.path = path;
        this.gps = gps;
    }

    public MovData(MovData src) {
        this.id = src.getId();
        this.timeStamp = src.getTimeStamp();
        this.path = src.getPath();
        this.gps = src.getGps();
    }

    protected MovData(Parcel in) {
        readFromParcel(in);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public GpsData getGps() {
        return gps;
    }

    public void setGps(GpsData gps) {
        this.gps = gps;
    }

    public String toJSON(boolean withId) {
        StringBuilder sb = new StringBuilder();

        sb.append('{').append('\n');
        if (withId) {
            sb.append("  \"id\" : ").append(id).append(',').append('\n');
        }
        sb.append("  \"time_stamp\" : ").append(timeStamp.getTime()).append(',').append('\n');
        sb.append("  \"path\" : \"").append(path).append('\"').append(',').append('\n');
        if (gps == null) {
            sb.append("  \"gps\" : ").append(gps).append('\n');
        } else {
            sb.append("  \"gps\" : ").append(gps.toJSON(withId)).append('\n');
        }
        sb.append('}').append('\n');

        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong((id == null ? 0L : id));
        parcel.writeLong(timeStamp.getTime());
        parcel.writeString(path);
        parcel.writeParcelable(gps, i);
    }

    private void readFromParcel(Parcel in) {
        id = in.readLong();
        timeStamp = new Date(in.readLong());
        path = in.readString();
        gps = in.readParcelable(GpsData.class.getClassLoader());
    }

    public static final Creator<MovData> CREATOR = new Creator<MovData>() {
        @Override
        public MovData createFromParcel(Parcel in) {
            return new MovData(in);
        }

        @Override
        public MovData[] newArray(int size) {
            return new MovData[size];
        }
    };

}
