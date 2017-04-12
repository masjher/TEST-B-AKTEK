package com.example.fileexplorer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    ListView lv;
    ArrayList<String> al;
    File[] filtered;
    File f;
    File[] files;
    int i,h;
    String current_path="/";
    ArrayAdapter<String> adapter;
    long time[]=new long[2];
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.lv);
        context=this.context;
        time[0]=1;
        time[1]=2;
        display();//start with displaying current path(root)


        lv.setOnScrollListener(new AbsListView.OnScrollListener() {//for the colors
            @Override
            public void onScrollStateChanged(AbsListView view,
                                             int scrollState) {  }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;

                View listItem;
                for(int k=0;k<lv.getLastVisiblePosition()-lv.getFirstVisiblePosition()+1;k++)
                {
                    listItem = lv.getChildAt(k);
                    //if(lv.getFirstVisiblePosition()-k==0)
                    if(lv.getFirstVisiblePosition()==0 &&k==0){listItem.setBackgroundColor(Color.WHITE);continue;}
                    if(!filtered[k+lv.getFirstVisiblePosition()].isDirectory())
                        listItem.setBackgroundColor(Color.WHITE);
                    else listItem.setBackgroundColor(Color.YELLOW);
                }
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {   //actions when pressing a folder or the(...) special entry to go up
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(filtered[position]!=null) {
                    if (filtered[position].isDirectory())
                    {
                        current_path += filtered[position].toString().substring(current_path.length());//Pt 2 in Behaviour list
                        display();
                    }//no else statement implies no action when pressing files, Pt 3 in Behaviour list
                }else {
                    int f=current_path.lastIndexOf('/');
                    String F=Integer.toString(f);
                    if(!(f==0&&current_path.length()==1)){
                        current_path=current_path.substring(0,f);           //Pt 1 in Behaviour list
                        if(current_path.length()==0)
                            current_path="/";
                        display();
                    }
                }
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {//goes to next activity showing repeated words
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i=new Intent(MainActivity.this, unique_words.class);
                i.putExtra("directory", filtered[position].toString());     //Pt 4 in Behaviour list
                startActivity(i);
                return false;
            }});
    }
    public void display()   //main method that checks current path and displays all its children
    {

        al = new ArrayList<String>();
        i = 0;
        h = 0;
        int length=current_path.length();
        if(!current_path.equals("/"))
            length++;
        f = new File(current_path);
        files = f.listFiles();
        if (files != null) {
            filtered = new File[files.length+1];
            for (File inFile : files) {
                if (inFile.isDirectory()) {
                    al.add(inFile.toString().substring(length));
                    filtered[i] = inFile;
                    i++;
                }
            }
            Collections.sort(al);
            File temp;
            for (int j = 0; j < i - 1; j++) {
                for (int k = 0; k < i - 1; k++) {
                    if (filtered[k].toString().compareTo(filtered[k + 1].toString()) > 0) {
                        temp = filtered[k];
                        filtered[k] = filtered[k + 1];
                        filtered[k + 1] = temp;
                    }
                }
            }
            int f = 7;//totally useless todo remove
            for (File inFile : files) {
                if (!inFile.isDirectory()) {
                    al.add(inFile.toString().substring(length));
                    filtered[i + h] = inFile;
                    h++;
                }
            }
            f = 1;
            Collections.sort(al.subList(i, i + h));
            for (int j = 0; j < h - 1; j++) {
                for (int k = 0; k < h - 1; k++) {
                    if (filtered[k + i].toString().compareTo(filtered[k + i + 1].toString()) > 0) {
                        temp = filtered[k + i];
                        filtered[k + i] = filtered[k + i + 1];
                        filtered[k + i + 1] = temp;
                    }
                }
            }
        }
        al.add(0,"...");
        File temp;
        if(files != null) {
            temp = filtered[files.length];
            for (int l = files.length; l > 0; l--)
                filtered[l] = filtered[l - 1];
            filtered[0] = temp;
        }
        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, al);
        lv.setAdapter(adapter);
    }
    @Override
    public void onBackPressed() //shows parent folder when pressing the back button, if the current view shows the root, it will give a cute message
    {
        int f=current_path.lastIndexOf('/');
        String F=Integer.toString(f);
        if(f==0&&current_path.length()==1) {
            if(time[0]>time[1])
                time[1]= System.nanoTime();
            else time[0]=System.nanoTime();
            if((time[1]-time[0]<1000000000&&time[1]-time[0]>0)||(time[0]-time[1]<1000000000&&time[1]-time[0]<0))
            {
                final Toast toast = Toast.makeText(getApplicationContext(), "Long press to exit", Toast.LENGTH_SHORT);
                toast.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 500);
            }
            else
            {
                final Toast toast = Toast.makeText(getApplicationContext(), "This is the root", Toast.LENGTH_SHORT);
                toast.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 250);
            }
        }
        else {
            current_path=current_path.substring(0,f);           //Pt 1 in Behaviour list
            if(current_path.length()==0)
                current_path="/";
            display();
        }
    }
    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {//long back button press returns us to the homepage
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

}
