package com.BoardiesITSolutions.FileDirectoryPicker;

import android.os.Bundle;

public class DirectoryPicker extends BasePicker
{
    public static final String BUNDLE_CHOSEN_DIRECTORY = "ChosenDirectory";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.directory_picker);

        pickerMode = PickerMode.DirectoryPicker;

        init();

    }
}
