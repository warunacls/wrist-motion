package wristmotion.scorelab.org.wristmotion.Event;

import wristmotion.scorelab.org.wristmotion.DataHandler.Sensors;

/**
 * Created by wasn on 7/22/15.
 */
public class Event_SensorRange {


    private Sensors sensor;

    public Event_SensorRange(Sensors sensor) {

        this.sensor= sensor;
    }


    public Sensors getSensor() {

        return sensor;
    }


}
