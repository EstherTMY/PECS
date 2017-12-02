package cn.edu.wku.esthertang.pecs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.app.AlertDialog;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechListener;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

import cn.edu.wku.esthertang.pecs.helper.ImageHelper;
import cn.edu.wku.esthertang.pecs.helper.TTSController;

public class ImageDescribe extends AppCompatActivity{

    // Flag to indicate which task is to be performed.
    private static final int REQUEST_SELECT_IMAGE = 0;

    // The button to select an image
    private Button mButtonSelectImage;
    final Context context = this;
    // The image selected to detect.
    private Bitmap mBitmap;
    String category;

    private static final String TAG = "FileUtils";

    public static final int FLAG_SUCCESS = 1;//创建成功
    public static final int FLAG_EXISTS = 2;//已存在
    public static final int FLAG_FAILED = 3;//创建失败
    TTSController ttsController;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_describe);
        final Intent intent = getIntent();
        ttsController = TTSController.getInstance(this.getApplicationContext());
        ttsController.init();



        // If image is selected successfully, set the image URI and bitmap.
        Uri mImageUri = intent.getData();
        category = intent.getStringExtra("category");
        ttsController.playText("这是什么"+category);



        mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                mImageUri, getContentResolver());
        if (mBitmap != null) {
            // Show the image on screen.
            ImageView imageView = (ImageView) findViewById(R.id.selectedImage);
            imageView.setImageBitmap(mBitmap);

                        // Add detection log.
            Log.d("DescribeActivity", "Image: " + mImageUri + " resized to " + mBitmap.getWidth()
                                + "x" + mBitmap.getHeight());
            // get prompts.xml view
            LayoutInflater li = LayoutInflater.from(context);
            View promptsView = li.inflate(R.layout.prompts, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.editTextDialogUserInput);
            TextView textView = (TextView) promptsView
                    .findViewById(R.id.textView1);
            textView.setText("这是什么"+category);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // get user input and set it to result
                                    // edit text

                                    String result = userInput.getText().toString();
                                    SimpleDateFormat timesdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    //SimpleDateFormat filesdf = new SimpleDateFormat("yyyy-MM-dd HHmmss"); //文件名不能有：
                                    String FileTime =timesdf.format(new Date()).toString();//获取系统时间
                                    String filename = FileTime.replace(":", "");
                                    filename = filename.replace(" ", "");
                                    filename = filename.replace("-", "");
//                                    createDir("PECS");
                                    SaveImage(mBitmap,category,result,filename);
                                    Intent intent1 = new Intent();
                                    intent1.setClass(ImageDescribe.this, MainActivity.class);
                                    startActivity(intent1);
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                    Intent intent = new Intent();
                                    intent.setClass(ImageDescribe.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();


        }

    }
    private void SaveImage(Bitmap finalBitmap,String category,String itemName, String fileName) {

        // use "File.separator" instead of "/"
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + category + "-" + itemName +"-"+ fileName+".jpg");

//create the file
        try {
            file.createNewFile();
            //text you want to write to your file
//check if file exists
            if(file.exists()){
                FileOutputStream fo = new FileOutputStream(file);
                //write the data
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 40, fo);

                //close to avoid memory leaks
                fo.close();

                //give a log message that the file was created with "text"
                System.out.println("file created: "+file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
