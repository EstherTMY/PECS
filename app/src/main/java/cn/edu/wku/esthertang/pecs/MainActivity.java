package cn.edu.wku.esthertang.pecs;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


import cn.edu.wku.esthertang.pecs.helper.SelectImageActivity;
import cn.edu.wku.esthertang.pecs.helper.TTSController;

public class MainActivity extends AppCompatActivity {
    // The button to select an image
    private Button mButtonSelectImage;
    private Button mButtonSelectImage1;
    private Button mButtonSelectImage2;
    // Flag to indicate which task is to be performed.
    private static final int REQUEST_SELECT_IMAGE = 0;
    // The URI of the image selected to detect.
    private Uri mImageUri;

    ImageButton itemButton;
    ImageButton characterButton;
    ImageButton actionButton;

    String category="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButtonSelectImage = (Button) findViewById(R.id.addNewButton);
        mButtonSelectImage1 = (Button) findViewById(R.id.addNewCharacterButton);
        mButtonSelectImage2 = (Button) findViewById(R.id.addNewActionButton);
        itemButton = (ImageButton) findViewById(R.id.itemButton);
        characterButton = (ImageButton) findViewById(R.id.characterButton);
        actionButton = (ImageButton) findViewById(R.id.actionButton);
        TTSController ttsController = TTSController.getInstance(this.getApplicationContext());
        ttsController.init();
        ttsController.playText("欢迎进入");
        mButtonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "物品";
                selectImage(v);
            }
        });
        mButtonSelectImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "人物";
                selectImage(v);
            }
        });
        mButtonSelectImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "行为";
                selectImage(v);
            }
        });

        itemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,CategoryItem.class);
                startActivity(intent);
            }
        });
        characterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("Item", "人物");
                //intent.putExtra("category",category);
                intent.setClass(MainActivity.this, SpecificItem.class);
                startActivity(intent);
            }
        });
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("Item", "行为");
                //intent.putExtra("category",category);
                intent.setClass(MainActivity.this, SpecificItem.class);
                startActivity(intent);
            }
        });

    }

    public void selectImage(View view) {

        Intent intent;
        intent = new Intent(MainActivity.this, SelectImageActivity.class);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    // Called when image selection is done.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("DescribeActivity", "onActivityResult");
        switch (requestCode) {
            case REQUEST_SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    mImageUri = data.getData();
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, ImageDescribe.class);
                    intent.putExtra("category",category);
                    intent.setData(mImageUri);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

}
