package kr.syszone.t20.bmts.model;

public class SensorsRequest {

    private String tid;

    private GpsData[] gps;

    private ObdData[] obd;

    private AmbData[] amb;

    public SensorsRequest() {
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public GpsData[] getGps() {
        return gps;
    }

    public void setGps(GpsData[] gps) {
        this.gps = gps;
    }

    public ObdData[] getObd() {
        return obd;
    }

    public void setObd(ObdData[] obd) {
        this.obd = obd;
    }

    public AmbData[] getAmb() {
        return amb;
    }

    public void setAmb(AmbData[] amb) {
        this.amb = amb;
    }

    public String toJSON(boolean withId) {
        StringBuilder sb = new StringBuilder();

        sb.append('{').append('\n');
        sb.append("  \"tid\" : \"").append(tid).append('\"').append(',').append('\n');
        if (gps == null || gps.length == 0) {
            sb.append("  \"gps\" : [],").append('\n');
        } else {
            sb.append("  \"gps\" : [").append('\n');
            for (GpsData gd : gps) {
                sb.append(gd.toJSON(withId));
                sb.append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("          ],").append('\n');
        }

        if (obd == null || obd.length == 0) {
            sb.append("  \"obd\" : [],").append('\n');
        } else {
            sb.append("  \"obd\" : [").append('\n');
            for (ObdData od : obd) {
                sb.append(od.toJSON(withId));
                sb.append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("          ],").append('\n');
        }

        if (amb == null || amb.length == 0) {
            sb.append("  \"amb\" : []").append('\n');
        } else {
            sb.append("  \"amb\" : [").append('\n');
            for (AmbData ad : amb) {
                sb.append(ad.toJSON(withId));
                sb.append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("          ]").append('\n');
        }

        sb.append('}').append('\n');

        return sb.toString();
    }

}
