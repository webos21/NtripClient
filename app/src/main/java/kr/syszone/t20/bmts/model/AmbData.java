package kr.syszone.t20.bmts.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class AmbData implements Parcelable {

    private Long id;
    private Date timeStamp;
    private Float temperature;
    private Float humidity;

    public AmbData() {
    }

    public AmbData(Long id, Long ts, Float t, Float h) {
        this.id = id;
        this.timeStamp = new Date(ts);
        this.temperature = t;
        this.humidity = h;
    }

    public AmbData(AmbData src) {
        this.id = src.getId();
        this.timeStamp = src.getTimeStamp();
        this.temperature = src.getTemperature();
        this.humidity = src.getHumidity();
    }

    protected AmbData(Parcel in) {
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

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Float getHumidity() {
        return humidity;
    }

    public void setHumidity(Float humidity) {
        this.humidity = humidity;
    }

    public String toJSON(boolean withId) {
        StringBuilder sb = new StringBuilder();

        sb.append('{').append('\n');
        if (withId) {
            sb.append("  \"id\" : ").append(id).append(',').append('\n');
        }
        sb.append("  \"time_stamp\" : ").append(timeStamp.getTime()).append(',').append('\n');
        sb.append("  \"temperature\" : ").append(temperature).append(',').append('\n');
        sb.append("  \"humidity\" : ").append(humidity).append('\n');
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
        parcel.writeFloat(temperature);
        parcel.writeFloat(humidity);
    }

    private void readFromParcel(Parcel in) {
        id = in.readLong();
        timeStamp = new Date(in.readLong());
        temperature = in.readFloat();
        humidity = in.readFloat();
    }

    public static final Creator<AmbData> CREATOR = new Creator<AmbData>() {
        @Override
        public AmbData createFromParcel(Parcel in) {
            return new AmbData(in);
        }

        @Override
        public AmbData[] newArray(int size) {
            return new AmbData[size];
        }
    };

}
