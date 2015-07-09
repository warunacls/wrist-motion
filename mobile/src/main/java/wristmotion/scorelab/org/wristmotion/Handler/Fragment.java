package wristmotion.scorelab.org.wristmotion.Handler;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import wristmotion.scorelab.org.wristmotion.R;



/**
 * Created by wasn on 7/17/15.
 */
public class Fragment extends Activity{

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        if (savedInstanceState == null)
        {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new android.app.Fragment())
                    .commit();
        }

    }




    public static class AboutFragment extends Fragment {

        public AboutFragment() {
        }
    }






    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_layout, container, false);

        ((TextView) rootView.findViewById(R.id.open_source_content)).setText(Html.fromHtml(getResources().getString(R.string.open_source_content)));
        ((TextView) (TextView) rootView.findViewById(R.id.open_source_content)).setMovementMethod(LinkMovementMethod.getInstance());

        ((TextView) rootView.findViewById(R.id.about_github_content)).setText(Html.fromHtml(getResources().getString(R.string.about_github_content)));
        ((TextView) rootView.findViewById(R.id.about_github_content)).setMovementMethod(LinkMovementMethod.getInstance());


        ((TextView) rootView.findViewById(R.id.device_content)).setText(Html.fromHtml(getResources().getString(R.string.device_content)));
        ((TextView) rootView.findViewById(R.id.device_content)).setMovementMethod(LinkMovementMethod.getInstance());



        try
        {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            ((TextView) rootView.findViewById(R.id.header)).setText(getResources().getString(R.string.app_name) + " v." + pInfo.versionName);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }


        return rootView;
    }


}




