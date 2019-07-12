package kr.syszone.t20.bmts.model;

public class MsgGetResponse {

    private Integer statusCode;
    private MsgData[] messages;

    public MsgGetResponse(MsgData[] messages) {
        this.statusCode = 200;
        this.messages = messages;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public MsgData[] getMessages() {
        return messages;
    }

    public void setMessages(MsgData[] messages) {
        this.messages = messages;
    }

}
