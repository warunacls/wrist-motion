package wristmotion.scorelab.org.wristmotion.Handler;

/**
 * Created by wasn on 7/17/15.
 */


import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;

import io.realm.Realm;
import wristmotion.scorelab.org.wristmotion.Data.data;
import wristmotion.scorelab.org.wristmotion.DataHandler.SensorDataLine;
import wristmotion.scorelab.org.wristmotion.DataHandler.Sensors;
import wristmotion.scorelab.org.wristmotion.Event.Event_BusProvider;
import wristmotion.scorelab.org.wristmotion.Event.Event_SensorBuild;
import wristmotion.scorelab.org.wristmotion.Event.Event_SensorRange;
import wristmotion.scorelab.org.wristmotion.Event.Event_SensorUpdate;
import wristmotion.scorelab.org.wristmotion.Event.Event_TagAdd;
import wristmotion.scorelab.org.wristmotion.Handler.SensorManager;

import android.R;



/**
 * Created by wasn on 7/8/15.
 */
public class SensorFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final int SENSOR_TOOGLES = 6;
    private long sensorId;
    private Sensors sensor;
    private SensorGraphView sensorgraphview;
    private float spread;




   private Realm realmDB;
    private  String AndroidID;



    private boolean[] sensorGraphs = new boolean[SENSOR_TOOGLES];



    public static SensorFragment newInstance(long sensorId) {
        SensorFragment fragment = new SensorFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, sensorId);
        fragment.setArguments(args);

        return fragment;
    }


    public SensorFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {

            sensorId = getArguments().getLong(ARG_PARAM1);
        }

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {




        sensor=SensorManager.getInstance(getActivity()).getSensor(sensorId);

        final View view = inflater.inflate(R.layout.fragment_layout, container, false);


        ((TextView) view.findViewById(R.id.title)).setText(sensor.getName());

        sensorgraphview = (SensorGraphView) view.findViewById(R.id.graphview);

        Resources res = getResources();

        view.findViewById(R.id.legend1).setBackgroundColor(res.getColor(R.color.graph_color_1));
        view.findViewById(R.id.legend2).setBackgroundColor(res.getColor(R.color.graph_color_2));
        view.findViewById(R.id.legend3).setBackgroundColor(res.getColor(R.color.graph_color_3));
        view.findViewById(R.id.legend4).setBackgroundColor(res.getColor(R.color.graph_color_4));
        view.findViewById(R.id.legend5).setBackgroundColor(res.getColor(R.color.graph_color_5));
        view.findViewById(R.id.legend6).setBackgroundColor(res.getColor(R.color.graph_color_6));


        String packageName = getActivity().getPackageName();
        for (int i = 0; i < SENSOR_TOOGLES; i++) {

            // Setting click listener for toggles
            int resourceId = res.getIdentifier("legend" + (i + 1) + "_container", "id", packageName);
            final int finalI = i;
            view.findViewById(resourceId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  sensorGraphs[finalI] = !sensorGraphs[finalI];
                    sensorgraphview.setDrawSensors(sensorGraphs);
                    v.setSelected(sensorGraphs[finalI]);
                }
            });


            view.findViewById(resourceId).setSelected(true);
            sensorGraphs[i] = true;
        }

        sensorgraphview.setDrawSensors(sensorGraphs);

        return view;
    }



    private void initialiseSensorData() {


        spread =sensor.getMaxValue() - sensor.getMinValue();

        LinkedList<SensorDataLine> dataRaw =sensor.getdataRaw();

        if (dataRaw == null || dataRaw.isEmpty()) {
            Log.w("sensor data", "no data found for sensor " + sensor.getID() + " " + sensor.getName());
            return;
        }





        ArrayList<Float>[] normalisedValues = new ArrayList[dataRaw.getFirst().getValues().length];
        ArrayList<Integer>[] accuracyValues = new ArrayList[dataRaw.getFirst().getValues().length];
        ArrayList<Long>[] timestampValues = new ArrayList[dataRaw.getFirst().getValues().length];


        for (int i = 0; i < normalisedValues.length; ++i) {
            normalisedValues[i] = new ArrayList<>();
            accuracyValues[i] = new ArrayList<>();
            timestampValues[i] = new ArrayList<>();
        }


        for (SensorDataLine dataPoint : dataRaw) {

            for (int i = 0; i < dataPoint.getValues().length; ++i) {
                float normalised = (dataPoint.getValues()[i] - sensor.getMinValue()) / spread;
                normalisedValues[i].add(normalised);
                accuracyValues[i].add(dataPoint.getAccuracy());
                timestampValues[i].add(dataPoint.getTimestamp());
            }
        }


        this.sensorgraphview.setNormalisedDataLines(normalisedValues, accuracyValues, timestampValues, SensorManager.getInstance(getActivity()).getTags());
        this.sensorgraphview.setZeroLine((0 - sensor.getMinValue()) / spread);

        this.sensorgraphview.setMaxValueLabel(MessageFormat.format("{0,number,#}", sensor.getMaxValue()));
        this.sensorgraphview.setMinValueLabel(MessageFormat.format("{0,number,#}", sensor.getMinValue()));



    }

    public void onResume() {
        super.onResume();
        initialiseSensorData();


         realmDB = Realm.getInstance(getActivity());
         AndroidID = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);


    }



    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Event_BusProvider.BusProvider.getInstance().register(this);
    }

    public void onDetach() {
        super.onDetach();
        Event_BusProvider.BusProvider.getInstance().unregister(this);
    }


    public void onEvent_TagAdd(Event_TagAdd event) {
        this.sensorgraphview.addNewTag(event.getTag());
    }



    public void onEvent_SensorUpdate(Event_SensorUpdate event) {
        if (event.getSensor().getID() == this.sensor.getID()) {

         realmDB.beginTransaction();

            data entry= realmDB.createObject(data.class);
            entry.setAndroidDevice(AndroidID);
            entry.setTimestamp(event.getDataPoint().getTimestamp());
            if (event.getDataPoint().getValues().length > 0) {
                entry.set_x(event.getDataPoint().getValues()[0]);
            } else {
                entry.set_x(0.0f);
            }

            if (event.getDataPoint().getValues().length > 1) {
                entry.set_y(event.getDataPoint().getValues()[1]);
            } else {
                entry.set_y(0.0f);
            }

            if (event.getDataPoint().getValues().length > 2) {
                entry.set_z(event.getDataPoint().getValues()[2]);
            } else {
                entry.set_z(0.0f);
            }

            entry.setAccuracy(event.getDataPoint().getAccuracy());
            entry.setDatasource("Acc");
            entry.setDatatype(event.getSensor().getID());
            realmDB.commitTransaction();



            for (int i = 0; i < event.getDataPoint().getValues().length; ++i) {
                float normalised = (event.getDataPoint().getValues()[i] - sensor.getMinValue()) / spread;
                this.sensorgraphview.addNewDataLine(normalised, event.getDataPoint().getAccuracy(), i, event.getDataPoint().getTimestamp());
            }


        }
    }
    public void onSensorRangeEvent(Event_SensorRange event) {
        if (event.getSensor().getID() == this.sensor.getID()) {
            initialiseSensorData();
        }
    }

}
