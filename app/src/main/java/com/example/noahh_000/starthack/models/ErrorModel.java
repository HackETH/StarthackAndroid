package com.example.noahh_000.starthack.models;

import android.content.Context;
import android.util.Log;

/**
 * Created by NoahH_000 on 03.05.2016.
 */
public class ErrorModel {
    public static void e(String classTag, String message)
    {
        Log.v(classTag, message);
    }
}
