package com.example.noahh_000.starthack.models;

import java.util.List;

/**
 * Created by NoahH_000 on 12.03.2016.
 */
public class Country extends ListElement{
    public String original;
    public String international;
    public String language;

    public Country()
    {
    }

    public String getName()
    {
        return "country";
    }

    public String getShow()
    {
        return this.original;
    }

    public String getData()
    {
        return this.international;
    }

    public String getLanguage()
    {
        return this.language;
    }

    public String[] getValues()
    {
        return new String[]{"original", "international", "language"};
    }

    public void setValue(String name, String value)
    {
        if (name == "original")
            setOriginal(value);
        else if (name == "international")
            setInternational(value);
        else if (name == "language")
            setLanguage(value);
    }

    private void setInternational(String international)
    {
        this.international = international;
    }

    private void setOriginal(String original)
    {
        this.original = original;
    }

    private void setLanguage (String language)
    {
        this.language = language;
    }
}
