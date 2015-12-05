package felix.com.skydrop.factory;

import android.content.SharedPreferences;

import felix.com.skydrop.constant.ApplicationDataConstant;
import felix.com.skydrop.model.ApplicationData;

/**
 * Created by fsoewito on 12/4/2015.
 */
public class ApplicationDataFactory {
    public static ApplicationData getInstance() {
        ApplicationData data = new ApplicationData();
        data.setInitialized(false);
        return data;
    }

    public static ApplicationData getInstance(SharedPreferences preferences) {
        if (preferences != null) {
            ApplicationData data = new ApplicationData();
            data.setInitialized(preferences.getBoolean(ApplicationDataConstant.INIT, false));
            data.setAddress(preferences.getString(ApplicationDataConstant.ADDRESS, "Location N/A"));
            return data;
        } else {
            return getInstance();
        }
    }
}
