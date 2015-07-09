package wristmotion.scorelab.org.wristmotion.Event;

import wristmotion.scorelab.org.wristmotion.DataHandler.SensorDataLine;
import wristmotion.scorelab.org.wristmotion.DataHandler.Sensors;

/**
 * Created by wasn on 7/23/15.
 */
public class Event_SensorUpdate {

    private Sensors sensor;
    private SensorDataLine sensorDataLine;


    public Event_SensorUpdate(Sensors sensor, SensorDataLine sensorDataLine) {
        this.sensor = sensor;
        this.sensorDataLine = sensorDataLine;
    }


    public Sensors getSensor() {
        return sensor;
    }

    public SensorDataLine getDataPoint() {
        return sensorDataLine;
    }
}
