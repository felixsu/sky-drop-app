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
        data.setTemperatureUnit(SettingConstant.UNIT_CELSIUS);
        data.setAutoUpdate(SettingConstant.UPDATE_MANUAL);
        data.setPaidVersion(SettingConstant.FREE);
        data.setPressureUnit(SettingConstant.UNIT_SI);
        data.setWindUnit(SettingConstant.UNIT_SI);
        return data;
    }

    public static SettingData getInstance(SharedPreferences preferences) {
        if (preferences != null) {
            SettingData data = new SettingData();
            data.setTemperatureUnit(preferences.getBoolean(SettingConstant.KEY_TEMP_UNIT, SettingConstant.UNIT_CELSIUS));
            data.setAutoUpdate(preferences.getBoolean(SettingConstant.KEY_UPDATE, SettingConstant.UNIT_CELSIUS));
            data.setPaidVersion(preferences.getBoolean(SettingConstant.KEY_PAID_VERSION, SettingConstant.UNIT_CELSIUS));
            data.setPressureUnit(preferences.getBoolean(SettingConstant.KEY_PRESSURE_UNIT, SettingConstant.UNIT_CELSIUS));
            data.setWindUnit(preferences.getBoolean(SettingConstant.KEY_WIND_UNIT, SettingConstant.UNIT_CELSIUS));
            return data;
        } else {
            return getInstance();
        }
    }
}
