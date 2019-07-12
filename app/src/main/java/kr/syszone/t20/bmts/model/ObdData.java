package kr.syszone.t20.bmts.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class ObdData implements Parcelable {

    private Long id;
    private Date timeStamp;
    private Integer rpm;
    private Float speed;
    private Integer brake;
    private Integer throttle;
    private String dtc;
    private Float accDistance;
    private Float efficiency;
    private Float batteryVolt;
    private Float tempEngOil;
    private Float tempCoolant;
    private Float tempInduct;
    private Float tempAmbient;
    private Float maf;
    private Float amp;

    public ObdData() {
    }

    public ObdData(Long id, Long ts, Integer rpm, Float speed, Integer brake, Integer throttle, String dtc, Float accd, Float eff, Float bat, Float teoil, Float tc, Float ti, Float ta, Float maf, Float amp) {
        this.id = id;
        this.timeStamp = new Date(ts);
        this.rpm = rpm;
        this.speed = speed;
        this.brake = brake;
        this.throttle = throttle;
        this.dtc = dtc;
        this.accDistance = accd;
        this.efficiency = eff;
        this.batteryVolt = bat;
        this.tempEngOil = teoil;
        this.tempCoolant = tc;
        this.tempInduct = ti;
        this.tempAmbient = ta;
        this.maf = maf;
        this.amp = amp;
    }

    public ObdData(ObdData src) {
        this.id = src.getId();
        this.timeStamp = src.getTimeStamp();
        this.rpm = src.getRpm();
        this.speed = src.getSpeed();
        this.brake = src.getBrake();
        this.throttle = src.getThrottle();
        this.dtc = src.getDtc();
        this.accDistance = src.getAccDistance();
        this.efficiency = src.getEfficiency();
        this.batteryVolt = src.getBatteryVolt();
        this.tempEngOil = src.getTempEngOil();
        this.tempCoolant = src.getTempCoolant();
        this.tempInduct = src.getTempInduct();
        this.tempAmbient = src.getTempAmbient();
        this.maf = src.getMaf();
        this.amp = src.getAmp();
    }

    protected ObdData(Parcel in) {
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

    public Integer getRpm() {
        return rpm;
    }

    public void setRpm(Integer rpm) {
        this.rpm = rpm;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public Integer getBrake() {
        return brake;
    }

    public void setBrake(Integer brake) {
        this.brake = brake;
    }

    public Integer getThrottle() {
        return throttle;
    }

    public void setThrottle(Integer throttle) {
        this.throttle = throttle;
    }

    public String getDtc() {
        return dtc;
    }

    public void setDtc(String dtc) {
        this.dtc = dtc;
    }

    public Float getAccDistance() {
        return accDistance;
    }

    public void setAccDistance(Float accDistance) {
        this.accDistance = accDistance;
    }

    public Float getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(Float efficiency) {
        this.efficiency = efficiency;
    }

    public Float getBatteryVolt() {
        return batteryVolt;
    }

    public void setBatteryVolt(Float batteryVolt) {
        this.batteryVolt = batteryVolt;
    }

    public Float getTempEngOil() {
        return tempEngOil;
    }

    public void setTempEngOil(Float tempEngOil) {
        this.tempEngOil = tempEngOil;
    }

    public Float getTempCoolant() {
        return tempCoolant;
    }

    public void setTempCoolant(Float tempCoolant) {
        this.tempCoolant = tempCoolant;
    }

    public Float getTempInduct() {
        return tempInduct;
    }

    public void setTempInduct(Float tempInduct) {
        this.tempInduct = tempInduct;
    }

    public Float getTempAmbient() {
        return tempAmbient;
    }

    public void setTempAmbient(Float tempAmbient) {
        this.tempAmbient = tempAmbient;
    }

    public Float getMaf() {
        return maf;
    }

    public void setMaf(Float maf) {
        this.maf = maf;
    }

    public Float getAmp() {
        return amp;
    }

    public void setAmp(Float amp) {
        this.amp = amp;
    }

    public String toJSON(boolean withId) {
        StringBuilder sb = new StringBuilder();

        sb.append('{').append('\n');
        if (withId) {
            sb.append("  \"id\" : ").append(id).append(',').append('\n');
        }
        sb.append("  \"time_stamp\" : ").append(timeStamp.getTime()).append(',').append('\n');
        sb.append("  \"rpm\" : ").append(rpm).append(',').append('\n');
        sb.append("  \"speed\" : ").append(speed).append(',').append('\n');
        sb.append("  \"brake\" : ").append(brake).append(',').append('\n');
        sb.append("  \"throttle\" : ").append(throttle).append(',').append('\n');
        sb.append("  \"dtc\" : \"").append(dtc).append('\"').append(',').append('\n');
        sb.append("  \"acc_distance\" : ").append(accDistance).append(',').append('\n');
        sb.append("  \"efficiency\" : ").append(efficiency).append(',').append('\n');
        sb.append("  \"battery_volt\" : ").append(batteryVolt).append(',').append('\n');
        sb.append("  \"temp_eng_oil\" : ").append(tempEngOil).append(',').append('\n');
        sb.append("  \"temp_coolant\" : ").append(tempCoolant).append(',').append('\n');
        sb.append("  \"temp_induct\" : ").append(tempInduct).append(',').append('\n');
        sb.append("  \"temp_ambient\" : ").append(tempAmbient).append(',').append('\n');
        sb.append("  \"maf\" : ").append(maf).append(',').append('\n');
        sb.append("  \"amp\" : ").append(amp).append('\n');
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
        parcel.writeInt(rpm);
        parcel.writeFloat(speed);
        parcel.writeInt(brake);
        parcel.writeInt(throttle);
        parcel.writeString(dtc);
        parcel.writeFloat(accDistance);
        parcel.writeFloat(efficiency);
        parcel.writeFloat(batteryVolt);
        parcel.writeFloat(tempEngOil);
        parcel.writeFloat(tempCoolant);
        parcel.writeFloat(tempInduct);
        parcel.writeFloat(tempAmbient);
        parcel.writeFloat(maf);
        parcel.writeFloat(amp);
    }

    private void readFromParcel(Parcel in) {
        id = in.readLong();
        timeStamp = new Date(in.readLong());
        rpm = in.readInt();
        speed = in.readFloat();
        brake = in.readInt();
        throttle = in.readInt();
        dtc = in.readString();
        accDistance = in.readFloat();
        efficiency = in.readFloat();
        batteryVolt = in.readFloat();
        tempEngOil = in.readFloat();
        tempCoolant = in.readFloat();
        tempInduct = in.readFloat();
        tempAmbient = in.readFloat();
        maf = in.readFloat();
        amp = in.readFloat();
    }

    public static final Creator<ObdData> CREATOR = new Creator<ObdData>() {
        @Override
        public ObdData createFromParcel(Parcel in) {
            return new ObdData(in);
        }

        @Override
        public ObdData[] newArray(int size) {
            return new ObdData[size];
        }
    };

}
