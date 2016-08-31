package com.example.noahh_000.starthack.models;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by NoahH_000 on 12.03.2016.
 */
public class CountryParser extends ListParser {
    private static String TAG = "country parser";

    public CountryParser(Context context)
    {
        super(context);
    }

    public String getFile()
    {
        String lang = Locale.getDefault().getLanguage();
        if (lang == "de")
            return "countries-de.plist";
        else if (lang == "en")
            return "countries.plist";
        else
            return "countries.plist";
    }

    public String getName()
    {
        return "country";
    }

    public ListElement createElement()
    {
        return new Country();
    }

    public ArrayList<Country> getCountryList()
    {
        ArrayList<Country> countryList = new ArrayList<Country>();
        for (ListElement l : this.getList())
            countryList.add((Country)l);
        return countryList;
    }
}