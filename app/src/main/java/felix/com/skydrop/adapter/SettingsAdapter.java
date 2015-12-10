package felix.com.skydrop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import felix.com.skydrop.R;
import felix.com.skydrop.constant.SettingConstant;
import felix.com.skydrop.model.SettingData;
import felix.com.skydrop.model.SettingElement;
import felix.com.skydrop.viewholder.MyViewHolder;

/**
 * Created by fsoewito on 12/7/2015.
 */
public class SettingsAdapter extends RecyclerView.Adapter<MyViewHolder<SettingElement>> {
    public static final String TAG = SettingsAdapter.class.getSimpleName();

    private static final int EDITABLE = 0;
    private static final int PASSIVE = 1;

    SettingElement[] mSettingElements;
    SettingData mSettingData;
    Context mContext;

    public SettingsAdapter(Context context, SettingData settingData) {
        mContext = context;
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
                    element.setDescriptionTrue("Unit is in kmh");
                    element.setDescriptionFalse("Unit is in mph");
                    element.setValue(settingData.isWindUnit());
                    break;
                case 2:
                    element.setTitle("Pressure Unit");
                    element.setDescriptionTrue("Unit is in bar");
                    element.setDescriptionFalse("Unit is in mmHg");
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
                    element.setTitle("Current Version");
                    element.setDescriptionTrue("v 1.5");
                    element.setDescriptionFalse("v 1.5");
                    element.setEditable(false);
                    break;
                default:
                    break;
            }
            elements[i] = element;
        }
        return elements;
    }

    public class ActiveViewHolder extends MyViewHolder<SettingElement> implements CompoundButton.OnCheckedChangeListener {
        TextView mTitleLabel;
        TextView mDescriptionLabel;
        Switch mValueSwitch;


        public ActiveViewHolder(View itemView) {
            super(itemView);
            mTitleLabel = (TextView) itemView.findViewById(R.id.item_label_setting_title);
            mDescriptionLabel = (TextView) itemView.findViewById(R.id.item_label_setting_description);
            mValueSwitch = (Switch) itemView.findViewById(R.id.item_switch_value);
            mValueSwitch.setOnCheckedChangeListener(this);

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

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int position = getLayoutPosition();
            switch (position) {
                case 0:
                    mSettingData.setTemperatureUnit(isChecked);
                    if (isChecked) {
                        mDescriptionLabel.setText(mSettingElements[position].getDescriptionTrue());
                    } else {
                        mDescriptionLabel.setText(mSettingElements[position].getDescriptionFalse());
                    }
                    break;
                case 1:
                    mSettingData.setWindUnit(isChecked);
                    if (isChecked) {
                        mDescriptionLabel.setText(mSettingElements[position].getDescriptionTrue());
                    } else {
                        mDescriptionLabel.setText(mSettingElements[position].getDescriptionFalse());
                    }
                    break;
                case 2:
                    mSettingData.setPressureUnit(isChecked);
                    if (isChecked) {
                        mDescriptionLabel.setText(mSettingElements[position].getDescriptionTrue());
                    } else {
                        mDescriptionLabel.setText(mSettingElements[position].getDescriptionFalse());
                    }
                    break;
                case 3:
                    mSettingData.setAutoUpdate(isChecked);
                    if (isChecked) {
                        mDescriptionLabel.setText(mSettingElements[position].getDescriptionTrue());
                    } else {
                        mDescriptionLabel.setText(mSettingElements[position].getDescriptionFalse());
                    }
                    break;
                default:
                    break;
            }
            mSettingData.toString();
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

            itemView.setOnClickListener(this);
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
            Toast.makeText(mContext, "pressed", Toast.LENGTH_SHORT).show();
        }
    }

}
