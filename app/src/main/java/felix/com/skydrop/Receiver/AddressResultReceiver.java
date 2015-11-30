package felix.com.skydrop.Receiver;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.widget.Toast;

import felix.com.skydrop.constant.GeocoderConstant;

/**
 * Created by fsoewito on 11/30/2015.
 */
@SuppressLint("ParcelCreator")
public class AddressResultReceiver extends ResultReceiver {
    private Receiver mReceiver;

    public AddressResultReceiver(Handler handler) {
        super(handler);
    }

    public interface Receiver{
        public void onReceiveResult(int resultCode, Bundle resultData);
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
}
