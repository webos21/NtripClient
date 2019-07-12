package kr.syszone.t20.bmts.model;

public class PicRequest {

    private String tid;
    private PicData picture;

    public PicRequest() {
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public PicData getPicture() {
        return picture;
    }

    public void setPicture(PicData picture) {
        this.picture = picture;
    }

    public String toJSON(boolean withId) {
        StringBuilder sb = new StringBuilder();

        sb.append('{').append('\n');
        sb.append("  \"tid\" : \"").append(tid).append('\"').append(',').append('\n');
        sb.append("  \"picture\" : ").append(picture.toJSON(withId)).append('\n');
        sb.append('}').append('\n');

        return sb.toString();
    }

}
