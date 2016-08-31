package com.example.noahh_000.starthack.models;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by NoahH_000 on 12.03.2016.
 */
public class LanguageParser extends ListParser{
    private static String TAG = "language parser";

    public LanguageParser(Context context)
    {
        super(context);
    }

    public ArrayList<Language> getLanguageList()
    {
        ArrayList<Language> langlist = new ArrayList<Language>();
        for (ListElement l : this.getList())
            langlist.add((Language)l);
        return langlist;
    }

    public ListElement createElement()
    {
        return new Language();
    }

    public String getName()
    {
        return "language";
    }

    public String getFile()
    {
        return "languages.plist";
    }

}