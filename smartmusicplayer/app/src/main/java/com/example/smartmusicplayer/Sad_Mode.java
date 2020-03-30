package com.example.smartmusicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import static com.example.smartmusicplayer.R.layout.list_item;

public class Sad_Mode extends AppCompatActivity {
    private ListView recyclerView;
    private ArrayAdapter<String> stringArrayAdapter;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listitems;
    private ArrayList<String> stringList;
    private ArrayList<File> mySongs;
    private String wordsString;
    private ArrayList<File> filterList= new ArrayList<>();
    private TextView noItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sad__mode);
        setTitle("Sad Mode");
        recyclerView=findViewById(R.id.recyclerView1);

        SharedPreferences sharedPreferences=getSharedPreferences("Sadmode",MODE_PRIVATE);
        wordsString=sharedPreferences.getString("song","");

        noItem=findViewById(R.id.noItem);
        if(!wordsString.equals("")){

//            Intent intent=getIntent();
//            Bundle bundle=intent.getExtras();
//            mySongs=(ArrayList) bundle.getParcelableArrayList("allSongs");
            mySongs=Bridge.mySongs;


            loadList();


            String type=getIntent().getStringExtra("key");
            if(type!=null && type.equals("face")){
                String songname=recyclerView.getItemAtPosition(0).toString();
                sendInfoPlay(songname,0);
            }
        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String songname=recyclerView.getItemAtPosition(i).toString();
                sendInfoPlay(songname,i);
            }
        });

        }
        else{

            noItem.setVisibility(View.VISIBLE);
        }
    }

    private void sendInfoPlay(String songname,int pos){
        Intent intent=new Intent(Sad_Mode.this,PlayMusic.class);
        intent.putExtra("songs",filterList);
        intent.putExtra("songname",songname);
        intent.putExtra("pos",pos);
        startActivity(intent);
    }

    private void loadList(){
        if(!wordsString.equals("")){


        String[]    items=wordsString.split(",");
        stringList=new ArrayList<>();
        for (int i = 0; i <items.length ; i++) {
            stringList.add(items[i]);
        }

        listitems=new ArrayList<>(Arrays.asList(items));
        stringArrayAdapter=new ArrayAdapter<>(this, list_item,R.id.snam,listitems);
        recyclerView.setAdapter(stringArrayAdapter);
        adapter=new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,stringList);


        for (int i1 = 0; i1 <stringList.size() ; i1++) {
            for (int i = 0; i <mySongs.size() ; i++) {
                if(mySongs.get(i).getName().equals(stringList.get(i1)+".mp3")){
                    filterList.add(mySongs.get(i));
                    break;
                }
            }
        }
        }
        else{
            noItem.setVisibility(View.VISIBLE);
        }
    }
    public void more(final View view){

        PopupMenu popupMenu = new PopupMenu(getApplicationContext(),view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.remove:
                        SharedPreferences sharedPreferences=getSharedPreferences("Sadmode",MODE_PRIVATE);
                        int pos=recyclerView.getPositionForView(view);
                        String name=recyclerView.getItemAtPosition(pos).toString();

                        wordsString=wordsString.replace(name,"");
                        if(wordsString.contains(",,")){
                            wordsString=wordsString.replace(",,",",");
                        }

                        char first=wordsString.charAt(0);
                        if(String.valueOf(first).equals(",")){
                            wordsString=wordsString.replace(",","");
                        }
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("song",wordsString);
                        editor.apply();
                        //mySongs.remove(pos);
                        loadList();
                        Toast.makeText(getApplicationContext(),name+" Song removed",Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.clear,menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
            switch (item.getItemId()){
                case R.id.clear:{
                    SharedPreferences sharedPreferences=getSharedPreferences("Sadmode",MODE_PRIVATE);
                    sharedPreferences.edit().clear().apply();
                    loadList();
                    recyclerView.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(),"Playlist cleared",Toast.LENGTH_SHORT).show();
                }
            }


        return super.onOptionsItemSelected(item);
    }


}
