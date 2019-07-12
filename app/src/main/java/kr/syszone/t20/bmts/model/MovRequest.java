package kr.syszone.t20.bmts.model;

public class MovRequest {

    private String tid;
    private MovData movie;

    public MovRequest() {
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public MovData getMovie() {
        return movie;
    }

    public void setMovie(MovData movie) {
        this.movie = movie;
    }

    public String toJSON(boolean withId) {
        StringBuilder sb = new StringBuilder();

        sb.append('{').append('\n');
        sb.append("  \"tid\" : \"").append(tid).append('\"').append(',').append('\n');
        sb.append("  \"movie\" : ").append(movie.toJSON(withId)).append('\n');
        sb.append('}').append('\n');

        return sb.toString();
    }

}
