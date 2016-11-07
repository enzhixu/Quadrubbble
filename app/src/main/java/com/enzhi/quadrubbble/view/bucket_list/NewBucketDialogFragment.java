package com.enzhi.quadrubbble.view.bucket_list;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.enzhi.quadrubbble.MainActivity;
import com.enzhi.quadrubbble.R;

/**
 * Created by enzhi on 9/26/2016.
 */
public class NewBucketDialogFragment extends DialogFragment {
    public static final String KEY_BUCKET_NAME = "bucket_name";
    public static final String KEY_BUCKET_DESCRIPTION = "bucket_description";

    public static final String TAG = "NewBucketDialogFragment";

    public static NewBucketDialogFragment newInstance() {

        Bundle args = new Bundle();

        NewBucketDialogFragment fragment = new NewBucketDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_bucket, null);
        final EditText editBucketName = (EditText) view.findViewById(R.id.new_bucket_name);
        final EditText editBucketDescription = (EditText) view.findViewById(R.id.new_bucket_description);
        Dialog dialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle("New bucket")
                .setPositiveButton(R.string.new_bucket_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(KEY_BUCKET_NAME, editBucketName.getText().toString());
                        resultIntent.putExtra(KEY_BUCKET_DESCRIPTION, editBucketDescription.getText().toString());

                        //此處重要！！！
                        getTargetFragment().onActivityResult(
                                MainActivity.REQ_CODE_NEW_BUCKET, Activity.RESULT_OK, resultIntent);
                        dismiss();
                    }
                })
                .setNegativeButton("cancel", null)
                .show();
        //dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return dialog;
    }
}
