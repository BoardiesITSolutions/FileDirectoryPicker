package com.BoardiesITSolutions.FileDirectoryPicker;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class OpenFilePicker extends BasePicker
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_file_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pickerMode = PickerMode.OpenFileDialog;

        init();
    }
}
