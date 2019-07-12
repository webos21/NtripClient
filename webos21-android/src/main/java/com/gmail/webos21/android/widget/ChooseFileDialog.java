package com.gmail.webos21.android.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gmail.webos21.android.R;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class ChooseFileDialog extends Dialog {

    private static final int FOLDER_IMAGE_ID = 0;
    private static final int FILE_IMAGE_ID = 1;

    private static final int ROOT_FOLDER_LEVEL = 0;
    private static final int PARENT_FODLER_LEVEL = 1;
    private static final int FILES_IN_FOLDER_LEVEL = 2;

    private static final int MINIMUM_FILESIZE_BYTES = 1024;

    private static final int BG_COLOR_0 = 0;

    private static final String ROOTPATH = "/";
    private static final String PARENTPATH = "../";

    private FileChosenListener chosenMonitor;

    private TextView mFilePathView;

    private ListView mListView;
    private FileListAdapter mAdaptor;
    private ArrayList<FileFolderItem> mFileFolderList;

    private Button mChooseButton;

    private String mCurrentPath = ROOTPATH;
    private File mSelectedFile;
    private String mFileTypeFilter;

    /**
     * @param context      the parent context
     * @param entryPath    the entry path of browsing
     * @param filterSuffix the string to use filtering
     */
    public ChooseFileDialog(Context context, String entryPath, String filterSuffix) {
        super(context);

        setContentView(R.layout.dialog_choose_file);
        mListView = (ListView) findViewById(R.id.dlgFileList);
        mFilePathView = (TextView) findViewById(R.id.dlgFileTxtPath);
        mChooseButton = (Button) findViewById(R.id.dlgFileBtnSelect);
        mChooseButton.setEnabled(false);
        mChooseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mSelectedFile != null) {
                    if (chosenMonitor != null) {
                        chosenMonitor.onFileChosen(mSelectedFile);
                    }
                    dismiss();
                }
            }
        });

        mFileFolderList = new ArrayList<FileFolderItem>();
        mAdaptor = new FileListAdapter(context, mFileFolderList);

        String curPath = entryPath;
        if (curPath == null) {
            curPath = ROOTPATH;
        }

        mFileTypeFilter = filterSuffix;
        browseFolder(curPath);

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> listview, View view, int position, long id) {
                FileFolderItem item = mFileFolderList.get(position);
                if (item == null)
                    return;

                File file = new File(item.getFilePath());

                if (file.isDirectory()) {
                    mChooseButton.setEnabled(false);
                    if (file.canRead()) {
                        highlightClear(listview);
                        browseFolder(item.getFilePath());
                    }
                } else {
                    if (file.length() <= MINIMUM_FILESIZE_BYTES) {
                        showDialog(file.getName());
                    } else {
                        mSelectedFile = file;
                        view.setSelected(true);
                        mChooseButton.setEnabled(true);
                        highlightSelectedItem(listview, view);
                    }
                }
            }
        });

        mListView.setAdapter(mAdaptor);
    }

    public void setOnFileChosenListener(FileChosenListener fcl) {
        chosenMonitor = fcl;
    }

    /**
     * Browsing the current folder using given path
     *
     * @param path String The given folder path
     */
    private void browseFolder(final String path) {
        mFileFolderList.clear();
        mCurrentPath = path;

        File f = new File(mCurrentPath);
        File[] files = null;
        if (!f.exists()) {
            f = new File(ROOTPATH);
            files = f.listFiles();
        } else if (mFileTypeFilter == null) {
            files = f.listFiles();
        } else {
            files = f.listFiles(new FileFilter() {

                @Override
                public boolean accept(File pathname) {
                    if (pathname.isDirectory()) {
                        return true;
                    } else {
                        String name = pathname.getName();
                        if (name.endsWith(mFileTypeFilter)) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            });
        }

        mFilePathView.setText(getContext().getText(R.string.dlg_file_location) + ": " + mCurrentPath);

        if (!mCurrentPath.equals(ROOTPATH)) {
            FileFolderItem item = new FileFolderItem(ROOTPATH, ROOTPATH, FOLDER_IMAGE_ID, ROOT_FOLDER_LEVEL);
            mFileFolderList.add(item);

            item = new FileFolderItem(PARENTPATH, f.getParent(), FOLDER_IMAGE_ID, PARENT_FODLER_LEVEL);
            mFileFolderList.add(item);
        }

        for (File file : files) {
            if (file.isDirectory()) {
                mFileFolderList.add(new FileFolderItem(file.getName(), file.getPath(), FOLDER_IMAGE_ID, FILES_IN_FOLDER_LEVEL));
            } else {
                mFileFolderList.add(new FileFolderItem(file.getName(), file.getPath(), FILE_IMAGE_ID, FILES_IN_FOLDER_LEVEL));
            }
        }

        Collections.sort(mFileFolderList);

        mAdaptor.notifyDataSetChanged();
    }

    private void showDialog(String filename) {
        new AlertDialog.Builder(getContext()).setIcon(R.mipmap.cf_icon_dialog)
                .setTitle("[" + filename + "] " + getContext().getText(R.string.dlg_file_cannot_load))
                .setPositiveButton("OK", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    /**
     * Highlight selected Item
     *
     * @param listview AdapterView<> The listview instance
     * @param view     View the selected item view
     */
    private void highlightSelectedItem(AdapterView<?> listview, View view) {
        for (int i = 0; i < listview.getChildCount(); i++) {
            listview.getChildAt(i).setBackgroundColor(BG_COLOR_0);
        }
        view.setBackgroundColor(Color.CYAN);
    }

    /**
     * Highlight clear
     *
     * @param listview AdapterView<> The listview instance
     */
    private void highlightClear(AdapterView<?> listview) {
        for (int i = 0; i < listview.getChildCount(); i++) {
            listview.getChildAt(i).setBackgroundColor(BG_COLOR_0);
        }
    }

    public interface FileChosenListener {
        public void onFileChosen(File chosenFile);
    }

    /**
     * List adapter class to display file or folder items in #ListView
     */
    private class FileListAdapter extends BaseAdapter {

        private ArrayList<FileFolderItem> mList = null;
        private LayoutInflater mInflater;

        public FileListAdapter(Context context, ArrayList<FileFolderItem> list) {
            mList = list;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if (mList == null)
                return 0;
            else
                return mList.size();
        }

        @Override
        public Object getItem(int index) {
            if (mList == null)
                return null;
            else
                return mList.get(index);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.dialog_choose_file_row, parent, false);

                holder = new ViewHolder();
                holder.fileName = (TextView) convertView.findViewById(R.id.dlgFileItemFolderName);
                holder.imageView = (ImageView) convertView.findViewById(R.id.dlgFileItemIcon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            FileFolderItem selected = mList.get(position);
            holder.fileName.setText(selected.getFileName());
            int imageId = selected.getImageid();
            if (imageId == FILE_IMAGE_ID) {
                holder.imageView.setImageResource(R.mipmap.cf_icon_file);
            } else if (imageId == FOLDER_IMAGE_ID) {
                holder.imageView.setImageResource(R.mipmap.cf_icon_folder);
            }

            return convertView;
        }

        /**
         * Hold the item views instance
         */
        private class ViewHolder {
            TextView fileName;
            ImageView imageView;
        }

    }

    /**
     * File/Folder item class to hold file name/path, image id and level info
     */
    private class FileFolderItem implements Comparable<FileFolderItem> {
        public String fileName = null;
        public String filePath = null;
        public int imageId;
        public int level;

        /**
         * Class constructor
         *
         * @param filename String file name
         * @param filepath String file path
         * @param imageid  int the image id
         * @param level    int used when sorting items
         */
        public FileFolderItem(String filename, String filepath, int imageid, int level) {
            this.fileName = filename;
            this.filePath = filepath;
            this.imageId = imageid;
            this.level = level;
        }

        /**
         * Get the file name for each item view
         *
         * @return file name
         */
        public String getFileName() {
            return fileName;
        }

        /**
         * Get image id for each item view
         *
         * @return image id
         */
        public int getImageid() {
            return imageId;
        }

        /**
         * Get file path for each item view
         *
         * @return the file path
         */
        public String getFilePath() {
            return filePath;
        }

        @Override
        public int compareTo(FileFolderItem another) {
            if (this.imageId > another.imageId) {
                return 1;
            } else if (this.imageId < another.imageId) {
                return -1;
            } else {
                if (this.level == another.level) {
                    return (this.fileName.toLowerCase(Locale.getDefault())
                            .compareTo(another.fileName.toLowerCase(Locale.getDefault())));
                } else if (this.level > another.level) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }
    }
}
