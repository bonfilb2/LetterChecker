package com.example.brian.letterchecker;

import android.content.res.TypedArray;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Noel Gallagher on 10-Mar-16.
 * Arraylist which will obtain it's capacity from length of the library of letters
 */
public class resultHolder implements Serializable{
    private ArrayList<Integer> temp = new ArrayList();
    private ArrayList<String> results = new ArrayList();
    private TypedArray ta;

    resultHolder(TypedArray t){
        ta = t;
        for(int i=0;i<t.length();i++)
        {
            temp.add(0);
        }
    }

    public void incrementIndex(int index, int amount){
        int t= temp.get(index);
        t = t+amount;
        temp.set(index, t);
    }

    public String toString() {
        String s="";
        for (int i = 0; i < results.size(); i++) {
            s = s+results.get(i);
        }
        return s;
    }

    public ArrayList<String> finalResult(){
        for(int i=0;i<ta.length();i++){
            results.add(ta.getString(i));
            results.add("" + temp.get(i));
        }
        return results;
    }

    public ArrayList<Integer> getPracticeResults(){
        return(temp);
    }

    public resultHolder addArrayLists(ArrayList<Integer> al, resultHolder rh){
        for(int i=0;i<ta.length();i++) {
            rh.incrementIndex(i, al.get(i));
        }
        return rh;
    }
}
