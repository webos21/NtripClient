package kr.syszone.t20.bmts.model;

public class MsgPostRequest {

    private String tid;
    private MsgData message;

    public MsgPostRequest() {
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public MsgData getMessage() {
        return message;
    }

    public void setMessage(MsgData message) {
        this.message = message;
    }

    public String toJSON(boolean withId) {
        StringBuilder sb = new StringBuilder();

        sb.append('{').append('\n');
        sb.append("  \"tid\" : \"").append(tid).append('\"').append(',').append('\n');
        if (message == null) {
            sb.append("  \"message\" : null").append('\n');
        } else {
            sb.append("  \"message\" : ").append(message.toJSON(withId)).append('\n');
        }
        sb.append('}').append('\n');

        return sb.toString();
    }

}
