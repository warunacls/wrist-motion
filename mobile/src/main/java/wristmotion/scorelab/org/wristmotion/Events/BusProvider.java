package wristmotion.scorelab.org.wristmotion.Events;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Created by wasn on 7/8/15.
 */
public class BusProvider {

    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

        public static void postOnMainThread(final Object event) {
                 Handler handler = new Handler(Looper.getMainLooper());

                       handler.post(new Runnable() {
                                public void run() {
                                           BUS.post(event);
                                        }
                              });
             }

                  private BusProvider() {
              }





































}
