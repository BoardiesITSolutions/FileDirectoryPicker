package com.BoardiesITSolutions.FileDirectoryPicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.BoardiesITSolutions.FileDirectoryPicker.Logic.IPermissionResponse;
import com.BoardiesITSolutions.FileExplorer.Logic.FileManager;

import java.io.File;
import java.util.ArrayList;

import static com.BoardiesITSolutions.FileDirectoryPicker.DirectoryPicker.BUNDLE_CHOSEN_DIRECTORY;

public abstract class BasePicker extends AppCompatActivity implements IPermissionResponse
{
    private static final int REQUEST_RENAME_ITEM = 1000;
    public static final String BUNDLE_SELECTED_FILE = "SelectedFile";
    protected enum PickerMode {DirectoryPicker, OpenFileDialog, SaveFileDialog}
    protected PickerMode pickerMode;
    protected boolean inSelectionMode = false;
    private String currentPath = "";
    protected com.BoardiesITSolutions.FileExplorer.Logic.FileManager fileManager = null;
    protected GridView gridView = null;
    protected DirectoryAdapter directoryAdapter;
    protected ArrayList<FileManager.DirectoryOrFileInfo> directoryStructureArray;
    protected ImageButton btnUpLevel;
    protected LinearLayout navigationLayout;
    protected Button btnMoveToRoot = null;
    protected ArrayList<Button> navFolderButtons;
    protected PermissionManager permissionManager;
    protected ArrayList<FileManager.DirectoryOrFileInfo> selectedDirectories;

    String getCurrentPath()
    {
        return this.currentPath;
    }

    protected void init()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        selectedDirectories = new ArrayList<>();

        navFolderButtons = new ArrayList<>();


        navigationLayout = findViewById(R.id.directoryFilePicker_currentPath);
        btnMoveToRoot = new Button(this);

        btnMoveToRoot.setText("/");
        btnMoveToRoot.setEnabled(false);
        btnMoveToRoot.setTag(Environment.getExternalStorageDirectory().getPath());
        btnMoveToRoot.setOnClickListener(mNavFolderClickListener);
        navFolderButtons.add(btnMoveToRoot);
        navigationLayout.addView(btnMoveToRoot);

        gridView = findViewById(R.id.directory_picker_gridview);
        btnUpLevel = findViewById(R.id.directoryFilePicker_upLevel);
        btnUpLevel.setEnabled(false);



