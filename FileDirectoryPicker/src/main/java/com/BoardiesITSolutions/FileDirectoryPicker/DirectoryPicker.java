package com.BoardiesITSolutions.FileDirectoryPicker;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

public class DirectoryPicker extends BasePicker
{
    public static final String BUNDLE_CHOSEN_DIRECTORY = "ChosenDirectory";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.directory_picker);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pickerMode = PickerMode.DirectoryPicker;

        init();

    }
}
