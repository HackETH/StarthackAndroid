package com.example.noahh_000.starthack.models;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NoahH_000 on 12.03.2016.
 */
public abstract class ListParser {
    protected ArrayList<ListElement> elements;

    public ListParser(Context context)
    {
        this.context = context;
        generateList(context);
    }

    private Context context;

    public ArrayList<ListElement> getList()
    {
        return elements;
    }

    public abstract String getName();

    public abstract ListElement createElement();

    public abstract String getFile();

    private static String TAG = "country parser";

    public String ShowToData(String show)
    {
        for (ListElement l : elements)
        {
            if (l.getShow().equals(show))
                return l.getData();
        }
        ErrorModel.e("languageparser", "languageparser:originaltointernational not found: orig: ");
        return "Language not found";
    }

    public String DataToShow(String data)
    {
        for (ListElement l : elements)
        {
            if (l.getData().equals(data))
                return l.getShow();
        }
        ErrorModel.e("languageparser", "languageparser:InternationalToOriginal not found: inter: ");
        return "Language not found";
    }

    private void generateList(Context context)
    {
        if (elements != null)
            return;
        elements = new ArrayList<ListElement>();

        try {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlFactoryObject.newPullParser();

            InputStream stream = context.getAssets().open(this.getFile());
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
                        if(name.equals(this.getName())){
                            ListElement element = createElement();
                            for (String val : element.getValues()) { // Set each Element
                                element.setValue(val, parser.getAttributeValue(null, val));
                            }
                            elements.add(element);
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