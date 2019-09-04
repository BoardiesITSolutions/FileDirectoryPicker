package com.BoardiesITSolutions.FileDirectoryPicker;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.BoardiesITSolutions.FileDirectoryPicker.Logic.IPermissionResponse;

import java.io.File;

public class CreateDirectory extends AppCompatActivity implements IPermissionResponse
{

    private static final String TAG = "CreateDirectory";
    public static final int REQUEST_CREATE_DIRECTORY = 1;
    private PermissionManager permissionManager;
    EditText txtDirectoryName;
    Button btnCancel;
    Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_directory);

        permissionManager = new PermissionManager(this, this);
        txtDirectoryName = (EditText)findViewById(R.id.createDirectory_txtDirectoryName);
        btnCancel = (Button)findViewById(R.id.createDirectory_btnCancel);
        btnCreate = (Button)findViewById(R.id.createDirectory_btnCreate);

        btnCancel.setOnClickListener(mBtnCancelClickListener);
        btnCreate.setOnClickListener(mBtnCreateClickListener);
    }

    protected View.OnClickListener mBtnCancelClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    protected View.OnClickListener mBtnCreateClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            if (!permissionManager.isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                permissionManager.requestPermission(PermissionManager.REQUEST_PERMISSION_WRITE_STORAGE, "Write permission is required in order to create a directory in the current location");
            }
            else
            {
                createDirectory();
            }
        }
    };

    private void createDirectory()
    {

        try
        {
            if (txtDirectoryName.getText().toString().length() == 0)
            {
                txtDirectoryName.setError(getString(R.string.please_enter_directory_name));
            }
            else {
                Bundle bundle = getIntent().getExtras();
                if (bundle == null) {
                    throw new Exception("Bundle was empty");
                }
                if (bundle.getString("currentPath") == null) {
                    throw new Exception("Current Path was null");
                }

                String currentPath = bundle.getString("currentPath");
                File directory = new File(currentPath + "/" + txtDirectoryName.getText().toString());
                if (directory.mkdir())
                {
                    Toast.makeText(CreateDirectory.this, getString(R.string.successfully_created_directory), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Directory Successfully Created");
                }
                else
                {
                    Log.d(TAG, "Directory not created");
                }
                setResult(Activity.RESULT_OK);
                finish();
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG, ex.toString());
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void permissionGranted()
    {
        createDirectory();
    }

    @Override
    public void permissionDenied()
    {

    }

    @Override
    public void invalidPermissionRequested()
    {

    }
}
