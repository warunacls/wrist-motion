import android.app.Fragment;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import wristmotion.scorelab.org.wristmotion.R;

/**
 * Created by wasn on 7/8/15.
 */
public class SensorFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private long sensorId;
    private Sensor sensor;
    private SensorGraphView sensorgraphview;
    private float spread;

    private boolean[] drawSensors = new boolean[6];


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

    public View OnCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sensor = SensorManager.getInstance(getActivity()).getSensor(sensorId);


        final View view = inflater.inflate(R.layout.fragment_sensor, container, false);


        ((TextView) view.findViewById(R.id.title)).setText(sensor.getName());

        sensorview = (SensorGraphView) view.findViewById(R.id.graph_view);

        Resources res = getResources();

        view.findViewById(R.id.legend1).setBackgroundColor(res.getColor(R.color.graph_color_1));
        view.findViewById(R.id.legend2).setBackgroundColor(res.getColor(R.color.graph_color_2));
        view.findViewById(R.id.legend3).setBackgroundColor(res.getColor(R.color.graph_color_3));
        view.findViewById(R.id.legend4).setBackgroundColor(res.getColor(R.color.graph_color_4));
        view.findViewById(R.id.legend5).setBackgroundColor(res.getColor(R.color.graph_color_5));
        view.findViewById(R.id.legend6).setBackgroundColor(res.getColor(R.color.graph_color_6));

    }


















}
