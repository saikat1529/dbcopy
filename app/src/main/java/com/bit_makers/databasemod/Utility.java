package com.bit_makers.databasemod;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by fojlesaikat on 3/30/17.
 */

public class Utility {
    Context context;
    ArrayList<String> errorlist;
    ProgressDialog mProgressDialog;
    //BaseApiInterface baseApiInterface = BaseApiClient.getBaseClient().create(BaseApiInterface.class);

    public Utility(Context ctx){
        context = ctx;
        errorlist = new ArrayList<String>();
    }

    /*
    ================ Set Image Size for ImageView ===============
    */
    public void setImageSize(View view, String layout, int w, int h, String position){
        ImageView imageView =  (ImageView)view;
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        int actualWidth, actualHeight;
        if(w==0){
            actualWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        else {
            actualWidth = (width/10)*w;
        }
        if(h==0){
            actualHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        else {
            actualHeight = (height/10)*h;
        }
        if(layout.equals("Linear")) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(actualWidth, actualHeight);
            imageView.setLayoutParams(params);
        }
        else if(layout.equals("Relative")){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(actualWidth, actualHeight);
            switch (position){
                case "Center":
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    break;
                case "Top":
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    break;
                case "Bottom":
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    break;
            }
            imageView.setLayoutParams(params);
        }

    }

    /*
    =============== Get Version ===============
    */
    public String getVersion(){
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo!=null){
            return "Version "+packageInfo.versionName;
        }
        else{
            return "version not found";
        }
    }

    /*
    =============== Set Font ===============
    */
    public void setFont(View view, String fontName) {
        Typeface tf = null;
        if(fontName.equals(Font.REGULAR)){
            tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
        }
        else if(fontName.equals(Font.LIGHT)){
            tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
        }
        else{
            tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        }
        if(view instanceof TextView){
            TextView tv = (TextView)view;
            tv.setTypeface(tf);
        }
        else if(view instanceof EditText){
            EditText et = (EditText)view;
            et.setTypeface(tf);
        }
        else if(view instanceof Button){
            Button btn = (Button)view;
            btn.setTypeface(tf);
        }

    }

    /*
    =============== SharedPref for User ===============
    */
    public void setUser(JSONObject jsonObject){
        SharedPreferences sp = context.getSharedPreferences("User",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if(jsonObject!=null) {
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = "";
                try {
                    value = jsonObject.getString(key);
                } catch (JSONException ex) {
                    call_error(ex);
                }
                editor.putString(key, value);
            }
        }
        else{
            editor.putString("value", "none");
        }
        editor.commit();
    }


