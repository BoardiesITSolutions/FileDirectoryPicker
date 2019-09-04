package com.BoardiesITSolutions.FileDirectoryPicker;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

public class SaveFilePicker extends BasePicker
{
    public static final String BUNDLE_SAVE_PATH = "SavePath";
    public static final String BUNDLE_SAVE_FILE = "FileName";
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
