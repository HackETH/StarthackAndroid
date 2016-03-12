package com.example.noahh_000.starthack;

import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by NoahH_000 on 12.03.2016.
 */
public class LanguageParser {
    public static ArrayList<Language> getLanguageList(Context context)
    {
        ArrayList<Language> languages = new ArrayList<Language>();
        try {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlFactoryObject.newPullParser();

            InputStream stream = context.getResources().getAssets().open("languages.plist");

            parser.setInput(stream, null);

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
            Log.d("language_parser", "error in xml parser " + e.toString());
        }

        return languages;
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}