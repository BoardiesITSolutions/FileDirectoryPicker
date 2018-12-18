package com.BoardiesITSolutions.FileDirectoryPicker;

import android.os.Bundle;

public class OpenFilePicker extends BasePicker
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_file_activity);

        pickerMode = PickerMode.OpenFileDialog;

        init();
    }
}
