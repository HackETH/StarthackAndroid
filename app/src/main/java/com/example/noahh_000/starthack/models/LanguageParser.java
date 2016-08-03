package com.example.noahh_000.starthack.models;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by NoahH_000 on 12.03.2016.
 */
public class LanguageParser {
    private static ArrayList<Language> languages;

    public LanguageParser(Context context)
    {
        generateLanguageList(context);
    }
    private static String TAG = "language parser";
    public ArrayList<Language> getLanguages()
    {
        return languages;
    }

    public String OriginalToInternational(String original)
    {
        for (Language lang : languages)
        {
            if (lang.getOriginal().equals(original))
                return lang.getInternational();
        }
        Log.d("languageparser", "languageparser:originaltointernational not found: orig: " + original);
        return original;
    }

    public String InternationalToOriginal(String international)
    {
        for (Language lang : languages)
        {
            if (lang.getInternational().equals(international))
                return lang.getOriginal();
        }
        Log.d("languageparser", "languageparser:internationalToOriginal not found: inter: "+international+ " languages count:"+languages.size());
        return international;
    }


    private static void generateLanguageList(Context context)
    {
        if (languages != null)
            return;
        languages = new ArrayList<Language>();

        try {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlFactoryObject.newPullParser();

            String exampleString = "<language original=\"Deutsch\" international=\"German\"/>\n" +
                    "       <language original=\"中文\" international=\"Mandarin\"/>\n" +
                    "       <language original=\"English\" international=\"English\"/>\n" +
                    "       <language international=\"Swedish\" original=\"Svenska\"/>\n" +
                    "       <language international=\"Cebuano\" original=\"Sinugboanong Binisaya\"/>\n" +
                    "       <language international=\"Dutch\" original=\"Nederlands\"/>\n" +
                    "       <language international=\"French\" original=\"Français\"/>\n" +
                    "       <language international=\"Russian\" original=\"Русский\"/>\n" +
                    "       <language international=\"Italian\" original=\"Italiano\"/>\n" +
                    "       <language international=\"Spanish\" original=\"Español\"/>\n" +
                    "       <language international=\"Polish\" original=\"Polski\"/>\n" +
                    "       <language international=\"Vietnamese\" original=\"Tiếng Việt\"/>\n" +
                    "       <language international=\"Japanese\" original=\"日本語\"/>\n" +
                    "       <language international=\"Portuguese\" original=\"Português\"/>\n" +
                    "       <language international=\"Serbo Croatian\" original=\"Srpskohrvatski / Српскохрватски\"/>\n" +
                    "       <language international=\"Persian\" original=\"فارسی\"/>\n";
            InputStream stream = new ByteArrayInputStream(exampleString.getBytes(StandardCharsets.UTF_8));
            parser.setInput(stream, null);
            Log.d(TAG, "generateLanguageList:"+stream);
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT)
            {
                String name=parser.getName();
                switch (event){
                    case XmlPullParser.START_TAG:
                        break;

                    case XmlPullParser.END_TAG:
                        if(name.equals("language")){
                            languages.add(new Language(parser.getAttributeValue(null, "original"), parser.getAttributeValue(null, "international")));
                        }
                        break;
                }
                event = parser.next();
            }
        }
        catch (Exception e) {
            ErrorModel.e("language_parser", "error in xml parser " + e.toString());
        }
    }
}