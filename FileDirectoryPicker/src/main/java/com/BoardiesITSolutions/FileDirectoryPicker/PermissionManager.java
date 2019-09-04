package com.BoardiesITSolutions.FileDirectoryPicker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.BoardiesITSolutions.FileDirectoryPicker.Logic.IPermissionResponse;

public class PermissionManager
{
    public static final int REQUEST_PERMISSION_READ_STORAGE = 1;
    public static final int REQUEST_PERMISSION_WRITE_STORAGE = 2;
    private final String TAG = "PermissionManager";
    private AppCompatActivity activity;
    private Context context;
    private IPermissionResponse iPermissionResponse;

    public PermissionManager(AppCompatActivity activity, IPermissionResponse iPermissionResponse)
    {
        this.activity = activity;
        this.context = activity;
        this.iPermissionResponse = iPermissionResponse;
    }

    public boolean isPermissionGranted(String permission)
    {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void requestPermission(int permissionRequest, String permissionReasoning)
    {
        Log.d(TAG, "Got permission request: " + permissionRequest);
        String permission;
        switch (permissionRequest)
        {
            case REQUEST_PERMISSION_READ_STORAGE:
                permission = Manifest.permission.READ_EXTERNAL_STORAGE;
                break;
            case REQUEST_PERMISSION_WRITE_STORAGE:
                permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                break;
            default:
                throw new RuntimeException("Invalid permission request id specified");
        }
        Log.d(TAG, "Requesting permission '"+permission+"'");
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
        {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))
            {
                ActivityCompat.requestPermissions(activity, new String[] {permission}, permissionRequest);
            }
            else
            {
                Toast.makeText(activity, permissionReasoning, Toast.LENGTH_LONG).show();
                requestPermission(permissionRequest, permissionReasoning);
            }
        }
        else
        {
            iPermissionResponse.permissionGranted();
        }
    }

    public void handlePermissionResult(int requestCode, String permissions[], int[] grantResults)
    {
        Log.d(TAG, "Handling permission result for request: " + requestCode);
        switch (requestCode)
        {
            case REQUEST_PERMISSION_READ_STORAGE:
            case REQUEST_PERMISSION_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    iPermissionResponse.permissionGranted();
                }
                else
                {
                    iPermissionResponse.permissionDenied();
                }
                break;
            default:
                iPermissionResponse.invalidPermissionRequested();
        }
    }
}
