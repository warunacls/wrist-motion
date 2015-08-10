import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.Arrays;

/**
 * Created by wasn on 7/8/15.
 */
public class SensorTransmission extends WearableListenerService {

    private static final String TAG = "Wristmotion/SensorTransmission";


    public void onPeerConnected(Node peer) {
              super.onPeerConnected(peer);

                      Log.i(TAG, "Connected:" + peer.getDisplayName() + " (" + peer.getId() + ")");
           }


    public void onPeerDisconnected(Node peer) {
               super.onPeerDisconnected(peer);

                     Log.i(TAG, "Disconnected: " + peer.getDisplayName() + " (" + peer.getId() + ")");
         }



    public void onDataChanged(DataEventBuffer dataEvents) {
               Log.d(TAG, "onDataChanged()");

                       for (DataEvent dataEvent : dataEvents) {
                     if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                           DataItem dataItem = dataEvent.getDataItem();

                         SensorData(Integer.parseInt(dataItem.getUri().
                                         getLastPathSegment()),
                                 DataMapItem.
                                         fromDataItem(dataItem).
                                         getDataMap());
                         }
                    }
          }

    private void SensorData(int sensorType, DataMap dataMap){
               int accuracy = dataMap.getInt(DataMapKeys.ACCURACY);
               long timestamp = dataMap.getLong(DataMapKeys.TIMESTAMP);
               float[] values = dataMap.getFloatArray(DataMapKeys.VALUES);

                       Log.d(TAG, "Received sensor data " + sensorType + " = " + Arrays.toString(values));

                       Toast.makeText(this, "Received sensor data " + sensorType + " = " + Arrays.toString(values), Toast.LENGTH_SHORT).show();
            }



}