        gridView.setOnItemClickListener(mGridOnItemClickListener);
        gridView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        gridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener()
        {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked)
            {
                if (checked)
                {

                    gridView.getChildAt(position).setSelected(true);
                    directoryAdapter.getItem(position).setSelected(true);
                    gridView.getChildAt(position).setBackgroundResource(R.color.appPrimaryColour);

                    selectedDirectories.add(directoryAdapter.getItem(position));

                }
                else
                {
                    gridView.getChildAt(position).setSelected(false);
                    directoryAdapter.getItem(position).setSelected(false);
                    FileManager.DirectoryOrFileInfo selectedDirectory = directoryAdapter.getItem(position);
                    for (int i = 0; i < selectedDirectories.size(); i++)
                    {
                        if (selectedDirectories.get(i) == selectedDirectory)
                        {
                            selectedDirectories.remove(i);
                            selectedDirectory.setSelected(false);
                        }
                    }
                    gridView.getChildAt(position).setBackgroundColor(ContextCompat.getColor(BasePicker.this, android.R.color.transparent));
                }

                onPrepareActionMode(mode, mode.getMenu());
                mode.setTitle(selectedDirectories.size() + " selected");
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu)
            {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.file_dir_cab_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu)
            {
                if (selectedDirectories.size() >= 2)
                {
                    menu.findItem(R.id.mnuRename).setVisible(false);
                }
                else
                {
                    menu.findItem(R.id.mnuRename).setVisible(true);
                }
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item)
            {
                int menu_id = item.getItemId();
                if (menu_id == R.id.mnuDelete)
                {
                    int selectedCount = selectedDirectories.size();
                    deleteSelectedItems();
                    mode.finish();
                    Toast.makeText(BasePicker.this, "Successfully deleted " + selectedCount + " item(s)", Toast.LENGTH_SHORT).show();
                }
                else if (menu_id == R.id.mnuRename)
                {
                    //If we can click on rename, then we only have 1 file or directory to rename
                    Intent intent = new Intent(BasePicker.this, RenameDialog.class);
                    intent.putExtra(RenameDialog.BUNDLE_CURRENT_PATH, currentPath);
                    if (selectedDirectories.get(0).isDirectory())
                    {
                        intent.putExtra(RenameDialog.BUNDLE_ITEM_TO_RENAME, selectedDirectories.get(0).getDirectory());
                    }
                    else
                    {
                        intent.putExtra(RenameDialog.BUNDLE_ITEM_TO_RENAME, selectedDirectories.get(0).getFileInformation().getFileName());
                    }
                    startActivityForResult(intent, REQUEST_RENAME_ITEM);
                    mode.finish();
                }
                else
                {
                    return false;
                }
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode)
            {
                for (int i = 0; i < gridView.getChildCount();i++)
                {
                    gridView.getChildAt(i).setBackgroundResource(android.R.color.transparent);
                }
                for (int i = 0; i < directoryStructureArray.size(); i++)
                {
                    directoryStructureArray.get(i).setSelected(false);
                }
                selectedDirectories.clear();
            }
        });

        btnUpLevel.setOnClickListener(mBtnUpLevelClickListener);

        //Check that we have permission to read the SD card
        permissionManager = new PermissionManager(this, this);
        permissionManager.requestPermission(PermissionManager.REQUEST_PERMISSION_READ_STORAGE, "We need to read the SD card in order for you to pick where to export to");
    }

    protected void retrieveDirectoryListing()
    {
        currentPath = Environment.getExternalStorageDirectory().getPath();

        fileManager = new FileManager(this);

        directoryStructureArray = fileManager.getFilesAndDirectoryStructure();
        directoryAdapter = new DirectoryAdapter(this, directoryStructureArray);
        gridView.setAdapter(directoryAdapter);
    }

    public View.OnClickListener mNavFolderClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view) {
            Button button = ((Button)view);
            directoryStructureArray.clear();
            directoryStructureArray.addAll(fileManager.getFilesAndDirectoryStructure(button.getTag().toString()));
            currentPath = button.getTag().toString();
            directoryAdapter.notifyDataSetChanged();
            //First find the index of the view of the button that was clicked
            int buttonClickedIndex;
            for (buttonClickedIndex = 0; buttonClickedIndex < navigationLayout.getChildCount(); buttonClickedIndex++)
            {
                Button currentButton = (Button)navigationLayout.getChildAt(buttonClickedIndex);
                if (button.getText().toString().equals(currentButton.getText().toString()))
                {
                    break;
                }
            }

            //Now remove all views after the index
            for (int i = navigationLayout.getChildCount()-1; i > buttonClickedIndex; i--)
            {
                navigationLayout.removeViewAt(i);
            }

            if (button.getText().toString().equals("/"))
            {
                btnMoveToRoot.setEnabled(false);
                btnUpLevel.setEnabled(false);
            }

        }
    };

    /**
     * Remove the last nav folder button from the top linear layout.
     * It will only be removed if the tag is not the same as the current path
     * e.g. if the user clicks on the directory for the directory they are alread in
     */
    private void removeLastElementNavFolderButton()
    {
        for (int i = 0; i < navigationLayout.getChildCount(); i++)
        {
            View navView = navigationLayout.getChildAt(i);
            if (navView instanceof ImageButton)
            {
                continue;
            }
            if (navView instanceof Button) {
                Button navButton = ((Button)navView);
                Log.d("DirectoryPicker", "At: " + i + " " + navButton.getTag().toString());
                if (i == navigationLayout.getChildCount()-1
                        && !navButton.getTag().toString().equals(currentPath))
                {
                    navigationLayout.removeViewAt(i);

                }
            }
        }
    }

    protected View.OnClickListener mBtnUpLevelClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            int endOfCurrentPath = currentPath.lastIndexOf("/");
            currentPath = currentPath.substring(0, endOfCurrentPath);
            directoryStructureArray.clear();
            directoryStructureArray.addAll(fileManager.getFilesAndDirectoryStructure(currentPath));
            directoryAdapter.notifyDataSetChanged();
            if (currentPath.equals(Environment.getExternalStorageDirectory().getPath()))
            {
                btnUpLevel.setEnabled(false);
                btnMoveToRoot.setEnabled(false);
                //Finds the root button from the linear navigation layout
                navigationLayout.getChildAt(1).setEnabled(false);
            }
            removeLastElementNavFolderButton();
        }
    };

    AdapterView.OnItemClickListener mGridOnItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            FileManager.DirectoryOrFileInfo directoryOrFileInfo =
                    (FileManager.DirectoryOrFileInfo)parent.getItemAtPosition(position);

            if (directoryOrFileInfo.isDirectory())
            {
                currentPath += "/" + directoryOrFileInfo.getDirectory();
                Button btnMoveToDir = new Button(BasePicker.this);
                btnMoveToDir.setTag(currentPath);
                btnMoveToDir.setText(directoryOrFileInfo.getDirectory());
                btnMoveToDir.setOnClickListener(mNavFolderClickListener);
                btnMoveToDir.setAllCaps(false);
                navFolderButtons.add(btnMoveToDir);
                navigationLayout.addView(btnMoveToDir);
                btnMoveToRoot.setEnabled(true);
                directoryStructureArray.clear();
                directoryStructureArray.addAll(fileManager.getFilesAndDirectoryStructure(currentPath));
                directoryAdapter.notifyDataSetChanged();
                btnUpLevel.setEnabled(true);
                //Find the root button to enable it
                navigationLayout.getChildAt(1).setEnabled(true);
            }
            else
            {
                if (pickerMode == PickerMode.OpenFileDialog)
                {
                    Intent intent = new Intent();
                    intent.putExtra(BasePicker.BUNDLE_SELECTED_FILE, currentPath + "/" + directoryOrFileInfo.getFileInformation().getFileName());
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.directory_file_picker_menu, menu);
        if (pickerMode == PickerMode.DirectoryPicker
                || pickerMode == PickerMode.SaveFileDialog)
        {
            getMenuInflater().inflate(R.menu.main_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.mainMnu_addDirectory) {
            Intent intent = new Intent(BasePicker.this, CreateDirectory.class);
            intent.putExtra("currentPath", currentPath);
            startActivityForResult(intent, CreateDirectory.REQUEST_CREATE_DIRECTORY);
            return true;
        }
        else if (itemId == R.id.mnuDone)
        {
            if (pickerMode == PickerMode.DirectoryPicker)
            {
                Intent intent = new Intent();
                intent.putExtra(BUNDLE_CHOSEN_DIRECTORY, getCurrentPath());
                setResult(Activity.RESULT_OK, intent);
                finish();
                return true;
            }
            else if (pickerMode == PickerMode.SaveFileDialog)
            {
                if (BasePicker.this instanceof SaveFilePicker)
                {
                    returnSaveFile();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void returnSaveFile()
    {
        EditText txtFileName = findViewById(R.id.txtFileName);
        if (txtFileName.getText().toString().length() == 0)
        {
            txtFileName.setError(getString(R.string.please_specify_a_file_name));
        }
        else
        {
            String savePath = currentPath + "/" + txtFileName.getText().toString();
            Intent intent = new Intent();
            intent.putExtra(SaveFilePicker.BUNDLE_SAVE_PATH, savePath);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            if (requestCode == CreateDirectory.REQUEST_CREATE_DIRECTORY)
            {
                directoryStructureArray.clear();
                directoryStructureArray.addAll(fileManager.getFilesAndDirectoryStructure(currentPath));
                directoryAdapter.notifyDataSetChanged();
            }
            if (requestCode == BasePicker.REQUEST_RENAME_ITEM)
            {
                retrieveDirectoryListing();
            }
        }
    }

    private void deleteSelectedItems()
    {
        for (int i = 0; i < selectedDirectories.size(); i++)
        {
            if (selectedDirectories.get(i).isDirectory())
            {
                File file = new File(getCurrentPath() + "/" + selectedDirectories.get(i).getDirectory());
                file.delete();
            }
            else
            {
                File file = new File(getCurrentPath() + "/" + selectedDirectories.get(i).getFileInformation().getFileName());
                file.delete();
            }
        }
        selectedDirectories.clear();
        retrieveDirectoryListing();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.handlePermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    public void permissionGranted()
    {
        retrieveDirectoryListing();
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
