package felix.com.skydrop.receiver_temp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by fsoewito on 11/30/2015.
 *
 */
@SuppressLint("ParcelCreator")
public class AddressResultReceiver extends ResultReceiver {
    private Receiver mReceiver;

    public AddressResultReceiver(Handler handler) {
        super(handler);
    }

    public Receiver getReceiver() {
        return mReceiver;
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
       if (mReceiver != null){
           mReceiver.onReceiveResult(resultCode, resultData);
       }

    }

    public interface Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }
}
