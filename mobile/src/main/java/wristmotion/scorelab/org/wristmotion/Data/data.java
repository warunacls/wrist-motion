package wristmotion.scorelab.org.wristmotion.Data;

import io.realm.RealmObject;

/**
 * Created by wasn on 7/23/15.
 */
public class data extends RealmObject {

    private String androidDevice;
    private long timestamp;
    private int accuracy;

    private float x;
    private float y;
    private float z;

    private String datasource;
    private long datatype;

    public String getAndroidDevice() {
        return androidDevice;
    }
    public void setAndroidDevice(final String AndroidDevice) {
        androidDevice =AndroidDevice;
    }



    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(final long Timestamp) {
        timestamp = Timestamp;
    }



    public int getAccuracy() {
        return accuracy;
    }
    public void setAccuracy(final int Accuracy) {
        accuracy = Accuracy;
    }



    public String getDatasource() {
        return datasource;
    }
    public void setDatasource(final String Datasource) {
        datasource = Datasource;
    }



    public long getDatatype()
    {
        return datatype;
    }
    public void setDatatype(final long Datatype)
    {
        datatype = Datatype;
    }



    public float get_x() {
        return x;
    }
    public void set_x(final float x1) {
        x = x1;
    }




    public float get_y() {
        return y;
    }
    public void set_y(final float y1) {
        y = y1;
    }




    public float get_z()
    {
        return z;
    }
    public void set_z(final float z1) {
        z = z1;
    }

}
