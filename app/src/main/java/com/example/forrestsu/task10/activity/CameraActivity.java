package com.example.forrestsu.task10.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.forrestsu.task10.R;
import com.example.forrestsu.task10.utils.ChoosePhoto;
import com.example.forrestsu.task10.utils.ImageCompress;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.getExternalStorageDirectory;

public class CameraActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "CameraActivity";

    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;

    private Button takePhotoBT, choosePhotoBT, copyBT;
    private ImageView photoIV;
    private EditText folderNameET;

    private Uri imageUri;

    private String savePath;
    private String saveName = "output_image.jpg";
    String imagePath = "";

    private ImageCompress imageCompress;
    private ChoosePhoto choosePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        requestPermission();
        init();
    }

    /*
    初始化
     */
    public void init() {
        //findView
        takePhotoBT = (Button) findViewById(R.id.bt_take_photo);
        choosePhotoBT = (Button) findViewById(R.id.bt_choose_photo);
        copyBT = (Button) findViewById(R.id.bt_copy);
        photoIV = (ImageView) findViewById(R.id.iv_photo);
        folderNameET = (EditText) findViewById(R.id.et_folder_name);

        //setListener
        takePhotoBT.setOnClickListener(this);
        choosePhotoBT.setOnClickListener(this);
        copyBT.setOnClickListener(this);

        //init
        savePath = getExternalCacheDir().getAbsolutePath();
        //savePath = Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM).getAbsolutePath();
        imageCompress = new ImageCompress();
        choosePhoto = new ChoosePhoto();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.bt_take_photo:
                takePhoto();
                /*
                //检查权限，拍照
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(CameraActivity.this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        CameraActivity.this.requestPermissions(
                                new String[]{Manifest.permission.CAMERA}, 1);
                    } else {
                        takePhoto();
                    }
                } else {
                    takePhoto();
                }
                */
                break;
            case R.id.bt_choose_photo:
                openAlbum();
                break;
            case R.id.bt_copy:
                if (!TextUtils.isEmpty(imagePath)) {
                    String folderName = folderNameET.getText().toString();
                    String newPath = getExternalStorageDirectory().getAbsolutePath() + File.separator + folderName;
                    Log.i(TAG, "onClick: newPath:" + newPath);
                    File file = new File(newPath);
                    //如果目录不存在，创建目录
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    try {
                        //源文件
                        FileInputStream fis = new FileInputStream(imagePath);
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        //输出文件
                        imagePath = imagePath.trim();
                        FileOutputStream fos = new FileOutputStream(newPath + File.separator
                                + imagePath.substring(imagePath.lastIndexOf("/") + 1));
                        BufferedOutputStream bos = new BufferedOutputStream(fos);
                        try {
                            byte[] data = new byte[1024];
                            int len;
                            while ((len = bis.read(data)) != -1) {
                                bos.write(data, 0, len);
                            }
                            bos.flush();
                            bis.close();
                            bos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
                break;
            default:
                break;
        }
    }

    //请求权限
    public void requestPermission() {
        List<String> permissionList = new ArrayList<String>();
        if (ContextCompat.checkSelfPermission(CameraActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(CameraActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String permissions[] = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(CameraActivity.this, permissions, 1);
        }
    }


    /*
    拍照
    */
    public void takePhoto() {
        //创建File对象，用于存储拍摄后的照片
        File outPutImage =  new File(savePath, saveName);
        try {
            if (outPutImage.exists()) {
                outPutImage.delete();
            } else {
                outPutImage.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(this,
                    "com.example.forrestsu.task10.fileprovider", outPutImage);
        } else {
            imageUri = Uri.fromFile(outPutImage);
        }
        //启动相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    /*
    打开相册
     */
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //File.separator:在 UNIX 系统上，此字段的值为 '/'；在 Microsoft Windows 系统上，它为 '\'
                    imagePath = savePath + File.separator + saveName;
                    /*
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        photoIV.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    */
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //判断手机版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        imagePath = choosePhoto.handleImageOnKitKat(this, data);
                        Log.i(TAG, "onActivityResult: path:" + imagePath);
                    } else {
                        imagePath = choosePhoto.handleImageBeforeKitKat(this, data);
                    }
                }
                break;
            default:
                break;
        }

        if (!imagePath.equals("")){
            Glide.with(this)
                    .load(imagePath)
                    .into(photoIV);
            /*
            imageCompress.compressWithLuban(CameraActivity.this, imagePath);
            imageCompress.setLubanListener(new ImageCompress.LubanListener() {
                @Override
                public void onSuccess(String newPath) {
                    if (newPath != null) {
                        Bitmap bitmap = BitmapFactory.decodeFile(newPath);
                        photoIV.setImageBitmap(bitmap);
                    } else {
                        Toast.makeText(CameraActivity.this,
                                "failed to get image", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailed() {
                    //
                }
            });
            */
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(CameraActivity.this,
                                    "请授予权限", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(CameraActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }
}
