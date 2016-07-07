package com.example.noahh_000.starthack.models;

import android.content.Context;
import android.content.Intent;

import com.example.noahh_000.starthack.activities.TranslatorIsReadyActivity;
import com.example.noahh_000.starthack.activities.TranslatorVideoCallActivity;
import com.example.noahh_000.starthack.activities.UserHomeScreenActivity;
import com.example.noahh_000.starthack.activities.UndecidedPickRoleActivity;
import com.example.noahh_000.starthack.activities.TranslatorLanguagePickerActivity;
import com.example.noahh_000.starthack.activities.UserLanguagePickerActivity;
import com.example.noahh_000.starthack.activities.UserVideoCallActivity;

/**
 * Created by NoahH_000 on 03.05.2016.
 */
public class ActivityNavigationModel {
    public static class InitializationAsTranslator
    {
        /* Makes the transition to the TranslatorActivity
        * Does not pass any data*/
        public static void makeTransition(Context context)
        {
            Intent intentApp = new Intent(context, TranslatorIsReadyActivity.class);
            intentApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentApp);
        }
    }

    public static class PickRoleTranslator
    {
        /* Makes the transition to the TranslatorActivity
        * Does not pass any data*/
        public static void makeTransition(Context context)
        {
            Intent intentApp = new Intent(context, TranslatorLanguagePickerActivity.class);
            context.startActivity(intentApp);
        }
    }

    public static class PickRoleUser
    {
        /* Makes the transition to the TranslatorActivity
        * Does not pass any data*/
        public static void makeTransition(Context context)
        {
            Intent intentApp = new Intent(context, UserHomeScreenActivity.class);
            context.startActivity(intentApp);
        }
    }


    public static class InitializationAsUser
    {
        /* Makes the transition to the TranslatorActivity
        * Does not pass any data*/
        public static void makeTransition(Context context)
        {
            Intent intentApp = new Intent(context, UserHomeScreenActivity.class);
            intentApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentApp);
        }
    }

    public static class InitializationAsUndecided
    {
        /* Makes the transition to the TranslatorActivity
        * Does not pass any data*/
        public static void makeTransition(Context context)
        {
            Intent intentApp = new Intent(context, UndecidedPickRoleActivity.class);
            context.startActivity(intentApp);
        }
    }

    public static class UserFoundTranslatorStartVideoCall
    {
        /* Makes the transition to the UserVideoCall Activity
         *
         * This is called, when the user receives feedback that a translator was found
         * It passes the conversationId of the Conversation that the user started and is now in
        * */
        public static void makeTransition(Context context, String conversationId)
        {
            Intent intentApp = new Intent(context, UserVideoCallActivity.class);

            intentApp.putExtra("conversationId", conversationId);

            context.startActivity(intentApp);
        }

        public static String[] getTransition(Intent intent)
        {
            String[] extra = new String[1];
            extra[0] = intent.getStringExtra("conversationId");
            return extra;
        }
    }

    public static class TranslatorAcceptVideoCall
    {

        /* Makes the transition to the TranslatorVideoCall Activity
         *
         * This is called, when the translator accepts an invitation for a video call
         * it contains the conversationId he joined and the users twilioId
        * */
        public static void makeTransition(Context context, String twilioId, String conversationId)
        {
            Intent intentApp = new Intent(context, TranslatorVideoCallActivity.class);

            intentApp.putExtra("conversationId", conversationId);
            intentApp.putExtra("twilioId", twilioId);

            context.startActivity(intentApp);
        }

        public static String[] getTransition(Intent intent)
        {
            String[] extra = new String[2];
            extra[0] = intent.getStringExtra("twilioId");
            extra[1] = intent.getStringExtra("conversationId");
            return extra;
        }
    }

    public static class UserLanguagePickerStart
    {

        /* Makes the transition to the UserLanguagePicker
         *
         * This is called, when a user wants to change his languages
         * choosesFirstLanguage is true, when the firstLanguage is chosen
         * else the second one is chosen (the language will be saved in there)
        * */
        public static void makeTransition(Context context, boolean choosesFirstLanguage)
        {
            Intent intentApp = new Intent(context, UserLanguagePickerActivity.class);

            intentApp.putExtra("choosesFirstLanguage", choosesFirstLanguage);

            context.startActivity(intentApp);
        }

        public static boolean getTransition(Intent intent)
        {
            return intent.getBooleanExtra("choosesFirstLanguage", true);
        }
    }

    public static class TranslatorLanguagePickerStart
    {

        /* Makes the transition to the TranslatorLanguagePicker
        * */
        public static void makeTransition(Context context, boolean choosesFirstLanguage)
        {
            Intent intentApp = new Intent(context, TranslatorLanguagePickerStart.class);

            context.startActivity(intentApp);
        }
    }
}
