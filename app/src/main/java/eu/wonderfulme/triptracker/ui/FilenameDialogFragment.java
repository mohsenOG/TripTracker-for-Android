package eu.wonderfulme.triptracker.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import eu.wonderfulme.triptracker.R;
import eu.wonderfulme.triptracker.database.LocationRepository;
import eu.wonderfulme.triptracker.utility.Utils;

public class FilenameDialogFragment extends DialogFragment {

    private static final String FILENAME_DIALOG_ITEM_KEY = "FILENAME_DIALOG_ITEM_KEY";


    private LocationRepository locationRepository;

    public static FilenameDialogFragment newInstance(int itemKey) {
        FilenameDialogFragment fragment = new FilenameDialogFragment();
        Bundle args = new Bundle();
        args.putInt(FILENAME_DIALOG_ITEM_KEY, itemKey);
        fragment.setArguments(args);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        locationRepository = new LocationRepository(getActivity().getApplication());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.filename_dialog_title);
        builder.setMessage(R.string.filename_dialog_message);

        int itemKey;
        if (getArguments() != null) {
            itemKey = getArguments().getInt(FILENAME_DIALOG_ITEM_KEY);
        } else {
           throw  new RuntimeException("OOOOH there is no itemKey in Filename dialog!");
        }

        final EditText input = new EditText(getActivity());
        String defaultFilename = Utils.getFormattedTime(System.currentTimeMillis());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setText(defaultFilename);
        input.setSelectAllOnFocus(true);
        input.requestFocus();

        builder.setView(input);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);



        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String routeName = input.getText().toString();
                locationRepository.updateRouteName(itemKey, routeName);
                dialogInterface.dismiss();
            }
        });

        // Canceling filename.
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String routeName = input.getText().toString();
                locationRepository.updateRouteName(itemKey, routeName);
                dialogInterface.dismiss();
            }
        });

        return builder.create();
    }
}
