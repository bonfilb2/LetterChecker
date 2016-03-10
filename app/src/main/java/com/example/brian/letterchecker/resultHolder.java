package com.example.brian.letterchecker;

import android.content.res.TypedArray;

import java.util.ArrayList;

/**
 * Created by Noel Gallagher on 10-Mar-16.
 * Arraylist which will obtain it's capacity from length of the library of letters
 */
public class resultHolder {
    private ArrayList<Integer> results = new ArrayList();

    resultHolder(TypedArray t){
        for(int i=0;i<t.length();i++)
        {
            results.add(0);
        }
    }

    public void incrementIndex(int index, int amount){
        int t= results.get(index);
        t = t+amount;
        results.set(index, t);
    }

    public String toString() {
        String s="";
        for (int i = 0; i < results.size(); i++) {
            s = s+"index " + i + " value " + results.get(i) + " ";
        }
        return s;
    }
}
