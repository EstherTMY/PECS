package cn.edu.wku.esthertang.pecs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.edu.wku.esthertang.pecs.helper.TTSController;

public class CategoryItem extends AppCompatActivity {
    ListView mShowPathLvItem;
    private List<String> data;
    private ArrayAdapter<String> adapter;
    TextView itemName;
    TextView container;
    List<String> imgPathes;
    GridView listView;
    TTSController ttsController;
    String item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_item);
        ttsController = TTSController.getInstance(this.getApplicationContext());
        ttsController.init();
        mShowPathLvItem = (ListView) findViewById(R.id.lv_show_path_item);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1);
        data = getImagePathFromSD();
        adapter.addAll(data);
        mShowPathLvItem.setAdapter(adapter);
        mShowPathLvItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(CategoryItem.this, "你单击的是第" + (position + 1) + "条数据", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent();
//                intent.putExtra("Item", data.get(position));
//                //intent.putExtra("category",category);
//                intent.setClass(CategoryItem.this, SpecificItem.class);
//                startActivity(intent);
                item = data.get(position);
                itemName = (TextView) findViewById(R.id.itemName);
                itemName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ttsController.playText(itemName.getText().toString());
                    }
                });
                container = (TextView) findViewById(R.id.container);
                itemName.setText(item);
                listView = (GridView) findViewById(R.id.imageList);
                imgPathes = getImagePathFromSD(item);
                String[] pathes = new String[imgPathes.size()];
                for(int i = 0; i<imgPathes.size();i++){
                    pathes[i] = imgPathes.get(i);
                }

                listView.setAdapter(new CategoryItem.ImageListAdapter(CategoryItem.this, pathes));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Toast.makeText(SpecificItem.this, "你单击的是第" + (position + 1) + "条数据", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("imgPath", imgPathes.get(position));
                        if(item.equals("人物")) {
                            bundle.putString("category", "character");
                        }else if(item.equals("行为")){
                            bundle.putString("category", "action");
                        }else{
                            bundle.putString("category", "item");
                        }
                        intent.putExtras(bundle);
                        intent.setClass(CategoryItem.this, DemandAsk.class);
                        startActivity(intent);
                    }
                });
            }
        });

    }
    private List<String> getImagePathFromSD() {
        // 图片列表
        List<String> imagePathList = new ArrayList<String>();
        // 得到sd卡内image文件夹的路径   File.separator(/)
        try {
            String filePath = Environment.getExternalStorageDirectory().toString();
            // 得到该路径文件夹下所有的文件

            File fileAll = new File(filePath);
            File[] files = fileAll.listFiles();
            // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (checkIsImageFile(file.getPath()) && checkIsCategory(file.getPath())) {
                    String[] categories = file.getPath().split("/");
                    String fileName = categories[categories.length - 1];
                    String[] fileNames = fileName.split("-");
                    if(fileNames[0].startsWith("物品")) {
                        imagePathList.add(fileNames[1]);
                    }
                }
            }
        } catch (Exception e) {

        }
        removeDuplicate(imagePathList);
        // 返回得到的图片列表
        return imagePathList;
    }
    private List<String> getImagePathFromSD(String item) {
        // 图片列表
        List<String> imagePathList = new ArrayList<String>();
        // 得到sd卡内image文件夹的路径   File.separator(/)
        try {
            String filePath = Environment.getExternalStorageDirectory().toString();
            // 得到该路径文件夹下所有的文件

            File fileAll = new File(filePath);
            File[] files = fileAll.listFiles();
            // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (checkIsImageFile(file.getPath()) && checkIsCategory(file.getPath(),item,"物品")) {
                    imagePathList.add(file.getPath());
                }
                if(item.equals("人物")||item.equals("行为")){
                    if(checkIsImageFile(file.getPath()) && checkIsCategory(file.getPath(),item)){
                        imagePathList.add(file.getPath());
                    }
                }
            }
        }catch (Exception e){

        }
        // 返回得到的图片列表
        return imagePathList;
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

    private boolean checkIsCategory(String fName) {
        boolean isCategory = false;
        String[] categories = fName.split("/");
        String fileName = categories[categories.length - 1];
        String[] fileNames = fileName.split("-");
//        String folder = fileNames[0];
//        if(fileName.startsWith(category)&& fileNames.length!=0){
//            isCategory = true;
//        }
        if (fileNames.length == 3) {
            isCategory = true;
        }
        return isCategory;
    }

    private boolean checkIsCategory(String fName, String item, String category){
        boolean isCategory = false;
        String[] categories = fName.split("/");
        String fileName = categories[categories.length-1];
        String[] fileNames = fileName.split("-");
        String folder = fileNames[0];
        if(fileNames[1].startsWith(item) && fileNames.length!=0 && fileNames[0].equals(category)){
            isCategory = true;
        }
        return isCategory;
    }
    private boolean checkIsCategory(String fName, String category){
        boolean isCategory = false;
        String[] categories = fName.split("/");
        String fileName = categories[categories.length-1];
        String[] fileNames = fileName.split("-");
        String folder = fileNames[0];
        if(fileNames.length!=0 && fileNames[0].equals(category)){
            isCategory = true;
        }
        return isCategory;
    }
    List<String> removeDuplicate(List<String> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).equals(list.get(i))) {
                    list.remove(j);
                }
            }
        }
        return list;
    }
    public class ImageListAdapter extends ArrayAdapter {
        private Context context;
        private LayoutInflater inflater;

        private String[] imageUrls;

        public ImageListAdapter(Context context, String[] imageUrls) {
            super(context, R.layout.listview_item_image, imageUrls);

            this.context = context;
            this.imageUrls = imageUrls;

            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.listview_item_image, parent, false);
            }

            Glide
                    .with(context)
                    .load(imageUrls[position])
                    .into((ImageView) convertView);

            return convertView;
        }
    }
}
