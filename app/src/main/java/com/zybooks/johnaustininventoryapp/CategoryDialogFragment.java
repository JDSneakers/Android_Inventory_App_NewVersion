package com.zybooks.johnaustininventoryapp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

// DialogFragment to get category from user
public class CategoryDialogFragment extends DialogFragment {

    // Interface to send category back to activity
    public interface OnCategoryEnteredListener {
        void onCategoryEntered(String category);
    }

    private OnCategoryEnteredListener mListener;

    @Override // Create dialog box
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        // Create an EditText view to get user input
        final EditText categoryEditText = new EditText(getActivity());
        categoryEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        categoryEditText.setMaxLines(1);

        //pop up window for user to add a category
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.category)
                .setView(categoryEditText)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String category = categoryEditText.getText().toString();
                        mListener.onCategoryEntered(category.trim());
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    @Override // Make sure activity implements OnCategoryEnteredListener
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnCategoryEnteredListener) context;
    }
}