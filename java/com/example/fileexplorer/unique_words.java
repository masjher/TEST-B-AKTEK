package com.example.fileexplorer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class unique_words extends AppCompatActivity {
    ListView lv;
    ArrayList<String> al, al2;
    ArrayAdapter<String> adapter;
    String directory;
    int depth=5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unique_words);
        directory=getIntent().getStringExtra("directory");
        al=new ArrayList<>();
        al2=new ArrayList<>();
        lv = (ListView) findViewById(R.id.lv);
        File file=new File(directory);
        List<File> unsorted=getListFiles(file,depth);
        int count=1;
        String newname, oldname="";
        String special[]=new String[4];
        special[0]=".";
        special[1]="-";
        special[2]=" ";
        special[3]="_";
        int pos[]=new int[4];
        int min = 100, index;

        for(int k=0;k<unsorted.size();k++) {//Separating words
            newname=unsorted.get(k).toString().substring(unsorted.get(k).toString().lastIndexOf('/')+1);
            index=4;
            while(index!=-1) {
                min=100;index=-1;
                pos[0] = newname.indexOf(special[0]);
                pos[1] = newname.indexOf(special[1]);
                pos[2] = newname.indexOf(special[2]);
                pos[3] = newname.indexOf(special[3]);
                for (int i = 0; i < 4; i++)
                    if (pos[i] < min && pos[i] > -1) {
                        min = pos[i];
                        index = i;
                    }
                    if(index==-1)
                        al.add(newname);
                else {
                        if(pos[index]!=0)
                            al.add(newname.substring(0, pos[index]));
                        newname=newname.substring(pos[index]+1);
                    }
            }
        }

        Collections.sort(al);               //sort to catch same words easily
        for(int k=0;k<al.size();k++) {       //counting unique words
            newname=al.get(k);
            if(k!=0) {
                if (newname.equals(oldname)) {
                    count++;
                    continue;
                }
                al2.add(oldname + "\n" + count);
                if(k==al.size()-1)
                    al2.add(newname + "\n" + 1);
                oldname = newname;
                count = 1;
            }else oldname=newname;
        }
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1, al2);//
        lv.setAdapter(adapter);


    }
    private List<File> getListFiles(File parentDir, int depth) {        //gets all files of a certain depth
        if(depth==0) {
            ArrayList<File> l= new ArrayList<>();
            l.add(parentDir);
            return l;
        }
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        if(files!=null)
        for(int k=0;k<files.length;k++)
        {
            if(files[k].isDirectory()) {
                inFiles.addAll(getListFiles(files[k], depth - 1));
                if(inFiles.size()>0) {
                    if (inFiles.get(inFiles.size() - 1) != files[k])
                        inFiles.add(files[k]);
                }else inFiles.add(files[k]);
            }
            else inFiles.add(files[k]);
        }
        return inFiles;
    }
}
