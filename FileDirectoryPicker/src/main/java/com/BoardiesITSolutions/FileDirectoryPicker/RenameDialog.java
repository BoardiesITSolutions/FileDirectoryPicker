package com.BoardiesITSolutions.FileDirectoryPicker;

import android.app.Activity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;

public class RenameDialog extends AppCompatActivity
{
    public static final String BUNDLE_CURRENT_PATH = "CurrentPath";
    public static final String BUNDLE_ITEM_TO_RENAME = "DirectoryToRename";

    private EditText txtRename;
    private Button btnCancel;
    private Button btnRename;

    private String originalName;
    private String currentPath;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rename_activity);

        txtRename = findViewById(R.id.rename_txtName);
        btnCancel = findViewById(R.id.renameDirectory_btnCancel);
        btnRename = findViewById(R.id.renameDirectory_btnRename);

        btnRename.setOnClickListener(mBtnRenameClickListener);
        btnCancel.setOnClickListener(mBtnCancelClickListener);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {
            originalName = bundle.getString(BUNDLE_ITEM_TO_RENAME);
            currentPath = bundle.getString(BUNDLE_CURRENT_PATH);
            txtRename.setText(originalName);
            txtRename.selectAll();
        }
    }

    private View.OnClickListener mBtnCancelClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    };

    private View.OnClickListener mBtnRenameClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            renameItem();
        }
    };

    private void renameItem()
    {
        if (txtRename.getText().toString().length() == 0)
        {
            txtRename.setError(getString(R.string.please_enter_a_file_directory_name));
        }
        else if (txtRename.getText().toString().equals(originalName))
        {
            txtRename.setError(getString(R.string.file_directory_name_hasnt_been_changed));
        }
        else
        {
            File originalFile = new File(currentPath + "/" + originalName);
            File newFile = new File(currentPath + "/" + txtRename.getText().toString());
            originalFile.renameTo(newFile);

            setResult(Activity.RESULT_OK);
            finish();
        }
    }
}

