package kr.syszone.t20.bmts.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class ImuData implements Parcelable {

    private Long id;
    private Date timeStamp;
    private Float accX;
    private Float accY;
    private Float accZ;
    private Float gyroX;
    private Float gyroY;
    private Float gyroZ;

    public ImuData() {
    }

    public ImuData(Long id, Long ts, Float ax, Float ay, Float az, Float gx, Float gy, Float gz) {
        this.id = id;
        this.timeStamp = new Date(ts);
        this.accX = ax;
        this.accY = ay;
        this.accZ = az;
        this.gyroX = gx;
        this.gyroY = gy;
        this.gyroZ = gz;
    }

    public ImuData(ImuData src) {
        this.id = src.getId();
        this.timeStamp = src.getTimeStamp();
        this.accX = src.getAccX();
        this.accY = src.getAccY();
        this.accZ = src.getAccZ();
        this.gyroX = src.getGyroX();
        this.gyroY = src.getGyroY();
        this.gyroZ = src.getGyroZ();
    }

    protected ImuData(Parcel in) {
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

    public Float getAccX() {
        return accX;
    }

    public void setAccX(Float accX) {
        this.accX = accX;
    }

    public Float getAccY() {
        return accY;
    }

    public void setAccY(Float accY) {
        this.accY = accY;
    }

    public Float getAccZ() {
        return accZ;
    }

    public void setAccZ(Float accZ) {
        this.accZ = accZ;
    }

    public Float getGyroX() {
        return gyroX;
    }

    public void setGyroX(Float gyroX) {
        this.gyroX = gyroX;
    }

    public Float getGyroY() {
        return gyroY;
    }

    public void setGyroY(Float gyroY) {
        this.gyroY = gyroY;
    }

    public Float getGyroZ() {
        return gyroZ;
    }

    public void setGyroZ(Float gyroZ) {
        this.gyroZ = gyroZ;
    }

    public String toJSON(boolean withId) {
        StringBuilder sb = new StringBuilder();

        sb.append('{').append('\n');
        if (withId) {
            sb.append("  \"id\" : ").append(id).append(',').append('\n');
        }
        sb.append("  \"time_stamp\" : ").append(timeStamp.getTime()).append(',').append('\n');
        sb.append("  \"acc_x\" : ").append(accX).append(',').append('\n');
        sb.append("  \"acc_y\" : ").append(accY).append(',').append('\n');
        sb.append("  \"acc_z\" : ").append(accZ).append(',').append('\n');
        sb.append("  \"gyro_x\" : ").append(gyroX).append(',').append('\n');
        sb.append("  \"gyro_y\" : ").append(gyroY).append(',').append('\n');
        sb.append("  \"gyro_z\" : ").append(gyroZ).append('\n');
        sb.append('}').append('\n');

        return sb.toString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong((id == null ? 0L : id));
        parcel.writeLong(timeStamp.getTime());
        parcel.writeFloat(accX);
        parcel.writeFloat(accY);
        parcel.writeFloat(accZ);
        parcel.writeFloat(gyroX);
        parcel.writeFloat(gyroY);
        parcel.writeFloat(gyroZ);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private void readFromParcel(Parcel in) {
        id = in.readLong();
        timeStamp = new Date(in.readLong());
        accX = in.readFloat();
        accY = in.readFloat();
        accZ = in.readFloat();
        gyroX = in.readFloat();
        gyroY = in.readFloat();
        gyroZ = in.readFloat();
    }

    public static final Creator<ImuData> CREATOR = new Creator<ImuData>() {
        @Override
        public ImuData createFromParcel(Parcel in) {
            return new ImuData(in);
        }

        @Override
        public ImuData[] newArray(int size) {
            return new ImuData[size];
        }
    };

}
