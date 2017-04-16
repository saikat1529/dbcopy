package com.bit_makers.databasemod;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    String path;
    Utility utility = new Utility(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        path = "/data/data/" + getPackageName() + "/" + "databases/";
        ContextWrapper cw =new ContextWrapper(getApplicationContext());
        //path =cw.getFilesDir().getAbsolutePath()+ "/databases/"; //edited to databases
        //createDB();
        //copyDataBase();
//        if(createDB()) {
//            copyDB();
//        }
    }

    private void updateDB(){
    }

    private void copyDataBase()
    {
        Log.i("Database",
                "New database is being copied to device!");
        byte[] buffer = new byte[1024];
        OutputStream myOutput = null;
        int length;
        // Open your local db as the input stream
        InputStream myInput = null;
        try
        {
            myInput = getAssets().open("bangla_recipe_db");
            // transfer bytes from the inputfile to the
            // outputfile
            myOutput =new FileOutputStream(path+ "recipe_db");
            while((length = myInput.read(buffer)) > 0)
            {
                myOutput.write(buffer, 0, length);
            }
            myOutput.close();
            myOutput.flush();
            myInput.close();
            Log.i("Database",
                    "New database has been copied to device!");


        }
        catch(IOException e)
        {
            Log.i("Database",e.toString());
        }
    }

    private boolean createDB(){
        SQLiteDatabase sQLiteDatabase = null;
        try {
            sQLiteDatabase = getBaseContext().openOrCreateDatabase(path + "recipe_db", Context.MODE_PRIVATE, null);
        } catch (SQLiteException e) {
            Log.i("Database",e.toString());
        }
        if (sQLiteDatabase != null) {
            sQLiteDatabase.close();
        }
        return sQLiteDatabase != null;
    }

    /**
     * This method will copy database from /assets directory to application
     * package /databases directory
     **/
    /*private void copyDataBase() throws IOException {
        try {

            InputStream mInputStream = getAssets().open("bangla_recipe_db");
            String outFileName = path + "recipe_db";
            OutputStream mOutputStream = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = mInputStream.read(buffer)) > 0) {
                mOutputStream.write(buffer, 0, length);
            }
            mOutputStream.flush();
            mOutputStream.close();
            mInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private void copyDB(){
        try {
            InputStream open = getAssets().open("bangla_recipe_db");
            OutputStream fileOutputStream = new FileOutputStream(path + "recipe_db");
            byte[] bArr = new byte[1024];
            while (true) {
                int read = open.read(bArr);
                if (read <= 0) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    open.close();
                    return;
                }
                fileOutputStream.write(bArr, 0, read);
            }
        }
        catch (Exception ex){
            utility.logger("DB Not Copied");
        }
        finally {
            utility.logger("DB Copied");
        }
    }

    /** This method checks whether database is exists or not **/
    private boolean checkDataBase() {
        try {
            final String mPath = path + "bangla_recipe_db";
            final File file = new File(mPath);
            if (file.exists())
                return true;
            else
                return false;
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This method will create database in application package /databases
     * directory when first time application launched
     **/
    /*public void createDataBase() throws IOException {
        boolean mDataBaseExist = checkDataBase();
        if (!mDataBaseExist) {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException mIOException) {
                mIOException.printStackTrace();
                throw new Error("Error copying database");
            } finally {
                this.close();
            }
        }
    }*/

}
