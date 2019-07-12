package kr.syszone.t20.bmts.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class GpsData implements Parcelable {

    private Long id;
    private Date timeStamp;
    private Double latitude;
    private Double longitude;
    private Float altitude;
    private Float accuracy;
    private Float speed;
    private Float angle;

    public GpsData() {
    }

    public GpsData(Long id, Long ts, Double lat, Double lon, Float alt, Float acc, Float s, Float a) {
        this.id = id;
        this.timeStamp = new Date(ts);
        this.latitude = lat;
        this.longitude = lon;
        this.altitude = alt;
        this.accuracy = acc;
        this.speed = s;
        this.angle = a;
    }

    public GpsData(GpsData src) {
        this.id = src.getId();
        this.timeStamp = src.getTimeStamp();
        this.latitude = src.getLatitude();
        this.longitude = src.getLongitude();
        this.altitude = src.getAltitude();
        this.accuracy = src.getAccuracy();
        this.speed = src.getSpeed();
        this.angle = src.getAngle();
    }

    protected GpsData(Parcel in) {
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Float getAltitude() {
        return altitude;
    }

    public void setAltitude(Float altitude) {
        this.altitude = altitude;
    }

    public Float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Float accuracy) {
        this.accuracy = accuracy;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public Float getAngle() {
        return angle;
    }

    public void setAngle(Float angle) {
        this.angle = angle;
    }

    public String toJSON(boolean withId) {
        StringBuilder sb = new StringBuilder();

        sb.append('{').append('\n');
        if (withId) {
            sb.append("  \"id\" : ").append(id).append(',').append('\n');
        }
        sb.append("  \"time_stamp\" : ").append(timeStamp.getTime()).append(',').append('\n');
        sb.append("  \"latitude\" : ").append(latitude).append(',').append('\n');
        sb.append("  \"longitude\" : ").append(longitude).append(',').append('\n');
        sb.append("  \"altitude\" : ").append(altitude).append(',').append('\n');
        sb.append("  \"accuracy\" : ").append(accuracy).append(',').append('\n');
        sb.append("  \"speed\" : ").append(speed).append(',').append('\n');
        sb.append("  \"angle\" : ").append(angle).append('\n');
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
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeFloat(altitude);
        parcel.writeFloat(accuracy);
        parcel.writeFloat(speed);
        parcel.writeFloat(angle);
    }

    private void readFromParcel(Parcel in) {
        id = in.readLong();
        timeStamp = new Date(in.readLong());
        latitude = in.readDouble();
        longitude = in.readDouble();
        altitude = in.readFloat();
        accuracy = in.readFloat();
        speed = in.readFloat();
        angle = in.readFloat();
    }

    public static final Creator<GpsData> CREATOR = new Creator<GpsData>() {
        @Override
        public GpsData createFromParcel(Parcel in) {
            return new GpsData(in);
        }

        @Override
        public GpsData[] newArray(int size) {
            return new GpsData[size];
        }
    };

}