    /*
    =============== SharedPref for User ===============
    */
    public JSONObject getDeviceInformation(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("os_version", System.getProperty("os.version"));
            jsonObject.put("api_level", Build.VERSION.SDK_INT);
            jsonObject.put("device_model", Build.DEVICE);
            jsonObject.put("device_manufacturer", Build.MANUFACTURER);
        }
        catch (Exception ex){
            call_error(ex);
        }
        return jsonObject;
    }

    /*
    =============== Check Version ===============
    */
    public boolean checkVersion(String versionName, int versionCode){
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
            int currentCode = packageInfo.versionCode;
            if(currentCode<versionCode){
                return true;
            }
            else{
                return false;
            }
        }
        catch (Exception ex){
            return false;
        }
    }

    /*
    =============== SharedPref for User ===============
    */
    public void setVendor(JSONObject jsonObject){
        SharedPreferences sp = context.getSharedPreferences("Vendor",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if(jsonObject!=null) {
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = "";
                try {
                    value = jsonObject.getString(key);
                } catch (JSONException ex) {
                    call_error(ex);
                }
                editor.putString(key, value);
            }
        }
        else{
            editor.putString("value", "none");
        }
        editor.commit();
    }

    /*
    =============== SharedPref for User ===============
    */
    public JSONObject getUser(){
        File f = new File(
                "/data/data/com.bit_makers.biyebariandroid/shared_prefs/User.xml");
        if(!f.exists()){
            setUser(null);
        }
        SharedPreferences sp = context.getSharedPreferences("User",Context.MODE_PRIVATE);
        Map<String,?> map = sp.getAll();
        JSONObject jsonObject = new JSONObject();
        for(Map.Entry<String,?> entry: map.entrySet()){
            try {
                if(entry.getValue().toString().equals("null")){
                    jsonObject.put(entry.getKey(),JSONObject.NULL);
                }
                else{
                    jsonObject.put(entry.getKey(),entry.getValue().toString());
                }

            } catch (JSONException ex) {
                call_error(ex);
            }
        }
        return jsonObject;
    }

    /*
    =============== SharedPref for User ===============
    */
    public JSONObject getVendor(){
        File f = new File(
                "/data/data/com.bit_makers.biyebariandroid/shared_prefs/Vendor.xml");
        if(!f.exists()){
            setUser(null);
        }
        SharedPreferences sp = context.getSharedPreferences("Vendor",Context.MODE_PRIVATE);
        Map<String,?> map = sp.getAll();
        JSONObject jsonObject = new JSONObject();
        for(Map.Entry<String,?> entry: map.entrySet()){
            try {
                if(entry.getValue().toString().equals("null")){
                    jsonObject.put(entry.getKey(),JSONObject.NULL);
                }
                else{
                    if(entry.getKey().equals("areas")||entry.getKey().equals("categories")){
                        jsonObject.put(entry.getKey(),new JSONArray(entry.getValue().toString()));
                    }
                    else {
                        jsonObject.put(entry.getKey(), entry.getValue().toString());
                    }
                }

            } catch (JSONException ex) {
                call_error(ex);
            }
        }
        return jsonObject;
    }

    /*
    =============== Check Email ===============
    */
    public boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    /*
    =============== Get Empty User ===============
    */
    public JSONObject getEmptyUser(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", "Mr. Guest");
            jsonObject.put("userRole", 0);
            jsonObject.put("fullName", "Welcome");
            jsonObject.put("firstName", "");
            jsonObject.put("lastName", "");
            jsonObject.put("contactNo", "");
            jsonObject.put("nidOrPassportNo", "");
            jsonObject.put("password", "");
            jsonObject.put("rememberToken", JSONObject.NULL);
            jsonObject.put("verified", "no");
            jsonObject.put("companyId", "0");
            jsonObject.put("about", JSONObject.NULL);
            jsonObject.put("profilePic", JSONObject.NULL);
            jsonObject.put("lastLoginIp", JSONObject.NULL);
            jsonObject.put("signupIpAddress", JSONObject.NULL);
            jsonObject.put("position", JSONObject.NULL);
            jsonObject.put("location", "");
            jsonObject.put("website", "");
            jsonObject.put("fbUrl", "");
            jsonObject.put("googleUrl", "");
            jsonObject.put("twitterUrl", "");
            jsonObject.put("linkedinUrl", "");
            jsonObject.put("createdAt", JSONObject.NULL);
            jsonObject.put("updatedAt", JSONObject.NULL);
        }
        catch (JSONException ex){
            call_error(ex);
        }
        return jsonObject;
    }


    /*
    =============== Get Empty Vendor ===============
    */
    public JSONObject getEmptyVendor(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", "");
            jsonObject.put("email", "Mr. Guest");
            jsonObject.put("roleId", 0);
            jsonObject.put("contactNo", "");
            jsonObject.put("rememberToken", JSONObject.NULL);
            jsonObject.put("verified", "no");
            jsonObject.put("companyId", JSONObject.NULL);
            jsonObject.put("about", JSONObject.NULL);
            jsonObject.put("companyLogo", "");
            jsonObject.put("lastLoginIp", JSONObject.NULL);
            jsonObject.put("signupIpAddress", JSONObject.NULL);
            jsonObject.put("position", JSONObject.NULL);
            jsonObject.put("location", "");
            jsonObject.put("website", JSONObject.NULL);
            jsonObject.put("fbUrl", JSONObject.NULL);
            jsonObject.put("googleUrl", JSONObject.NULL);
            jsonObject.put("twitterUrl", JSONObject.NULL);
            jsonObject.put("linkedinUrl", JSONObject.NULL);
            jsonObject.put("areas", JSONObject.NULL);
            jsonObject.put("categories", JSONObject.NULL);
            jsonObject.put("createdAt", JSONObject.NULL);
            jsonObject.put("updatedAt", JSONObject.NULL);
        }
        catch (JSONException ex){
            call_error(ex);
        }
        return jsonObject;
    }

    /*
=============== SharedPref for Token ===============
*/
    public void setToken(String access){
        SharedPreferences sp = context.getSharedPreferences("Token",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("token",access);
        editor.commit();
    }

    /*
    =============== Get SharedPref for AccessToken ===============
    */
    public String getToken(String keyword){
        File f = new File(
                "/data/data/com.nullpointerbd.iucf/shared_prefs/Token.xml");
        if(!f.exists()){
            setToken("none");
        }
        SharedPreferences sp = context.getSharedPreferences("Token",Context.MODE_PRIVATE);
        return sp.getString(keyword,null);
    }

    /*
    =============== Set NavigationView ID ===============
    */
    public void setNavigationViewId(int id){
        SharedPreferences sp = context.getSharedPreferences("general",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("navigationid",id);
        editor.commit();
    }

    /*
    =============== Get NavigationView ID ===============
    */
    public int getNavigationViewId(){
        File f = new File(
                "/data/data/com.bit_makers.biyebariandroid/shared_prefs/general.xml");
        if(!f.exists()){
            setLogInStat(false,"none","none");
        }
        SharedPreferences sp = context.getSharedPreferences("general",Context.MODE_PRIVATE);
        return sp.getInt("navigationid",-1);
    }


    /*
    =============== SharedPref for Current Page ===============
    */
    public void setCurrentPage(String currentPage){
        SharedPreferences sp = context.getSharedPreferences("CurrentPage",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("name",currentPage);
        editor.commit();
    }

    /*
    =============== Call Activity ===============
    */
    public void callActivity(Class activity){
        Intent intent = new Intent(context,activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        ((Activity)context).finish();
    }

    /*
    =============== Get Current Page ===============
    */
    public String getCurrentPage(){
        File f = new File(
                "/data/data/com.bit_makers.biyebariandroid/shared_prefs/CurrentPage.xml");
        if(f.exists()){
            SharedPreferences sp = context.getSharedPreferences("CurrentPage",Context.MODE_PRIVATE);
            return sp.getString("name",null);
        }
        else{
            return null;
        }

    }

    /*
    =============== SharedPref for LogIn State ===============
    */
    public void setLogInStat(boolean flag, String loginThrough, String loginAs){
        SharedPreferences sp = context.getSharedPreferences("LogInStat",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("status",flag);
        editor.putString("through",loginThrough);
        editor.putString("as",loginAs);
        editor.commit();
    }

    /*
    =============== Get SharedPref for LogIn State ===============
    */
    public boolean getLogInStat(){
        File f = new File(
                "/data/data/com.bit_makers.biyebariandroid/shared_prefs/LogInStat.xml");
        if(!f.exists()){
            setLogInStat(false,"none","none");
        }
        SharedPreferences sp = context.getSharedPreferences("LogInStat",Context.MODE_PRIVATE);
        return sp.getBoolean("status",false);
    }

    /*
    =============== Get SharedPref for LogIn State ===============
    */
    public String getLogInThrough(){
        File f = new File(
                "/data/data/com.bit_makers.biyebariandroid/shared_prefs/LogInStat.xml");
        if(!f.exists()){
            setLogInStat(false,"none","none");
        }
        SharedPreferences sp = context.getSharedPreferences("LogInStat",Context.MODE_PRIVATE);
        return sp.getString("through","");
    }

    /*
    =============== Get SharedPref for LogIn State ===============
    */
    public String getLogInAs(){
        File f = new File(
                "/data/data/com.bit_makers.biyebariandroid/shared_prefs/LogInStat.xml");
        if(!f.exists()){
            setLogInStat(false,"none","none");
        }
        SharedPreferences sp = context.getSharedPreferences("LogInStat",Context.MODE_PRIVATE);
        return sp.getString("as","");
    }

    /*
    =============== Set Window FullScreen ===============
    */
    public void setFullScreen(){
        Activity activity = ((Activity)context);
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /*
    =============== Show Current Date ===============
    */
    public int getDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = simpleDateFormat.format(calendar.getTime());
        return Integer.parseInt(date);
    }

    /*
    ================ Show Toast Message ===============
    */
    public void showToast(String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }

    /*
    ================ Hide Keyboard from Screen ===============
    */
    public void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    /*
    ================ Add Error in Error List ===============
    */
    public void addError(String value){
        errorlist.add(value);
    }

    /*
    ================ Check if Error Found in Error List ===============
    */
    public boolean checkError(){
        if(errorlist.size()==0)
            return false;
        else
            return true;
    }

    /*
    ================ Show SnackBar Message ===============
    Here action is used to set OnClick action in actionbar
    If action == TRUE thats mean it has OnClick listener, hence next two arguments need
    to be passed. Example title and class must be passed if action = TRUE to
    avoid unwanted error.
    */
    public void showSnackBar(View view, String message){
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }


    /*
    ================ Show SnackBar Message ===============
    Here action is used to set OnClick action in actionbar
    If action == TRUE thats mean it has OnClick listener, hence next two arguments need
    to be passed. Example title and class must be passed if action = TRUE to
    avoid unwanted error.
    */
    public void showSnackBar(View view, String message, String length, final boolean action, String title, final Class activity){
        Snackbar snackbar;
        if(length.equals(KeyWord.SNACKBAR_LONG)){
            snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        }
        else if(length.equals(KeyWord.SNACKBAR_INDEFINITE)){
            snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        }
        else{
            snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        }
        if(action){
            snackbar.setAction(title, new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    setCurrentPage(Refresh.NULL);
                    Intent intent = new Intent(context,activity);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                    ((Activity)context).finish();
                }
            });
            snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }
        snackbar.show();
    }

    /*
    ================ Return Multiple Error in String ===============
    */
    public String getError(){
        String message = "";
        for(int i=0; i<errorlist.size(); i++){
            if(i!=0)
                message += "\n";
            message += errorlist.get(i);
        }
        return message;
    }

    /*
    ================ Clear Error from Error List ===============
    */
    public void clearError(){
        errorlist.clear();
    }

    /*
    ================ Clear Text for EditText, Button, TextView ===============
    */
    public void clearText(View[] view){
        for (View v: view) {
            if(v instanceof EditText){
                ((EditText) v).setText("");
            }
            else if(v instanceof Button){
                ((Button) v).setText("");
            }
            else if(v instanceof TextView){
                ((TextView) v).setText("");
            }
        }

    }

    /*
    ================ Set Visibility for EditText, Button, TextView ===============
    */
    public void setVisibility(View[] view, boolean flag){
        for (View v: view) {
            if(v instanceof EditText){
                if(flag) {
                    ((EditText) v).setVisibility(View.VISIBLE);
                }
                else{
                    ((EditText) v).setVisibility(View.GONE);
                }
            }
            else if(v instanceof Button){
                if(flag) {
                    ((Button) v).setVisibility(View.VISIBLE);
                }
                else{
                    ((Button) v).setVisibility(View.GONE);
                }
            }
            else if(v instanceof TextView){
                if(flag) {
                    ((TextView) v).setVisibility(View.VISIBLE);
                }
                else{
                    ((TextView) v).setVisibility(View.GONE);
                }
            }
        }

    }

    /*
    ================ GENERATE SHA TOKEN for App ===============
    */
    public String showToken(){
        String token = "";
        try{
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getApplicationContext().getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                token = Base64.encodeToString(md.digest(), Base64.DEFAULT);

            }
        } catch (PackageManager.NameNotFoundException ex) {
            token = ex.toString();
        } catch (NoSuchAlgorithmException ex) {
            token = ex.toString();
        }
        return token;
    }

    /*
    ================ This function set size for alert dialog throughout screen ===============
    */
    public void setSize(ViewGroup.LayoutParams params, int myWidth, int myHeight){
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        if(myWidth!=0) {
            int width = size.x;
            params.width = (width / 10) * myWidth;
        }
        if(myHeight!=0) {
            int height = size.y;
            params.height = (height / 10) * myHeight;
        }
    }

    /*
    ================ This function checks if corresponding service is running or not ===============
    */
    public boolean ServiceState(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /*
    ================ Log function ===============
     */
    public void logger(String message){
        Log.d("debug",message);
    }

    /*
   ================ Check Network Availability ===============
    */
    public boolean isNetworkAvailable(){
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return (info!=null && info.isConnected());
    }

    /*
   ================ Set Sync Called Status ===============
    */
    public void setSyncStatus(boolean value){
        SharedPreferences sp = context.getSharedPreferences("SyncState",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("status",value);
        editor.commit();
    }

    /*
   ================ Get Sync Called Status ===============
    */
    public boolean getSyncStatus(){
        SharedPreferences sp = ((Activity)context).getPreferences(Context.MODE_PRIVATE);
        return sp.getBoolean("status",false);
    }

    /*
   ================ Set Sync Interval ===============
    */
    public void setSyncInterval(String value){
        SharedPreferences sp = context.getSharedPreferences("SyncInterval",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("interval",value);
        editor.commit();
    }

    /*
   ================ Get Sync Called Status ===============
    */
    public String getSyncInterval(){
        SharedPreferences sp = context.getSharedPreferences("SyncInterval",Context.MODE_PRIVATE);
        //SharedPreferences sp = ((Activity) context).getPreferences(Context.MODE_PRIVATE);
        return sp.getString("interval","0");

    }

    /*
    ================ Check Shared Preference Exists ===============
    */
    public boolean isFileExist(String fileName){
        File file = new File("/data/data/"+
                context.getPackageName()+"/shared_prefs/"+
                fileName+".xml");
        if(file.exists())
            return true;
        else
            return false;
    }

    /*
    ================ Sync Start Function ===============
    */
/*    public void StartSync(long interval){
        Intent intent = new Intent(context, SyncService.class);
        intent.putExtra("Interval",interval);
        long intervalValue = interval*60*60*1000;
        PendingIntent pendingIntent = PendingIntent.getService(context,54321,intent,0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+intervalValue,pendingIntent);
        logger("Next Sync after "+((interval/1000)/60)+" minutes");
    }*/

    /*
    ================ Sync Stop Function ===============
    */
/*    public void StopSync(){
        Intent intent = new Intent(context, SyncService.class);
        context.stopService(intent);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 54321, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        logger("Sync Cancelled");
    }*/


    /*
    ================ Convert HashMap to String ===============
    */
    public String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    /*
    ================ Refresh Token ===============
    */
    public void refreshToken(){
        /*HashMap<String, String> map = new HashMap<String, String>();
        map.put("username","aDmin@#$");
        map.put("password","R$x0Z.o50@Os");
        JSONExecuter jsonExecuter = null;
        try {
            jsonExecuter = new JSONExecuter(context,false,"POST",getPostDataString(map),"PAIR_VALUE","none","NORMAL");         //boolean value define progress dialog visibility [true=visible]
        } catch (UnsupportedEncodingException ex) {
            call_error(ex);
        }
        jsonExecuter.delegate = (AsyncResponse)context;
        if(isNetworkAvailable()) {
            jsonExecuter.execute(UrlList.TOKEN_URL);
        }*/
        /*if (isNetworkAvailable()) {
            Call<ResponseBody> tokenCall = baseApiInterface.getToken("aDmin@#$","R$x0Z.o50@Os");
            tokenCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.code() == 200) {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            String access_token = jsonObject.optString("access_token");
                            String refresh_token = jsonObject.optString("refresh_token");
                            setToken(access_token, refresh_token);
                        }
                    }
                    catch (Exception ex){
                        //utility.showSnackBar(logo, ex.toString(), KeyWord.SNACKBAR_INDEFINITE, true, "Try Again", SplashScreen.class, Refresh.SPLASH);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //utility.showSnackBar(logo, t.toString(), KeyWord.SNACKBAR_INDEFINITE, true, "Try Again", SplashScreen.class, Refresh.SPLASH);
                }
            });
        }*/
    }


    /*
    ================ Show Progress Dialog ===============
    */
    public void showProgress(){
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.show();
    }

    /*
    ================ Hide Progress Dialog ===============
    */
    public void hideProgress(){
        if(mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /*
    ================ Receiving response after HTTP call ===============
    */
    public void processFinish(JSONObject[] jsonObject) {
        JSONObject[] jsonObj = jsonObject;
        if(!jsonObj[0].has("HTTP_MESSAGE")) {
            String access_token = jsonObject[0].optString("access_token");
            String refresh_token = jsonObject[0].optString("refresh_token");
            setToken(access_token);
        }
    }

    /*
    ================ Error Called Function ===============
    */

    public void call_error(Exception ex){
        String error = ex.getMessage();
        StackTraceElement[] message = ex.getStackTrace();
        //Intent intent = new Intent(context, ErrorHandle.class);
        Intent intent = new Intent(context, MainActivity.class);
        StringBuilder builder = new StringBuilder();
        int i=1;
        for (StackTraceElement trace : message) {
            builder.append("Exception "+i+"<br>File:"+trace.getFileName()+" | Method: "+trace.getMethodName()+" | Line: "+trace.getLineNumber()+"<br>");
            i++;
        }
        builder.append("Caused By:"+ex.toString());
        intent.putExtra("error",error);
        intent.putExtra("description",builder.toString());
        context.startActivity(intent);
    }

    /*
    ================ Get Runtime Permission ===============
    */
    public void getPermission(){
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1){
            int writePermissionCheck = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int readPermissionCheck = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if(writePermissionCheck== PackageManager.PERMISSION_DENIED&&readPermissionCheck==PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
            }
        }
    }


    /*
================ Get Runtime Permission ===============
*/

    /*
    ============== Base64 Decode =========
     */
    public String decodeBase64(String message){
        String text = "Conversion Error";
        try {
            byte[] bytes = null;
            bytes = Base64.decode(message, Base64.DEFAULT);
            text = new String(bytes, "UTF-8");
        }
        catch (Exception ex){
            call_error(ex);
        }
        return text;
    }
    /*
    ============== Base64 Encode =========
     */
    public String encodeBase64(String message){
        String text = "Conversion Error";
        try {
            byte[] data = message.getBytes("UTF-8");
            text = Base64.encodeToString(data, Base64.DEFAULT);
        }
        catch (Exception ex){
            call_error(ex);
        }
        return text;
    }

    /*
    ================ Log In menu Initiate ===============
    */
/*    public void login_init(NavigationView navigationView) {
        View headerLayout = navigationView.getHeaderView(0);
        TextView name = (TextView) headerLayout.findViewById(R.id.nav_header_title);
        TextView email = (TextView) headerLayout.findViewById(R.id.nav_header_email);
        ImageView vendorImage = (ImageView)headerLayout.findViewById(R.id.profile_image_vendor);
        CircleImageView circleImageView = (CircleImageView) headerLayout.findViewById(R.id.profile_image_user);
        if(getLogInStat()){
            if(getLogInAs().equals("vendor")) {
                vendorImage.setVisibility(View.VISIBLE);
                circleImageView.setVisibility(View.GONE);
                if(getVendor().optString("companyLogo")!=null) {
                    Picasso.with(context).load(getVendor().optString("companyLogo")).placeholder(ContextCompat.getDrawable(context,R.drawable.vendor_icon)).error(ContextCompat.getDrawable(context, R.drawable.vendor_icon)).into(vendorImage);
                }
                else {
                    vendorImage.setImageDrawable(context.getResources().getDrawable(R.drawable.user));
                }
            }
            if(getLogInAs().equals("user")){
                vendorImage.setVisibility(View.GONE);
                circleImageView.setVisibility(View.VISIBLE);
                if(!getUser().optString("profilePic").equals("null")){
                    Picasso.with(context).load(getUser().optString("profilePic")).into(circleImageView);
                }
                else {
                    circleImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.user));
                }
            }
        }
        else {
            vendorImage.setVisibility(View.VISIBLE);
            circleImageView.setVisibility(View.GONE);
            name.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
            vendorImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_without_border));
        }
        setFont(name, Font.MEDIUM);
        setFont(email, Font.LIGHT);
        if(getLogInAs().equals("user")) {
            name.setText(getUser().optString("fullName"));
            email.setText(getUser().optString("email"));
        }
        else if(getLogInAs().equals("vendor")){
            name.setText(getVendor().optString("name"));
            email.setText(getVendor().optString("email"));
        }
        else{
            name.setText("Welcome!");
            email.setText("Mr. Guest");
        }
    }*/


    /*
    ================ Check Permission ===============
    */
    public static boolean checkPermission(final Context context)
    {
        final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    /*
   =============== Re Arrange Error Message ===============
   */
    public String errorMessage(JSONArray jsonArray){
        StringBuilder sb = new StringBuilder();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                if (i > 0) {
                    sb.append("\n");
                }
                sb.append(jsonArray.get(i));
            }

        }
        catch (Exception ex){
            call_error(ex);
        }
        return sb.toString();
    }


    /*
   ================ Check Record ===============
   */
    /*public boolean checkData()
    {
        boolean flag = false;
        DB db = new DB(context);
        try {
            db.open();
            int group = db.getGroupDataCount();
            int location = db.getLocationDataCount();
            int area = db.getAreaDataCount();
            int unit = db.getUnitDataCount();
            if(group>0&&location>0&&area>0&&unit>0)
                flag = true;
            db.close();
        } catch (SQLException ex) {
            call_error(ex);
        }
        return flag;
    }*/


    /*
   ================ Check Record ===============
   */
    /*public ArrayList<String> url_checklist()
    {
        ArrayList<String> url_list = new ArrayList<String>();
        boolean flag = false;
        DB db = new DB(context);
        try {
            db.open();
            int group = db.getGroupDataCount();
            if(group==0){
                url_list.add(UrlList.Get_ALL_CATEGORIES);
            }
            int location = db.getLocationDataCount();
            if(location==0){
                url_list.add(UrlList.Get_ALL_LOCATION);
            }
            int unit = db.getUnitDataCount();
            if(unit==0){
                url_list.add(UrlList.GET_ALL_UNIT);
            }
            int area = db.getAreaDataCount();
            if(area==0){
                url_list.add(UrlList.Get_ALL_AREAS);
            }
            db.close();
        } catch (SQLException ex) {
            call_error(ex);
        }
        return url_list;
    }*/

    /*
    ================ Clear File ===============
    */
    public void clearFile()
    {
        String filePath = Environment.getExternalStorageDirectory().toString() + "/product";
        File myDir = new File(filePath);
        if(!myDir.exists()) {
            myDir.mkdirs();
        }
        File[] files = myDir.listFiles();
        if(files!=null) {
            for (int i = 0; i < files.length; i++) {
                File file = new File(files[i].getAbsolutePath());
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }

    /*
    ================ Check File ===============
    */
    public boolean checkFile()
    {
        String filePath = Environment.getExternalStorageDirectory().toString() + "/product";
        File myDir = new File(filePath);
        if(!myDir.exists()) {
            myDir.mkdirs();
        }
        File[] files = myDir.listFiles();
        if(files!=null&&files.length>0) {
            return true;
        }
        else {
            return false;
        }
    }


    /*
    ================ Add Edit Text Required ===============
    */
    /*public void setRequired(EditText editText, String color)
    {
        if(color.equals("RED")) {
            editText.setBackground(context.getResources().getDrawable(R.drawable.white_rounded_edittext_req));
        }
        else{
            editText.setBackground(context.getResources().getDrawable(R.drawable.white_rounded_edittext));
        }
    }*/


    /*
    ================ Get Screen Width ===============
    */
    public HashMap<String, Integer> getScreenRes()
    {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        map.put("width",width);
        map.put("height",height);
        return map;
    }
}
