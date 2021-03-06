package com.theunheard.habittracker;


import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditHabitFragmentDialog extends DialogFragment {

    private View dialogView;

    public EditHabitFragmentDialog() {
        // Required empty public constructor
    }


    public static EditHabitFragmentDialog newInstance(String title) {
        EditHabitFragmentDialog frag = new EditHabitFragmentDialog();
        Bundle args = new Bundle();
        args.putString(String.valueOf(com.theunheard.habittracker.R.string.edit_habit_dialog_title), title);
        frag.setArguments(args);
        return frag;
    }


    @Nullable
    @Override
    public View getView() {
        return dialogView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dialogView = inflater.inflate(com.theunheard.habittracker.R.layout.fragment_edit_habit_fragment_dialog, container, false);
        return dialogView;
    }




}
