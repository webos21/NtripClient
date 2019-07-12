package kr.syszone.t20.bmts.model;

public class MsgGetRequest {
    private String tid;

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String toJSON(boolean withId) {
        StringBuilder sb = new StringBuilder();

        sb.append('{').append('\n');
        sb.append("  \"tid\" : \"").append(tid).append('\"').append('\n');
        sb.append('}').append('\n');

        return sb.toString();
    }

}
