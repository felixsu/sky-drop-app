package felix.com.skydrop.factory;

import android.content.SharedPreferences;

import felix.com.skydrop.constant.SettingConstant;
import felix.com.skydrop.model.SettingData;

/**
 * Created by fsoewito on 12/5/2015.
 */
public class SettingDataFactory {
    public static SettingData getInstance() {
        SettingData data = new SettingData();
        data.setUnit(SettingData.UNIT_CELSIUS);
        return data;
    }

    public static SettingData getInstance(SharedPreferences preferences) {
        if (preferences != null) {
            SettingData data = new SettingData();
            data.setUnit(preferences.getInt(SettingConstant.TEMP_UNIT, SettingData.UNIT_CELSIUS));
            return data;
        } else {
            return getInstance();
        }
    }
}
