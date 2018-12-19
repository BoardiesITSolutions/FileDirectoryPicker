package com.BoardiesITSolutions.FileDirectoryPicker;

import android.content.Context;
import android.graphics.Typeface;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.BoardiesITSolutions.FileExplorer.Logic.FileManager;

import java.util.ArrayList;

/**
 * Created by Chris on 07/09/2014.
 */
public class DirectoryAdapter extends BaseAdapter
{
    public static ArrayList<FileManager.DirectoryOrFileInfo> directoryStructureArray = null;
    private Context context;

    public DirectoryAdapter(Context context, ArrayList<FileManager.DirectoryOrFileInfo> directoryStructureArray)
    {
        this.context = context;
        this.directoryStructureArray = directoryStructureArray;
    }

    @Override
    public int getCount() {
        return directoryStructureArray.size();
    }

    @Override
    public FileManager.DirectoryOrFileInfo getItem(int position) {
       return directoryStructureArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.directory_file_picker, null);

            FileManager.DirectoryOrFileInfo directoryOrFileInfo = getItem(position);
            TextView textView = convertView.findViewById(R.id.textview);
            ImageView imageView = convertView.findViewById(R.id.imageView);
            textView.setTypeface(null, Typeface.BOLD);
            if (getItem(position).isSelected())
            {
                convertView.setBackgroundResource(R.color.colorPrimary);
            }
            if (directoryOrFileInfo.isDirectory())
            {

                textView.setText(directoryOrFileInfo.getDirectory());
                imageView.setBackgroundResource(R.drawable.directory);
            }
            else
            {
                imageView.setBackgroundResource(R.drawable.file);
                textView.setText(directoryOrFileInfo.getFileInformation().getFileName());
            }

            return convertView;
        }
        catch (Exception ex)
        {
            Log.e("GetView", ex.toString());
            return null;
        }
    }
}
