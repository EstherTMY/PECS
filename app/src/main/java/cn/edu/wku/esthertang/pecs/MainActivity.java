package cn.edu.wku.esthertang.pecs;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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
    TextView gpsLocation;
    Button getLocationButton;

    String category="";
    //定位
    //定义数据
    //定位都要通过LocationManager这个类实现
    private LocationManager locationManager;
    private String provider; //是否为网络位置控制器或GPS定位
    //当前可用的位置控制器
    List<String> list;
    LocationManager mLocationManager;
    private Context mContext;
    private boolean isGPSEnabled;
    private static final String APP_ID = "wx0d6d2b0b99c0c11b";
    private IWXAPI api;
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
//        gpsLocation = (TextView) findViewById(R.id.gpsLocation);
        getLocationButton = (Button) findViewById(R.id.getLocation);


            //实例化
            //获取定位服务管理对象
        locationManager = (LocationManager)getSystemService
                (Context.LOCATION_SERVICE);
        //获取当前可用的位置控制器
            list = locationManager.getProviders(true);
            if (list.contains(LocationManager.GPS_PROVIDER)) {
                //是否为GPS位置控制器
                provider = LocationManager.GPS_PROVIDER;
                //gpsLocation.append("GPS位置控制器" + "\n");
            } else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
                //是否为网络位置控制器
                provider = LocationManager.NETWORK_PROVIDER;
                //gpsLocation.append("网络位置控制器" + "\n");
            } else {
                Toast.makeText(this, "请检查网络或GPS是否打开", Toast.LENGTH_LONG).show();
                return;
            }
            getLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    locationManager.requestLocationUpdates(provider, 2000, 10,
                            locationListener);
                    Location location = locationManager.getLastKnownLocation(provider);
                    while (location == null) {
                        location = locationManager.getLastKnownLocation(provider);
                    }
                    updateWithNewLocation(location);
                    locationManager.removeUpdates(locationListener);
//                    if (location != null) {
//                        //获取当前位置，这里只用到了经纬度
//                        String string = "纬度为：" + location.getLatitude() + ",经度为："
//                                + location.getLongitude();
//                        gpsLocation.setText(string);
//                    }
                }
            });



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
                    intent.setClass(MainActivity.this, CategoryItem.class);
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

    private void regToWx(){
        api = WXAPIFactory.createWXAPI(this,APP_ID,true);
        api.registerApp(APP_ID);
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
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        public void onProviderDisabled(String provider) {
            updateWithNewLocation(null);
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
     };
    private void updateWithNewLocation(Location location) {
        String latLongString;
        TextView myLocationText;
        myLocationText = (TextView) findViewById(R.id.gpsLocation);
        if (location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            latLongString = "纬度:" + lat + "\n经度:" + lng + "\n";

            Geocoder gc = new Geocoder(MainActivity.this, Locale.getDefault());
            try {
                // 取得地址相关的一些信息\经度、纬度
                List<Address> addresses = gc.getFromLocation(lat, lng, 1);
                StringBuilder sb = new StringBuilder();
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    sb.append(address.getLocality()).append("\n");
                    latLongString = latLongString + sb.toString();
                }
            } catch (IOException e) {
            }

        } else {
            latLongString = "无法获取地理信息";
        }
        myLocationText.setText("您当前的位置是:\n" + latLongString);
    }



}
