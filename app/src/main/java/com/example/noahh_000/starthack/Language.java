package com.example.noahh_000.starthack;

/**
 * Created by NoahH_000 on 12.03.2016.
 */
public class Language {
    public String original;
    public String international;

    public Language(String original, String international)
    {
        this.original = original;
        this.international = international;
    }

    public String getOriginal()
    {
        return this.original;
    }

    public String getInternational()
    {
        return this.international;
    }
}
