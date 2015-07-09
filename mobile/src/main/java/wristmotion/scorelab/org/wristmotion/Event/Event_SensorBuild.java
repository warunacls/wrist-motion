package wristmotion.scorelab.org.wristmotion.Event;



import wristmotion.scorelab.org.wristmotion.DataHandler.Sensors;

/**
 * Created by wasn on 7/28/15.
 */
public class Event_SensorBuild {
    private Sensors sensor;

    public Event_SensorBuild(Sensors sensor) {
        this.sensor = sensor;
    }

    public Sensors getSensor() {
        return sensor;
    }
}
