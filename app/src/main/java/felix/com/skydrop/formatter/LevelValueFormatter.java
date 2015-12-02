package felix.com.skydrop.formatter;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by fsoewito on 12/2/2015.
 */
public class LevelValueFormatter implements ValueFormatter, YAxisValueFormatter {
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        int val = Math.round(value);
        if (val >= 0 && val < 49) {
            return "LOW";
        } else if (val > 48 && val < 52) {
            return "MEDIUM";
        } else if (val > 51 && val <= 100) {
            return "HIGH";
        } else {
            return "HIGH";
        }
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        int val = Math.round(value);
        if (val >= 0 && val < 20) {
            return "L";
        } else if (val > 700 && val < 800) {
            return "M";
        } else if (val > 1400 && val < 1600) {
            return "H";
        } else {
            return "M";
        }
    }
}
