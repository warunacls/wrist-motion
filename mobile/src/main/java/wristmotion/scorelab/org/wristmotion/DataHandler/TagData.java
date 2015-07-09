package wristmotion.scorelab.org.wristmotion.DataHandler;

/**
 * Created by wasn on 7/20/15.
 */
public class TagData {

    private String Tname;
    private long timestamp;

    public TagData(String TagName, long Timestamp) {
       Tname = TagName;
        timestamp = Timestamp;
    }

    public String getTagName() {
        return Tname;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
