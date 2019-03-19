package com.talhababa.artbookapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<Bitmap> bitmaplist;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_art,menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = new Intent(getApplicationContext(),Main2Activity.class);
        intent.putExtra("info","new");
        startActivity(intent);

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.listview);

        final ArrayList<String> namelist = new ArrayList<>();
        bitmaplist = new ArrayList<>();

        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,namelist);
        listView.setAdapter(adapter);

        try {
            Main2Activity.database = this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
            Main2Activity.database.execSQL("CREATE TABLE IF NOT EXISTS arts (name VARCHAR,image BLOB)");

            Cursor cursor = Main2Activity.database.rawQuery("SELECT * FROM arts",null);
            int nameindex = cursor.getColumnIndex("name");
            int imageindex = cursor.getColumnIndex("image");
            cursor.moveToFirst();

            while (cursor != null){
                namelist.add(cursor.getString(nameindex));
                byte [] bytarray = cursor.getBlob(imageindex);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytarray,0,bytarray.length);
                bitmaplist.add(bitmap);

                cursor.moveToNext();
                adapter.notifyDataSetChanged();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),Main2Activity.class);
                intent.putExtra("info","old");
                intent.putExtra("name",namelist.get(position));
                intent.putExtra("position",bitmaplist.get(position));
                startActivity(intent);
            }
        });

    }
}
