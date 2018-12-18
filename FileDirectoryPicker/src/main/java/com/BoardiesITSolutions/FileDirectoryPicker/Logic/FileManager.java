package com.BoardiesITSolutions.FileExplorer.Logic;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 30/09/2014.
 */
public class FileManager
{
    private static final String TAG = "FileManager";
    Context context;

    public FileManager(Context context)
    {
        this.context = context;
    }

    /**
     * Get the directories and files from the root directory. Currently whereever
     * the android API Environment.getExternalStorageDirectory() points to
     * @return An ArrayList<DirectoryOrFileInfo> containing the information about the directory and the files
     */
    public ArrayList<DirectoryOrFileInfo> getFilesAndDirectoryStructure()
    {
        return getFilesAndDirectoryStructure(Environment.getExternalStorageDirectory().getPath());
    }

    /**
     * Gets the directory and file structure of the specified path
     * @param path The current path to get the directory structure from
     * @return An ArrayList<DirectoryOrFileInfo> containing the information about the directory and the files
     */
    public ArrayList<DirectoryOrFileInfo> getFilesAndDirectoryStructure(String path)
    {
        ArrayList<DirectoryOrFileInfo> filesAndDirectoriesArray = new ArrayList<DirectoryOrFileInfo>();

        File rootPath = new File(path);
        Log.v(TAG, "Root Path: " + rootPath.getPath());

        String[] directories = rootPath.list(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String filename) {
                return new File(dir, filename).isDirectory();
            }
        });
        for (int i = 0; i < directories.length; i++)
        {
            DirectoryOrFileInfo directoryOrFileInfo = new DirectoryOrFileInfo();
            directoryOrFileInfo.isDirectory = true;
            directoryOrFileInfo.directory = directories[i];
            filesAndDirectoriesArray.add(directoryOrFileInfo);
            Log.v(TAG, "Directory: " + directories[i]);
        }

        String[] files = rootPath.list(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String filename) {
                return new File(dir, filename).isFile();
            }
        });

        for (int i = 0; i < files.length; i++)
        {
            DirectoryOrFileInfo directoryOrFileInfo = new DirectoryOrFileInfo();
            directoryOrFileInfo.isDirectory = false;
            FileInformation fileInformation = new FileInformation();
            fileInformation.fileName = files[i];
            directoryOrFileInfo.fileInformation = fileInformation;
            filesAndDirectoriesArray.add(directoryOrFileInfo);
        }

        return filesAndDirectoriesArray;
    }

    public class DirectoryOrFileInfo
    {
        protected boolean isDirectory;
        protected String directory;
        protected FileInformation fileInformation;
        protected boolean selected = false;

        public boolean isDirectory()
        {
            return isDirectory;
        }
        public String getDirectory()
        {
            return directory;
        }
        public boolean isSelected()
        {
            return selected;
        }
        public void setSelected(boolean selected)
        {
            this.selected = selected;
        }

        public FileInformation getFileInformation()
        {
            return fileInformation;
        }
    }


    public class FileInformation
    {
        protected String fileName;
        protected long modifiedTime;
        protected long fileSize;

        public String getFileName()
        {
            return fileName;
        }
    }
}
