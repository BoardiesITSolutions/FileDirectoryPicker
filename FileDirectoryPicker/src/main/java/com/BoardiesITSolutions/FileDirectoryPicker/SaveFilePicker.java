package com.BoardiesITSolutions.FileDirectoryPicker;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class SaveFilePicker extends BasePicker
{
    public static final String BUNDLE_SAVE_PATH = "SavePath";
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_file_picker);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pickerMode = PickerMode.SaveFileDialog;
        init();
    }
}
