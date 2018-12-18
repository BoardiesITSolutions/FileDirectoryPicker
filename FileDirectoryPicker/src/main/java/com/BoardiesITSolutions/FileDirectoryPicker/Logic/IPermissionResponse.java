package com.BoardiesITSolutions.FileDirectoryPicker.Logic;

public interface IPermissionResponse
{
    void permissionGranted();
    void permissionDenied();
    void invalidPermissionRequested();
}
