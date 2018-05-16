package cn.edu.wku.esthertang.pecs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.edu.wku.esthertang.pecs.helper.TTSController;

public class DemandAsk extends AppCompatActivity {
    ImageView itemImg;
    ImageView characterImg;
    ImageView actionImg;
    TextView itemText;
    TextView characterText;
    TextView actionText;
    Button playButton;
    Button backButton;
    Button shareButton;

    String item;
    String character;
    String action;

    TTSController ttsController;
    private static final String APP_ID = "wx96b077ef1d8a52f5";
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demand_ask);
        ttsController = TTSController.getInstance(this.getApplicationContext());
        ttsController.init();
        regToWx();


        //Item itemObject = new Item();

        itemImg = (ImageView) findViewById(R.id.item);
        actionImg = (ImageView) findViewById(R.id.action);
        characterImg = (ImageView) findViewById(R.id.character);
        itemText = (TextView) findViewById(R.id.itemText);
        characterText = (TextView) findViewById(R.id.characterText);
        actionText = (TextView) findViewById(R.id.actionText);
        playButton = (Button) findViewById(R.id.playButton);
        backButton = (Button) findViewById(R.id.backButton);
        shareButton = (Button) findViewById(R.id.shareButton);

        Bundle bundle = this.getIntent().getExtras();
        String category = bundle.getString("category");
        String imgPath = bundle.getString("imgPath");
        deleteImg(getImagePathFromSD(category));
        Bitmap imgBitmap = BitmapFactory.decodeFile(imgPath);
        SaveImage(category,imgBitmap,itemName(imgPath));

        if(!getImagePathFromSD("item").equals("")){
            String itemPath = getImagePathFromSD("item");
            Log.d("Path",itemPath);
            Bitmap itemBitmap = BitmapFactory.decodeFile(itemPath);
            itemImg.setImageBitmap(itemBitmap);
            item = itemName(itemPath);
            itemText.setText(item);
        }
        if(!getImagePathFromSD("character").equals("")){
            String characterPath = getImagePathFromSD("character");
            Bitmap characterBitmap = BitmapFactory.decodeFile(characterPath);
            characterImg.setImageBitmap(characterBitmap);
            character = itemName(characterPath);
            characterText.setText(character);
        }
        if(!getImagePathFromSD("action").equals("")){
            String actionPath = getImagePathFromSD("action");
            Bitmap characterBitmap = BitmapFactory.decodeFile(actionPath);
            actionImg.setImageBitmap(characterBitmap);
            action = itemName(actionPath);
            actionText.setText(action);
        }


        itemImg = (ImageView) findViewById(R.id.item);
        itemImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.setClass(DemandAsk.this,CategoryItem.class);
                startActivity(intent1);
            }
        });
        characterImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.putExtra("Item","人物");
                intent1.setClass(DemandAsk.this,SpecificItem.class);
                startActivity(intent1);
            }
        });
        actionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.putExtra("Item","行为");
                intent1.setClass(DemandAsk.this,SpecificItem.class);
                startActivity(intent1);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.setClass(DemandAsk.this,MainActivity.class);
                startActivity(intent1);
            }
        });

        if(!getImagePathFromSD("item").equals("")&&!getImagePathFromSD("character").equals("")&&!getImagePathFromSD("action").equals("")){
            playButton.setClickable(true);
            shareButton.setClickable(true);
        }
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ttsController.playText(characterText.getText().toString()+actionText.getText().toString()+itemText.getText().toString());
            }
        });
        shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareText(characterText.getText().toString()+actionText.getText().toString()+itemText.getText().toString());
                    }
                });


        //SaveImage(category,itemBitmap);


    }

    private void regToWx(){
        api = WXAPIFactory.createWXAPI(this,APP_ID,true);
        api.registerApp(APP_ID);
    }
    private void shareText(String text){
        WXTextObject textObject = new WXTextObject();
        textObject.text = text;
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObject;
        msg.description = text;
        SendMessageToWX.Req req = new SendMessageToWX.Req();

        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }

    private String itemName(String fName){
        String[] categories = fName.split("/");
        String fileName = categories[categories.length-1];
        String[] fileNames = fileName.split("-");
        if(fileNames[0].equals("item")||fileNames[0].equals("character")||fileNames[0].equals("action")){
            String itemName = fileNames[1];
            String name = itemName .substring(0,itemName.lastIndexOf("."));
            return name;
        }else {
            String itemName = fileNames[1];
            return itemName;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteImg(getImagePathFromSD("item"));
        deleteImg(getImagePathFromSD("character"));
        deleteImg(getImagePathFromSD("action"));

    }

    private String getImagePathFromSD(String category) {
        // 图片列表
        String imagePath = "";
        // 得到sd卡内image文件夹的路径   File.separator(/)
        try {
            String filePath = Environment.getExternalStorageDirectory().toString();
            // 得到该路径文件夹下所有的文件

            File fileAll = new File(filePath);
            File[] files = fileAll.listFiles();
            // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (checkIsImageFile(file.getPath())) {
                    String[] categories = file.getPath().split("/");
                    String fileName = categories[categories.length - 1];
                    if(fileName.startsWith(category)){
                        imagePath = file.getPath();
                    }
                }
            }
        } catch (Exception e) {

        }
        // 返回得到的图片列表
        return imagePath;
    }
    private boolean checkIsImageFile(String fName) {
        boolean isImageFile = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals("jpg") || FileEnd.equals("png") || FileEnd.equals("gif")
                || FileEnd.equals("jpeg") || FileEnd.equals("bmp")) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }


    private void SaveImage(String category, Bitmap mBitmap, String fName) {

        // use "File.separator" instead of "/"
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + category + "-" + fName  +".jpg");

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
    boolean deleteImg(String selectedFilePath){
        File file = new File(selectedFilePath);
        boolean deleted = file.delete();
        return deleted;
    }
    void alter (){

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompts, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        TextView textView1 = (TextView) findViewById(R.id.textView1);
        textView1.setText("种类");

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text

                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
