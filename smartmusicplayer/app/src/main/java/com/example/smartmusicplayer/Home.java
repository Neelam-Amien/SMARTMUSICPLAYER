package com.example.smartmusicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.Toast;
import com.example.smartmusicplayer.facedetection.LivePreviewActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.smartmusicplayer.R.layout.list_item;

public class Home extends AppCompatActivity {

    private ListView recyclerView;
    private String[] items;
    private ArrayList<String> listitems;
    private ArrayAdapter<String> stringArrayAdapter;
    private ArrayAdapter<String> adapter;
    private AutoCompleteTextView search;
    private   ArrayList<String> checkBox=new ArrayList<>();
    private ArrayList<File> mySongs;
    private int searchSong=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        setTitle("Smart Music Player");


        recyclerView=findViewById(R.id.recyclerView);
       search=findViewById(R.id.search);
        search.setFocusableInTouchMode(true);
        search.requestFocus();
        if( permissions())
        {
           showSongList();
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

        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               String name=parent.getItemAtPosition(position).toString();
                int po=0;

                for (int i = 0; i <mySongs.size() ; i++) {
                    if(mySongs.get(i).getName().equals(name+".mp3")){
                        po=i;
                        break;
                    }
                }
                Intent intent=new Intent(Home.this,PlayMusic.class);
                intent.putExtra("songs",mySongs);
                intent.putExtra("songname",name);
                intent.putExtra("pos",po);
                startActivity(intent);
                //Toast.makeText(getApplicationContext(),name+po,Toast.LENGTH_SHORT).show();
            }
        });

        Bridge.mySongs=mySongs;

      auto_faceDetection();

    }

    private void auto_faceDetection(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences facePrefrence=getSharedPreferences("face",MODE_PRIVATE);
                String mode=facePrefrence.getString("mode","");
                if (mode.equals("on")){
                    startActivity(new Intent(Home.this, LivePreviewActivity.class));
                }
            }
        },2000);
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
            if(singleFile.isDirectory() /* && !singleFile.isHidden()*/){
                arraylist.addAll(list(singleFile));
            }
            else{
                if(singleFile.getName().endsWith(".mp3") /* || singleFile.getName().endsWith(".wav") */ ){
                    arraylist.add(singleFile);
                }
            }

        }

        return arraylist;
    }

    public void viewList(){

       mySongs=list(Environment.getExternalStorageDirectory());
        items=new String[mySongs.size()];

        for(int i=0; i<mySongs.size();i++){
            items[i]= mySongs.get(i).getName().replace(".mp3","")/*.replace(".wav","")*/;
        }

        listitems=new ArrayList<>(Arrays.asList(items));
        stringArrayAdapter=new ArrayAdapter<>(this, list_item,R.id.snam,listitems);
        recyclerView.setAdapter(stringArrayAdapter);

        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String songname=recyclerView.getItemAtPosition(i).toString();

                Intent intent=new Intent(Home.this,PlayMusic.class);
                intent.putExtra("songs",mySongs);
                intent.putExtra("songname",songname);
                intent.putExtra("pos",i);
                startActivity(intent);
            }
        });
    }
    public void happy(final View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(Home.this,Happy_Mode.class).putExtra("allSongs",mySongs));

            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(),view);
                popupMenu.getMenuInflater().inflate(R.menu.clear, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.clear:
                                SharedPreferences sharedPreferences=getSharedPreferences("Happymode",MODE_PRIVATE);
                                sharedPreferences.edit().clear().apply();
                                Toast.makeText(getApplicationContext(),"Playlist cleared",Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });
                popupMenu.show();
               return true;
            }
        });
    }
    public void sad(final View view){

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this,Sad_Mode.class).putExtra("allSongs",mySongs));
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(),view);
                popupMenu.getMenuInflater().inflate(R.menu.clear, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.clear:
                                SharedPreferences sharedPreferences=getSharedPreferences("Sadmode",MODE_PRIVATE);
                                sharedPreferences.edit().clear().apply();
                                Toast.makeText(getApplicationContext(),"Playlist cleared",Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });
                popupMenu.show();
                return true;
            }
        });
    }

    private void rateUs() {
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/apps/details?id=" + Home.this.getPackageName())));


    }

    private void showSongList(){
        viewList();
        adapter=new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,items);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    showSongList();
                    //Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();

                }
                else{
                   // Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();

                }
                return;
        }
    }

    public  void more(final View view){

        final String[] listItems = getResources().getStringArray(R.array.Mode);
        boolean[] checkedItems = new boolean[listItems.length];
        final ArrayList<Integer> mUserItems = new ArrayList<>();

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Home.this);
        mBuilder.setTitle("Add to Playlist");
        mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {

                if(isChecked){
                    mUserItems.add(position);
                }else{
                    mUserItems.remove((Integer.valueOf(position)));
                }
            }
        });
        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = "";
                String item2;


                for (int i = 0; i < mUserItems.size(); i++) {


                    item = item + listItems[mUserItems.get(i)];
                    item2=listItems[mUserItems.get(i)];
                    checkBox.add(item2);
                }

                int pos=recyclerView.getPositionForView(view);
                String songName=recyclerView.getItemAtPosition(pos).toString();
                SharedPreferences happyPreferences=getSharedPreferences("Happymode",MODE_PRIVATE);
                SharedPreferences sadPreferences=getSharedPreferences("Sadmode",MODE_PRIVATE);
                if(item.contains("Sad")){

                    SharedPreferences.Editor editor=sadPreferences.edit();
                    String wordsString=sadPreferences.getString("song","");
                    StringBuilder stringBuilder=new StringBuilder();
                    if(wordsString!=null){
                        stringBuilder.append(wordsString);
                    }
                    stringBuilder.append(songName);
                    stringBuilder.append(",");
                    if(!wordsString.contains(songName)){
                        editor.putString("song",stringBuilder.toString());
                        editor.apply();
                         Toast.makeText(getApplicationContext(),"Song Added",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Already Added",Toast.LENGTH_SHORT).show();
                    }

                }
                if(item.contains("Happy")){
                    SharedPreferences.Editor editor=happyPreferences.edit();
                    String wordsString=happyPreferences.getString("hsong","");
                    StringBuilder stringBuilder=new StringBuilder();
                    if(wordsString!=null){
                        stringBuilder.append(wordsString);
                    }
                    stringBuilder.append(songName);
                    stringBuilder.append(",");
                    if(!wordsString.contains(songName)){
                        editor.putString("hsong",stringBuilder.toString());
                        editor.apply();
                        Toast.makeText(getApplicationContext(),"Song Added",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Already Added",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater  inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        MenuItem checkable = menu.findItem(R.id.switchOnOffItem);
        SharedPreferences facePrefrence=getSharedPreferences("face",MODE_PRIVATE);
        String mode=facePrefrence.getString("mode","");
        if(mode.equals("on")){
            checkable.setChecked(true);
        }

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.search1:
                if(searchSong==0){
                    search.setVisibility(View.VISIBLE);
                    searchSong++;
                }
                else {
                    search.setVisibility(View.GONE);
                    searchSong=0;
                }
                return true;

            case R.id.switchOnOffItem:
                SharedPreferences facePrefrence=getSharedPreferences("face",MODE_PRIVATE);
                SharedPreferences.Editor editor=facePrefrence.edit();
                if(item.isChecked()){
                    editor.putString("mode","off");
                    editor.apply();
                    item.setChecked(false);
                    Toast.makeText(getApplicationContext(),"Face detection Deactivated",Toast.LENGTH_SHORT).show();
                }
                else{
                    item.setChecked(true);
                    editor.putString("mode","on");
                    editor.apply();
                    Toast.makeText(getApplicationContext(),"Face detection Activated",Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.camera:
                startActivity(new Intent(Home.this, LivePreviewActivity.class));
                return true;

            case R.id.rate:
            case R.id.update:
                rateUs();
                return true;

            case R.id.feedback:
                startActivity(new Intent(Home.this, feedback.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {


        final AlertDialog.Builder dialog = new AlertDialog.Builder(Home.this);
        dialog.setTitle("Do you want to exit?");
        dialog.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                moveTaskToBack(true);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               dialogInterface.dismiss();
            }
        });
        dialog.create();
        dialog.show();

    }
}
