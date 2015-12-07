package felix.com.skydrop.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import felix.com.skydrop.R;
import felix.com.skydrop.constant.SettingConstant;
import felix.com.skydrop.model.SettingData;
import felix.com.skydrop.model.SettingElement;
import felix.com.skydrop.viewholder.MyViewHolder;

/**
 * Created by fsoewito on 12/7/2015.
 */
public class SettingsAdapter extends RecyclerView.Adapter<MyViewHolder<SettingElement>> {
    private static final int EDITABLE = 0;
    private static final int PASSIVE = 1;

    SettingElement[] mSettingElements;
    SettingData mSettingData;

    public SettingsAdapter(SettingData settingData) {
        mSettingData = settingData;
        mSettingElements = createSettingElement(mSettingData);
    }

    @Override
    public int getItemViewType(int position) {
        if (mSettingElements[position].isEditable()) {
            return EDITABLE;
        } else {
            return PASSIVE;
        }
    }

    @Override
    public MyViewHolder<SettingElement> onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == EDITABLE) {
            View itemView = inflater.inflate(R.layout.item_setting_layout_boolean, parent, false);
            return new ActiveViewHolder(itemView);
        } else {
            View itemView = inflater.inflate(R.layout.item_setting_layout_boolean, parent, false);
            return new PassiveViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder<SettingElement> holder, int position) {
        holder.bindViewHolder(mSettingElements[position], position);
    }

    @Override
    public int getItemCount() {
        return SettingConstant.N_SETTING;
    }

    private SettingElement[] createSettingElement(SettingData settingData) {
        SettingElement[] elements = new SettingElement[SettingConstant.N_SETTING];
        for (int i = 0; i < elements.length; i++) {
            SettingElement element = new SettingElement();
            switch (i) {
                case 0:
                    element.setTitle("Temperature Unit");
                    element.setDescriptionTrue("Unit is in Celsius");
                    element.setDescriptionFalse("Unit is in Fahrenheit");
                    element.setValue(settingData.isTemperatureUnit());
                    break;
                case 1:
                    element.setTitle("Wind Speed Unit");
                    element.setDescriptionTrue("Unit is in mps");
                    element.setDescriptionFalse("Unit is in mph");
                    element.setValue(settingData.isWindUnit());
                case 2:
                    element.setTitle("Pressure Unit");
                    element.setDescriptionTrue("Unit is in mbar");
                    element.setDescriptionFalse("Unit is in atm");
                    element.setValue(settingData.isPressureUnit());
                    break;
                case 3:
                    element.setTitle("Auto Update");
                    element.setDescriptionTrue("Always update");
                    element.setDescriptionFalse("Manually update");
                    element.setValue(settingData.isAutoUpdate());
                    break;
                case 4:
                    element.setTitle("Pro Version");
                    element.setDescriptionTrue("Purchased already");
                    element.setDescriptionFalse("Remove ads");
                    element.setValue(settingData.isPaidVersion());
                    element.setEditable(false);
                    break;
                case 5:
                    element.setTitle("Version");
                    element.setDescriptionTrue("1.4");
                    element.setDescriptionFalse("1.4");
                    element.setEditable(false);
                    break;
                default:
                    break;
            }
            elements[i] = element;
        }
        return elements;
    }

    public class ActiveViewHolder extends MyViewHolder<SettingElement> {
        TextView mTitleLabel;
        TextView mDescriptionLabel;
        Switch mValueSwitch;

        public ActiveViewHolder(View itemView) {
            super(itemView);
            mTitleLabel = (TextView) itemView.findViewById(R.id.item_label_setting_title);
            mDescriptionLabel = (TextView) itemView.findViewById(R.id.item_label_setting_description);
            mValueSwitch = (Switch) itemView.findViewById(R.id.item_switch_value);
        }

        @Override
        public void bindViewHolder(SettingElement data, int position) {
            mTitleLabel.setText(data.getTitle());
            if (data.isValue()) {
                mDescriptionLabel.setText(data.getDescriptionTrue());
            } else {
                mDescriptionLabel.setText(data.getDescriptionFalse());
            }
            mValueSwitch.setChecked(data.isValue());
        }
    }

    public class PassiveViewHolder extends MyViewHolder<SettingElement> implements View.OnClickListener {
        TextView mTitleLabel;
        TextView mDescriptionLabel;
        Switch mValueSwitch;

        public PassiveViewHolder(View itemView) {
            super(itemView);
            mTitleLabel = (TextView) itemView.findViewById(R.id.item_label_setting_title);
            mDescriptionLabel = (TextView) itemView.findViewById(R.id.item_label_setting_description);
            mValueSwitch = (Switch) itemView.findViewById(R.id.item_switch_value);
        }

        @Override
        public void bindViewHolder(SettingElement data, int position) {
            mTitleLabel.setText(data.getTitle());
            if (data.isValue()) {
                mDescriptionLabel.setText(data.getDescriptionTrue());
            } else {
                mDescriptionLabel.setText(data.getDescriptionFalse());
            }
            mValueSwitch.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {
        }
    }

}
