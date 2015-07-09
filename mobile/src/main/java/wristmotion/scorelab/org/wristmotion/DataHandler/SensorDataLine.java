package wristmotion.scorelab.org.wristmotion.DataHandler;

/**
 * Created by wasn on 7/21/15.
 */
public class SensorDataLine {

    private long timestamp;
    private int accuracy;
    private float[] value;



    public SensorDataLine(long timestamp, int accuracy,  float[] value) {
        this.timestamp = timestamp;
        this.value = value;
    }


    public long getTimestamp() {

        return timestamp;
    }


    public int getAccuracy(){

        return accuracy;
    }

    public float[] getValues() {

        return value;
    }
}
