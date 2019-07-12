package kr.syszone.t20.bmts.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class MsgData implements Parcelable, Comparable<MsgData> {

    public static final Creator<MsgData> CREATOR = new Creator<MsgData>() {
        @Override
        public MsgData createFromParcel(Parcel in) {
            return new MsgData(in);
        }

        @Override
        public MsgData[] newArray(int size) {
            return new MsgData[size];
        }
    };
    private Long id;
    private Date timeStamp;
    private Integer type;
    private String sender;
    private String receiver;
    private String content;

    public MsgData() {
    }

    public MsgData(Long id, Long ts, Integer type, String sender, String receiver, String content) {
        this.id = id;
        this.timeStamp = new Date(ts);
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    public MsgData(MsgData src) {
        this.id = src.getId();
        this.timeStamp = src.getTimeStamp();
        this.type = src.getType();
        this.sender = src.getSender();
        this.receiver = src.getReceiver();
        this.content = src.getContent();
    }

    protected MsgData(Parcel in) {
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String toJSON(boolean withId) {
        StringBuilder sb = new StringBuilder();

        sb.append('{').append('\n');
        if (withId) {
            sb.append("  \"id\" : ").append(id).append(',').append('\n');
        }
        sb.append("  \"time_stamp\" : ").append(timeStamp.getTime()).append(',').append('\n');
        sb.append("  \"type\" : ").append(type).append(',').append('\n');
        sb.append("  \"sender\" : \"").append(sender).append('\"').append(',').append('\n');
        sb.append("  \"receiver\" : \"").append(receiver).append('\"').append(',').append('\n');
        sb.append("  \"content\" : \"").append(content).append('\"').append('\n');
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
        parcel.writeInt(type);
        parcel.writeString(sender);
        parcel.writeString(receiver);
        parcel.writeString(content);
    }

    private void readFromParcel(Parcel in) {
        id = in.readLong();
        timeStamp = new Date(in.readLong());
        type = in.readInt();
        sender = in.readString();
        receiver = in.readString();
        content = in.readString();
    }

    @Override
    public int compareTo(MsgData msgData) {
        if (msgData == null || msgData.getTimeStamp() == null) {
            return -1;
        }
        long myts = (timeStamp == null) ? -1 : timeStamp.getTime();
        long mdts = msgData.getTimeStamp().getTime();

        return (int) (myts - mdts);
    }
}
