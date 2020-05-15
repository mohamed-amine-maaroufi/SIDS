package com.andromob.sids.logs;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.andromob.sids.R;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

/**
 * The Class ImageAdapter.
 */
class ImageAdapter extends BaseAdapter {

    /** The context. */
    private Activity context;

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    /** The images. */
    private ArrayList<String> images;

    /**
     * Instantiates a new image adapter.
     *
     * @param localContext
     *            the local context
     */
    public ImageAdapter(Activity localContext) {
        context = localContext;
        images = getAllShownImagesPath(context);
    }

    public int getCount() {
        return images.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        ImageView picturesView;
        if (convertView == null) {
            picturesView = new ImageView(context);
            picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            picturesView
                    .setLayoutParams(new GridView.LayoutParams(270, 270));

        } else {
            picturesView = (ImageView) convertView;
        }


        Glide.with(context).load(images.get(position))
                .placeholder(R.drawable.ic_launcher_foreground).centerCrop()
                .into(picturesView);


        return picturesView;
    }

    /**
     * Getting All Images Path.
     *
     * @param activity
     *            the activity
     * @return ArrayList with images Path
     */
    private ArrayList<String> getAllShownImagesPath(Activity activity) {
       /*Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI ;
        //uri = Uri.parse(Environment.getExternalStorageDirectory()+"/Pictures/SIDS/");

        Log.d("ImageAdapter", "uri = " + uri.toString());

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Log.d("ImageAdapter", "absolutePathOfImage = " + absolutePathOfImage);
            Log.d("ImageAdapter", "column_index_data = " + column_index_data);

            listOfAllImages.add(absolutePathOfImage);
        }*/
      ///storage/emulated/0/Pictures/SIDS/SIDS20200503022322.jpg
        ArrayList<String> listOfAllImages = new ArrayList<String>();
       //String path = Environment.getExternalStorageDirectory().toString()+"/Pictures/SIDS/";
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+"/SIDS/";
        String absolutePathOfImage = null;
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();

        if(files != null)
        {
            Log.d("Files", "files size: " + files.length);
            for (int i = 0; i < files.length; i++)
            {

                absolutePathOfImage = path + files[i].getName();
                Log.d("Files", "FileName:" + absolutePathOfImage);
                listOfAllImages.add(absolutePathOfImage);
            }
        }


        return listOfAllImages;
    }
}
