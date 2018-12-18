package com.BoardiesITSolutions.FileDirectoryPicker;

import android.os.Bundle;

public class SaveFilePicker extends BasePicker
{
    public static final String BUNDLE_SAVE_PATH = "SavePath";
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_file_picker);

        pickerMode = PickerMode.SaveFileDialog;
        init();
    }
}
