package com.android.sids.CamService;


import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.android.sids.Tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;



/**
 * Created by IT02106 on 17/05/2018.
 */

public class CameraManager implements  Camera.PictureCallback, Camera.ErrorCallback, Camera.PreviewCallback, Camera.AutoFocusCallback{
    private static final String TAG = "CameraManager" ;
    private static CameraManager mManager;
    private Context mContext;
    private Camera mCamera;
    private SurfaceTexture mSurface;

    public CameraManager(Context context)
    {
            mContext = context;
    }

    public static CameraManager getInstance(Context context)
    {
        if(mManager == null )
            mManager = new CameraManager(context);
        return  mManager;
    }

    public void takePhoto()
    {
        Log.d(TAG,"camera manager function");
        if(isFrontCameraAvailable())
        {
            initCamera();
        }
    }

    private boolean isFrontCameraAvailable()
    {
        boolean result = false;
        if(mContext!=null && mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
        {
            int numberOfCameras = Camera.getNumberOfCameras();

            for(int i = 0;i<numberOfCameras;i++)
            {
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(i, info);

                if(info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
                {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }

    private void initCamera()
    {
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);

        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] objects) {
                //mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                return null;
            }

            @Override
            protected void onPostExecute(Object object)
            {
                try {
                    if(mCamera!=null)
                    {

                        mSurface = new SurfaceTexture(123);
                        mCamera.setPreviewTexture(mSurface);

                        Camera.Parameters params = mCamera.getParameters();
                        int angle = 270;//getCameraRotationAngle(Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
                        params.setRotation(angle);


                        if(autoFocusSupported(mCamera))
                        {
                            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

                        }
                        else
                        {
                            Log.w("asdaxxx","Autofocus is not supported");
                        }

                        mCamera.setParameters(params);
                        mCamera.setPreviewCallback(CameraManager.this);
                        mCamera.setErrorCallback(CameraManager.this);
                        mCamera.startPreview();
                        muteSound();
                    }
                }
                catch (IOException e)
                {
                    Log.w("asdaxxx","Exception camera");
                    e.printStackTrace();
                    releaseCamera();
                }

            }

        }.execute();
    }

    private boolean autoFocusSupported(Camera camera)
    {
        if(camera != null)
        {
            Camera.Parameters parames = camera.getParameters();
            List focusModes = parames.getSupportedFocusModes();

            if(focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO))
            {
                return true;
            }

        }

        return false;
    }

    private void muteSound()
    {
        if(mContext != null)
        {
            AudioManager mgr = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                mgr.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE,0);
            } else
            {
                mgr.setStreamMute(AudioManager.STREAM_SYSTEM, true);
            }
        }
    }

    private void releaseCamera()
    {
        if(mCamera != null)
        {
            mCamera.release();
            mSurface.release();
            mCamera = null;
            mSurface = null;
        }

        unmuteSound();
    }

    private void unmuteSound()
    {
        if(mContext != null)
        {
            AudioManager mgr = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                mgr.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE,0);
            }
            else
            {
                mgr.setStreamMute(AudioManager.STREAM_SYSTEM, false);
            }
        }
    }


    @Override
    public void onError(int error, Camera camera) {

        switch (error) {
            case Camera.CAMERA_ERROR_SERVER_DIED:
                Log.e(TAG, "Camera error: Media server died");
                break;
            case Camera.CAMERA_ERROR_UNKNOWN:
                Log.e(TAG, "Camera error: Unknown");
                break;
            case Camera.CAMERA_ERROR_EVICTED:
                Log.e(TAG, "Camera error: Camera was disconnected due to use by higher priority user");
                break;
            default:
                Log.e(TAG, "Camera error: no such error id (" + error + ")");
                break;
        }
    }


    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {

        try
        {
            if(autoFocusSupported(camera))
            {
               // mCamera.autoFocus(this);
                camera.setPreviewCallback(null);
                camera.takePicture(null,null,this);
            }
            else
            {

                camera.setPreviewCallback(null);
                camera.takePicture(null,null,this);
            }
        } catch (Exception e) {

            Log.e(TAG, "Camera error while taking picture");
            e.printStackTrace();
            releaseCamera();
        }
    }

    @Override
    public void onAutoFocus(boolean b, Camera camera) {
        if(camera != null)
        {
            try
            {
                camera.takePicture(null,null,this);
                mCamera.autoFocus(null);

            }catch (Exception e)
            {

                e.printStackTrace();
                releaseCamera();
            }
        }


    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {

        savePicture(bytes);
        releaseCamera();
    }

    private String savePicture(byte[] bytes)
    {


        try
        {
            File pictureFileDir = getDir();
            if(bytes == null)
            {
                Toast.makeText(mContext, "cant save image", Toast.LENGTH_LONG).show();
                Log.e("asdaxxx","Can't save image - no data");
                return null;
            }

            if(!pictureFileDir.exists() && !pictureFileDir.mkdirs())
            {
                Toast.makeText(mContext, "Can't create directory to save image", Toast.LENGTH_LONG).show();
                Log.e("asdaxxx","Can't create directory to save image.");
                return null;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
            String date = dateFormat.format(new Date());
            String photoFileName = Tools.app_name + date + ".jpg";

            Tools.filepath = pictureFileDir.getPath() + File.separator + photoFileName;
            Tools.fileName = photoFileName;
            Log.d("asdaxxx",Tools.filepath);


            File pictureFile = new File(Tools.filepath);
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(bytes);
            fos.close();

            Tools.compressImage(Tools.filepath,mContext);



            Log.d("asdaxxx","New image was saved" + photoFileName);


            Tools.waitThenUploadToDive(mContext);


        } catch (Exception e)
        {
            Log.e("asdaxxx",e.toString());

            e.printStackTrace();
        }

        return Tools.filepath;
    }

    private File getDir()
    {
        File sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, Tools.app_name);
    }


}
