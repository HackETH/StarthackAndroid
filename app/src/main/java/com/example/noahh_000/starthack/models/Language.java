package com.example.noahh_000.starthack.models;

/**
 * Created by NoahH_000 on 12.03.2016.
 */
public class Language extends ListElement{
    public String original;
    public String international;

    public Language()
    {
    }

    public String getShow()
    {
        return this.original;
    }

    public String getData()
    {
        return this.international;
    }

    public String[] getValues()
    {
        return new String[]{"original", "international"};
    }

    public void setValue(String name, String value)
    {
        if (name == "original")
            setOriginal(value);
        else if (name == "international")
            setInternational(value);
    }

    private void setInternational(String international)
    {
        this.international = international;
    }

    private void setOriginal(String original)
    {
        this.original = original;
    }
}
