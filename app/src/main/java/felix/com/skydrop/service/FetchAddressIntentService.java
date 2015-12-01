package felix.com.skydrop.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import felix.com.skydrop.constant.GeocoderConstant;

/**
 * Created by fsoewito on 11/30/2015.
 *
 */
public class FetchAddressIntentService extends IntentService {
    private static final String TAG = FetchAddressIntentService.class.getSimpleName();

    protected ResultReceiver mReceiver;

    public FetchAddressIntentService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMsg = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        mReceiver = intent.getParcelableExtra(GeocoderConstant.RECEIVER);
        Location location = intent.getParcelableExtra(GeocoderConstant.LOCATION_DATA_EXTRA);
        List<Address> addresses = null;
        try{
            if (location != null) {
                addresses = geocoder.getFromLocation(
                        location.getLatitude(), location.getLongitude(),
                        1);
            }else{
                addresses = null;
            }

        } catch (IOException e) {
            errorMsg = "Geocoder service error";
            Log.e(TAG, errorMsg);
        }


        if (addresses == null || addresses.size() <1){
            if (errorMsg.equals("")){
                errorMsg = "address not found";
                Log.e(TAG, errorMsg);
            }
            deliverResultToReceiver(GeocoderConstant.FAILURE_RESULT, errorMsg);
        }else{
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();

            for (int i = 0; i< address.getMaxAddressLineIndex(); i++){
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG, "address found");
            deliverResultToReceiver(GeocoderConstant.SUCCESS_RESULT,
                    address.getLocality());
        }

    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(GeocoderConstant.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }


}
