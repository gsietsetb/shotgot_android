package com.shotgot.shotgot.Fragment;

/**
 * Created by Guillermo on 9/1/17.
 */

import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.shotgot.shotgot.API.SocketPic;
import com.shotgot.shotgot.R;
import com.shotgot.shotgot.Utils.CameraView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.socket.client.Socket;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class FragmentShot extends ImmersiveModeFragment {
    private Camera mCamera = null;
    private CameraView mCameraView = null;
    private Camera.PictureCallback mPicture;
    private Socket mSocket;
    /**For NodeJS API
     * 192.168.1.15
     * Afterwrads should point to
     * api.shotgot.com
     */
    //    public Socket(Manager io, String nsp) {

    /*{
        try {
            Log.d("Socket", "Just before socket creation "+mSocket);
            IO.Options options = new IO.Options();
            options.port = 3000;
            *todo ToBeChanged to:
             * 192.168.1.15:3000
             * 127.0.0.1:3000
             * api.shotgot.com:3000
            mSocket = IO.socket("192.168.1.12:3000");
            mSocket.connect();
        } catch (URISyntaxException e) {}
    }*/

    /*Through Socket.io to our API
    private void attempPOST(byte[] data) throws IOException {
        String imgString = Base64.encodeToString(data,Base64.NO_WRAP);
//        String imageDataString = encodeImage(data);
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        URL url=new URL("http://192.168.1.19:3000/feat");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Connection", "Keep-Alive");
//        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
        out.write(imgString);
        out.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        while (in.readLine() != null) {
        }
        in.close();
    }*/

    /**
     * Safe method for getting a camera instance.
     *
     * @return
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    /**For Clarifai API
     final static String clientId = "SieJMnA5BP4CkpL0YoXEGOEj7VKAGrH8VLZpD7zm";
     final static String clientSecret = "QQLo9NTDvhg9R32nQaC8Fb-ogAZDyzD4YPushXH6";
     private List<ClarifaiOutput<Concept>> mTags;

     final ClarifaiClient client = new ClarifaiBuilder(clientId, clientSecret)
     .client(new OkHttpClient()) // OPTIONAL. Allows customization of OkHttp by the user
     .buildSync(); // or use .build() to get a Future<ClarifaiClient>
     */

    /**
     * Create a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

//    private Emitter.Listener onNewMessage = new Emitter.Listener() {
//
//        @Override
//        public void call(Object... args) {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    JSONObject data = (JSONObject) args[0];
//                    String username;
//                    String message;
//                    try {
//                        username = data.getString("username");
//                        message = data.getString("message");
//                    } catch (JSONException e) {
//                        return;
//                    }
//
//                    // add the message to view
//                    addMessage(username, message);
//                }
//            });
//        }
//    };

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "ShotGot");
        // This location works gotbest if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("ShotGot", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
            Log.d("IMG", "File created at" + mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    /**
     * Through Socket.io to api.shotgot.com API
     */
    private void attemptSend(byte[] data) {
        String imgString = Base64.encodeToString(data, Base64.NO_WRAP);
        Log.d("SOCKET", "[0] Going to connect to remote server");
        mSocket.connect();
        Log.d("SOCKET", "[1] Connected to remote server. Going to send: " + imgString);
        mSocket.emit("EVENT_MESSAGE", imgString);
        Log.d("SOCKET", "[2] Already sent data to remote server.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shot, container, false);

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Socket init
        SocketPic appSocket = new SocketPic();
        mSocket = appSocket.getSocket();

        // Create our Preview view and set it as the content of our activity.
        if (mCamera != null) {
            mCameraView = new CameraView(getActivity().getBaseContext(), mCamera);
            mPicture = new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    //Todo get the base 64 string
                    //TODO JSONObject dataJSON = new JSONObject();
//                    try {
//                        attempPOST(data);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    attemptSend(data);
                    /**String imgString = Base64.encodeToString(data,Base64.NO_WRAP);
                     byte[] b64toByte = imgString.getBytes();
                     //                    String compRes = "Bytes[]:"+data.toString()+
                     //                            "\n\t- Length: "+data.length+
                     //                            "\n- Base64: "+imgString.toString()+
                     //                            "\n\t- Base64 length: "+imgString.length()+
                     //                            "\nBack to Bytes[]:"+b64toByte.toString()+
                     //                            "\n\t- Length: "+b64toByte.length;
                     ////                    mTags =  getTagsFromImg(data);
                     //                    Toast.makeText(getContext(),compRes,Toast.LENGTH_LONG).show();
                     Log.d("API","Bytes[]:"+data.toString());
                     Log.d("API","Length:"+data.length);
                     Log.d("API","b64[]:"+imgString.toString());
                     Log.d("API","Length:"+imgString.length());
                     Log.d("API","b64ToByte[]:"+b64toByte.toString());
                     Log.d("API","Length:"+b64toByte.length);

                     Ideally would be @Deprecated as per URI instead
                     File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                     if (pictureFile == null) {
                     Log.d(TAG, "Error creating media file, check storage permissions: ");
                     return;
                     }

                     try {
                     FileOutputStream fos = new FileOutputStream(pictureFile);
                     Log.d(TAG, "File succesfully created: ");
                     fos.write(data);
                     fos.close();
                     } catch (FileNotFoundException e) {
                     Log.d(TAG, "File not found: " + e.getMessage());
                     } catch (IOException e) {
                     Log.d(TAG, "Error accessing file: " + e.getMessage());
                     }*/
                }
            };

            FrameLayout camera_view = (FrameLayout) view.findViewById(R.id.camera_preview);
            camera_view.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // get an image from the camera
                            getPicResults();
                        }
                    }
            );
            camera_view.addView(mCameraView);//add the SurfaceView to the layout

            // Trap the capture button.
            Button captureButton = (Button) view.findViewById(R.id.button_capture);
            captureButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // get an image from the camera
                            getPicResults();
                        }
                    }
            );
        }
        return view;
    }

    private void getPicResults() {
        mCamera.takePicture(null, null, mPicture);

        /**Play camera's Beep sound*/
        MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.cam_beep);
        mp.start();
    }

}
