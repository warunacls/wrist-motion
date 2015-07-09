package wristmotion.scorelab.org.wristmotion.Event;

import wristmotion.scorelab.org.wristmotion.DataHandler.TagData;

/**
 * Created by wasn on 7/22/15.
 */
public class Event_TagAdd {

    private TagData tagData;

    public Event_TagAdd(TagData tagdata) {
        tagData = tagdata;
    }

    public TagData getTag() {
        return tagData;
    }
}
