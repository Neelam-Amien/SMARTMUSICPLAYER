package com.example.smartmusicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.smartmusicplayer.facedetection.LivePreviewActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.smartmusicplayer.R.layout.list_item;

public class MainActivity extends AppCompatActivity {

    private ListView recyclerView;
    private String[] items;
    private ArrayList<String> listitems;
    private ArrayAdapter<String> stringArrayAdapter;
    private ArrayAdapter<String> adapter;
    private AutoCompleteTextView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Smart Music Player");
        search=findViewById(R.id.search);
        search.setFocusableInTouchMode(true);
        search.requestFocus();

        recyclerView=findViewById(R.id.recyclerView);

        if( permissions())
        {
            viewList();
            adapter=new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,items);
        }
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().equals("")){
                    viewList();
                }
                else{
                    search.setThreshold(1);
                    search.setAdapter(adapter);
                    searchitem(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



    }
    private void searchitem(String toString) {
        for(String item: items){
            if(!item.contains(toString)){
                listitems.remove(item);
            }
        }
        stringArrayAdapter.notifyDataSetChanged();
    }



    public ArrayList<File> list (File file){

        ArrayList<File> arraylist=new ArrayList<>();

        File[] files=file.listFiles();

        for(File singleFile: files){
            if(singleFile.isDirectory() && !singleFile.isHidden()){
                arraylist.addAll(list(singleFile));
            }
            else{
                if(singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")  ){
                    arraylist.add(singleFile);
                }
            }

        }

        return arraylist;
    }

    public void viewList(){

        final ArrayList<File> mySongs=list(Environment.getExternalStorageDirectory());
        items=new String[mySongs.size()];

        for(int i=0; i<mySongs.size();i++){
            items[i]= mySongs.get(i).getName().replace(".mp3","")/*.replace(".wav","")*/;
        }

        listitems=new ArrayList<>(Arrays.asList(items));
        stringArrayAdapter=new ArrayAdapter<>(this,list_item,R.id.snam,listitems);
        recyclerView.setAdapter(stringArrayAdapter);
        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String songname=recyclerView.getItemAtPosition(i).toString();

                Intent intent=new Intent(MainActivity.this,playMusic.class);
                intent.putExtra("songs",mySongs);
                intent.putExtra("songname",songname);
                intent.putExtra("pos",i);
                startActivity(intent);
            }
        });



    }


    private boolean permissions(){
        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.READ_EXTERNAL_STORAGE )
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ){

            requestt();
        }
        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.READ_EXTERNAL_STORAGE )
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED ){
            return true;
        }
        else{

            requestt();
            return false;
        }

    }

    private void requestt()     {
        ActivityCompat.requestPermissions(this,new String[]
                {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,

                },1
        );

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater  inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.search1:
                search.setVisibility(View.VISIBLE);
                return true;
        }
        switch (item.getItemId()){
            case R.id.camera:
                startActivity(new Intent(MainActivity.this, LivePreviewActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
