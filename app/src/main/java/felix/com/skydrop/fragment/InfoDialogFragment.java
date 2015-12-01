package felix.com.skydrop.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import felix.com.skydrop.R;

/**
 * Created by fsoewito on 12/1/2015.
 */
public class InfoDialogFragment extends DialogFragment{
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.info_title)
                .setMessage(R.string.info_message).setPositiveButton("OK", null);
        return builder.create();
    }
}
