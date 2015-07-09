package wristmotion.scorelab.org.wristmotion.DataHandler;

import android.util.Log;

import java.util.LinkedList;

import wristmotion.scorelab.org.wristmotion.Event.Event_BusProvider;
import wristmotion.scorelab.org.wristmotion.Event.Event_SensorRange;

/**
 * Created by wasn on 7/20/15.
 */
public class Sensors {

    private static final String TAG = "Wristmotion/Sensor";
    private static final int MAX_DATA_POINTS = 1000;

    private long Sid;
    private String Sname;
   public float minValue = Integer.MIN_VALUE;
    public float maxValue = Integer.MAX_VALUE;

    private LinkedList<SensorDataLine> dataRaw = new LinkedList<>();


    public Sensors(int Sid, String Sname) {
                this.Sid = Sid;
                this.Sname = Sname;

           }

    public long getID() {
        return Sid;
    }






    public  String getName(){

        return Sname;
    }


    public float getMinValue(){

        return minValue;
    }


public float getMaxValue(){

    return maxValue;
}



    public synchronized LinkedList<SensorDataLine> addDataLine() {

               return (LinkedList<SensorDataLine>) dataRaw.clone();
            }


    public synchronized void addDataLine(SensorDataLine dataline){

                dataRaw.addLast(dataline);


        if (dataRaw.size() > MAX_DATA_POINTS) {
            dataRaw.removeFirst();
        }

        boolean newLimits = false;


        for (float value : dataline.getValues()) {
            if (value > maxValue) {
                maxValue = value;
                newLimits = true;
            }
            if (value < minValue) {
                minValue = value;
                newLimits = true;
            }
        }

        if (newLimits) {
            Log.d(TAG, "New range for sensor " + Sid + ": " + minValue + " - " + maxValue);

            Event_BusProvider.BusProvider.postOnMainThread(new Event_SensorRange(this));
        }
    }


    public LinkedList<SensorDataLine> getdataRaw() {
        return dataRaw;
    }


}
