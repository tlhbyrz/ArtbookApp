package com.talhababa.artbookapp;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.jar.Manifest;

public class Main2Activity extends AppCompatActivity {

    ImageView imageview;
    EditText edittext;
    Button button;
    Bitmap selectedImage;
    static SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        imageview = (ImageView) findViewById(R.id.imageView);
        edittext = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);

        Intent intent = getIntent();
        String string  = intent.getStringExtra("info");

        if (string.equalsIgnoreCase("new")){
            edittext.setText("");
            Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.click);
            imageview.setImageBitmap(bitmap);
            button.setVisibility(View.VISIBLE);

        }else{
            String name = intent.getStringExtra("name");
            edittext.setText(name);
            edittext.setEnabled(false);
            int position = intent.getIntExtra("position",0);
            imageview.setImageBitmap(MainActivity.bitmaplist.get(position));
            imageview.setClickable(false);
            button.setVisibility(View.INVISIBLE);
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    public void select(View view){

        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE},2);
        }else{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2) {
            if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        imageview = (ImageView) findViewById(R.id.imageView);

        if (requestCode==1 && data!=null && resultCode==RESULT_OK){
            Uri image = data.getData();
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image);
                imageview.setImageBitmap(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    public void save(View view){
        edittext = (EditText) findViewById(R.id.editText);
        String mesage = edittext.getText().toString();

        ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.PNG,50,outputstream);
        byte[] bytArray = outputstream.toByteArray();

        try {
            database = this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS arts (name VARCHAR,image BLOB)");

            String sqlmesage = "INSERT INTO arts (name,image) VALUES (?,?) ";
            SQLiteStatement sqlitestatement = database.compileStatement(sqlmesage);
            sqlitestatement.bindString(1,mesage);
            sqlitestatement.bindBlob(2,bytArray);
            sqlitestatement.execute();

        }catch (Exception e){
            e.printStackTrace();
        }

        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);

    }

}
