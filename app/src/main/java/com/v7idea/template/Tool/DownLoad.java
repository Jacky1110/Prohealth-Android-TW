package com.v7idea.template.Tool;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


import com.v7idea.healthkit.Constant;
import com.v7idea.healthkit.Model.Token;
import com.v7idea.template.AirApplication;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HttpsURLConnection;

public class DownLoad {
    private static final String TAG = "DownLoad";

    public static final int ConnectionTimeOut = 30000;
    public static final int ReadTimeOut = 30000;

    private final static String contentType = "Content-Type";
    public final static String defaultContentType = "application/x-www-form-urlencoded";//application/json;charset=utf-8
    public final static String formDataContentType = "multipart/form-data";

    public final static String IOERROR = "IOERROR";
    public final static String WEBERROR = "WEBERROR";
    public final static String URLERROR = "URLERROR";//網址錯誤
    public final static String RESPONSEERROR = "NoHttpResponseException";//拿不到Response Code

    public final static int ERROR_NET = 9999;
    public final static int ERROR_WEB = 9998;
    public final static int ERROR_URL  = 9997;

    public final static String HeaderKeyName = "v7idea_Token";
    public static final String TIME_OUT_ERROR = "TimeOutException";

    /**
     * 量測是否有上網
     * 有則回傳true
     *
     * @return
     */
    public static boolean isConnectInternet() {
        boolean isConnect = false;

        Context context = AirApplication.getAppContext();

        ConnectivityManager getNetwordType = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nowNetworkType = getNetwordType.getActiveNetworkInfo();

        if (nowNetworkType != null) {
            isConnect = nowNetworkType.isConnectedOrConnecting();
        } else {
            // Log.d("ActivityEntryPage", "not connect to internet !!");
        }

        nowNetworkType = null;
        getNetwordType = null;

        return isConnect;
    }

    public BasicV7HttpResult postUrlByFormData(String URL,List<V7NameValuePair> params){
        return postUrlByFormData(null,null,URL,params );
    }

    public BasicV7HttpResult postUrlByFormData(String strHeaderKey,String strHeaderValue,String URL ,List<V7NameValuePair> params){
        final BasicV7HttpResult result = new BasicV7HttpResult();
        String returnString = null;

        DefaultHttpClient httpClient = new DefaultHttpClient();

        httpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));
        httpClient.setRedirectHandler(new DefaultRedirectHandler(){
            @Override
            public boolean isRedirectRequested(HttpResponse response, HttpContext context)
            {
                int statusCode = response.getStatusLine().getStatusCode();
                result.setResponseCode(statusCode);
                return super.isRedirectRequested(response, context);
            }
        });

        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 3000);
        HttpConnectionParams.setSoTimeout(httpClient.getParams(), 3000);

        HttpPost post = new HttpPost(URL);
        if(strHeaderKey != null&& strHeaderValue != null){
            post.setHeader(strHeaderKey,strHeaderValue);
        }
        MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        for (V7NameValuePair pair : params) {
            try {
                if(pair.getName().equals("userPhoto")&&!"".equals(pair.getValue())&&new File(pair.getValue()).exists()){
                    //判斷key是userPhoto且value不等於空值的話
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    //設置為true讓圖片不佳載到內存中
                    //options.inJustDecodeBounds = true;
                    //縮放的比例 其值表明縮放的倍數,值越大越不了我的犧性
                    options.inSampleSize = 2;
                    //透過路徑來取得圖片
                    Log.d("profile getValue",pair.getValue());
//                    Bitmap bitmap = BitmapFactory.decodeFile(pair.getValue(),options);
                    //用rotateImage 去做圖片旋轉角度 把圖片轉正
                    Bitmap bitmap = rotateImage(pair.getValue(),options);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int compress = 100;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    while (baos.toByteArray().length / 1024 > 100) {
                        baos.reset();
                        compress -= 10;
                        bitmap.compress(Bitmap.CompressFormat.JPEG, compress, baos);
                        if(compress == 0 ){
                            break;
                        }
                    }
                    //透過options.outMimeType來獲取圖片的格式  回傳為 "image/png","image/gif","image/jpeg"不是圖檔回傳null
                    if(options.outMimeType != null){
                                                //ByteArrayBody(byte[] data, String mimeType, String filename)
//                        ContentBody cbFile = new ByteArrayBody(baos.toByteArray(),options.outMimeType,"userProfile");
                        ContentBody cbFile = new FileBody(new File(pair.getValue()));
                        multipartEntity.addPart(pair.getName(),cbFile);
                    }else {
                        multipartEntity.addPart(pair.getName(), new StringBody(pair.getValue(), Charset.forName("UTF-8")));
                    }
                }else {
                    multipartEntity.addPart(pair.getName(), new StringBody(pair.getValue(), Charset.forName("UTF-8")));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        post.setEntity(multipartEntity);

        try
        {
            HttpResponse response = httpClient.execute(post);


            InputStreamReader inputStreamReader = new InputStreamReader(response.getEntity().getContent(), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String sResponse;

            StringBuilder s = new StringBuilder();

            while ((sResponse = bufferedReader.readLine()) != null)
            {
                s = s.append(sResponse);
            }

            returnString = s.toString();

            Log.d("Download", "returnString: " + returnString);

            int responseCode = response.getStatusLine().getStatusCode();
            result.setResponseCode(responseCode);
            result.setResultString(returnString);

        } catch (SocketTimeoutException e){
            result.setResultString(TIME_OUT_ERROR);
            result.setExceptionString(String.valueOf(e.getStackTrace()));
        } catch (NoHttpResponseException e) {
            e.printStackTrace();
            result.setResponseCode(ERROR_WEB);
            result.setResultString(RESPONSEERROR);
            result.setExceptionString(String.valueOf(e.getStackTrace()));
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            result.setResponseCode(ERROR_URL);
            result.setResultString(URLERROR);
            result.setExceptionString(String.valueOf(e.getStackTrace()));
        } catch (IOException e) {
            e.printStackTrace();
            result.setResponseCode(ERROR_URL);
            result.setResultString(IOERROR);
            result.setExceptionString(String.valueOf(e.getStackTrace()));
        }
        return result;
    }

    public Bitmap rotateImage(String imagePath,BitmapFactory.Options options){
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath,options);
        int orientation = 0;

        // 從指定路徑下讀取圖片，並獲取其EXIF資訊
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(imagePath);
            // 獲取圖片的旋轉資訊
            orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL );

            // 根據旋轉角度，生成旋轉矩陣
            Matrix mtx = new Matrix();
            switch (orientation){
                // 將原始圖片按照旋轉矩陣進行旋轉，並得到新的圖片
                case ExifInterface.ORIENTATION_ROTATE_90:
                    mtx.postRotate(90);
                    int w = bitmap.getWidth();
                    int h = bitmap.getHeight();
                    bitmap = Bitmap.createBitmap(bitmap,0,0,w,h,mtx,true);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    mtx.postRotate(180);
                    int w2 = bitmap.getWidth();
                    int h2 = bitmap.getHeight();
                    bitmap = Bitmap.createBitmap(bitmap,0,0,w2,h2,mtx,true);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    mtx.postRotate(270);
                    int w3 = bitmap.getWidth();
                    int h3 = bitmap.getHeight();
                    bitmap = Bitmap.createBitmap(bitmap,0,0,w3,h3,mtx,true);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return bitmap;
        }
        return bitmap;
    }

    public BasicV7HttpResult getStringFromURLAddHeader(String strUrl, String strHeaderValue)
    {
        return getStringFromURLAddHeader(strUrl, HeaderKeyName, strHeaderValue);
    }

    public BasicV7HttpResult getStringFromURLAddHeader(String strUrl, String strHeaderKeyName, String strHeaderValue) {
        BasicV7HttpResult result = new BasicV7HttpResult();

        if (isConnectInternet()) {
            DebugLog.d("httpRequest:Start", "進行Request:" + strUrl);

            int ifError = 0;
            String resultString = "";
            URL submitURL = null;

            ifError = 0;

            if (strUrl == "" || strUrl == null)
                ifError = 1; // 檢查目前的傳入值是否非空字串。

            if (ifError == 0) {

                try {
                    // String encodedurl = URLEncoder.encode(thisURLString,"UTF-8");
                    submitURL = new URL(strUrl);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    // Log.d("URL_IOERROR", e.toString());
                    // resultString = "URL 錯誤!!" + e.toString();
                    resultString = IOERROR;

                    result.setResponseCode(ERROR_URL);
                    result.setResultString(IOERROR);
                    result.setExceptionString(String.valueOf(e.getStackTrace()));
                    // alertbox("網路狀態不正常","請檢查您的裝置是否已經連上網路!!");
                }

                if (submitURL != null) {
                    HttpURLConnection urlConn = null;

                    try {
                        // 使用HttpURLConnection 進行連結;
                        urlConn = (HttpURLConnection) submitURL.openConnection();
                        urlConn.setConnectTimeout(ConnectionTimeOut);
                        urlConn.setReadTimeout(ReadTimeOut);
                        urlConn.setRequestMethod("GET");
                        urlConn.setRequestProperty("Accept-Encoding", "" );

                        if (AirApplication.isStringNotNullAndEmpty(strHeaderKeyName) && AirApplication.isStringNotNullAndEmpty(strHeaderValue)) {
                            urlConn.setRequestProperty(strHeaderKeyName, strHeaderValue);
                        }

                        // Log.d("WaterInfoApp_getStringFromURL", "urlConn.getReadTimeout(): " + urlConn.getReadTimeout());

                        urlConn.connect();

                        int httpCode = urlConn.getResponseCode();

                        result.setResponseCode(httpCode);

                        DebugLog.d("GetStringFromUrl", "httpCode: "+httpCode);

                        if(httpCode == HttpsURLConnection.HTTP_OK)
                        {
                            // 得到讀取的內容;
                            InputStreamReader httpStream = new InputStreamReader(urlConn.getInputStream());

                            // 為輸出建立Buffer;
                            BufferedReader httpBuffer = new BufferedReader(httpStream);
                            StringBuilder htmlContent = new StringBuilder();
                            String inputLine = null;

                            while ((inputLine = httpBuffer.readLine()) != null) {

                                // 結果只有一行，所以讀取一行即可。
                                // 未來如果要變成一個XML，再做另外的處理。
                                // resultString += inputLine;
                                htmlContent.append(inputLine);

                            }

                            httpStream.close();
                            urlConn.disconnect();
                            resultString = htmlContent.toString();
                            result.setResultString(resultString);
                        }
                        else
                        {
                            if(urlConn.getErrorStream() != null){
                                InputStreamReader ErrorReader = new InputStreamReader(urlConn.getErrorStream());

                                // 為輸出建立Buffer;
                                BufferedReader httpBuffer = new BufferedReader(ErrorReader);
                                StringBuilder htmlContent = new StringBuilder();
                                String inputLine = null;

                                while ((inputLine = httpBuffer.readLine()) != null) {

                                    // 結果只有一行，所以讀取一行即可。
                                    // 未來如果要變成一個XML，再做另外的處理。
                                    // resultString += inputLine;
                                    htmlContent.append(inputLine);

                                }

                                ErrorReader.close();
                            }

                            urlConn.disconnect();

                            if(httpCode >= 300 && httpCode < 400){
                                result.setResultString("httpCode:"+httpCode+"\n請確認您的Wifi連線狀態");
                            }else{
                                result.setResultString(resultString);
                            }
                        }
                    }
                    catch (SocketTimeoutException e)
                    {
                        e.printStackTrace();

                        result.setResultString(TIME_OUT_ERROR);
                        result.setExceptionString(String.valueOf(e.getStackTrace()));
                        return result;
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        // Log.d("WEBERROR", e.toString());

                        if (urlConn != null) {
                            urlConn.disconnect();
                        }

                        // Log.d("WEBERROR", "resultString: "+resultString);

                        // alertbox("網路狀態不正常","目前無法與主機取得正確的連線，請稍後再試!");
                        // resultString = "OpenConnection 錯誤!!" + e.toString();

                        result.setResultString(WEBERROR);
                        result.setExceptionString(String.valueOf(e.getStackTrace()));
                    } finally {
                        if (urlConn != null) {
                            urlConn.disconnect();
                        }
                    }
                }
            }

            // // Log.d("APP:HttpWebRequest", resultString);
            return result;
        } else {
            result.setResponseCode(ERROR_NET);
            result.setResultString(IOERROR);

            return result;
        }
    }

    public BasicV7HttpResult getStringFromUrlByDelete(String address, String strHeaderValue)
    {
        return getStringFromUrlByDelete(address, defaultContentType, HeaderKeyName, strHeaderValue);
    }

    public BasicV7HttpResult getStringFromUrlByDelete(String strUrl, String contentType, String strHeaderKeyName, String strHeaderValue) {
        BasicV7HttpResult result = new BasicV7HttpResult();

        if (isConnectInternet()) {
            DebugLog.d("httpRequest:Start", "進行Request:" + strUrl);

            int ifError = 0;
            String resultString = "";
            URL submitURL = null;

            ifError = 0;

            if (strUrl == "" || strUrl == null)
                ifError = 1; // 檢查目前的傳入值是否非空字串。

            if (ifError == 0){
                try {
                    // String encodedurl = URLEncoder.encode(thisURLString,"UTF-8");
                    submitURL = new URL(strUrl);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    // Log.d("URL_IOERROR", e.toString());
                    // resultString = "URL 錯誤!!" + e.toString();
                    resultString = IOERROR;

                    result.setResponseCode(ERROR_URL);
                    result.setResultString(IOERROR);
                    // alertbox("網路狀態不正常","請檢查您的裝置是否已經連上網路!!");
                    result.setExceptionString(String.valueOf(e.getStackTrace()));
                }

                if (submitURL != null) {
                    HttpURLConnection urlConn = null;

                    try {

                        // 使用HttpURLConnection 進行連結;
                        urlConn = getHttpURLConnection(submitURL, "DELETE");

                        if (urlConn != null) {

                            urlConn.setRequestProperty(this.contentType, contentType);
                            urlConn.setDoInput(true);
//                    urlConn.setDoOutput(true);

                            if (AirApplication.isStringNotNullAndEmpty(strHeaderKeyName) && AirApplication.isStringNotNullAndEmpty(strHeaderValue)) {
                                urlConn.setRequestProperty(strHeaderKeyName, strHeaderValue);
                            }

                            int httpCode = urlConn.getResponseCode();

                            result.setResponseCode(httpCode);

                            DebugLog.d("GetStringFromUrl", "httpCode: "+httpCode);

                            if(httpCode == HttpsURLConnection.HTTP_OK)
                            {
                                // 得到讀取的內容;
                                InputStreamReader httpStream = new InputStreamReader(urlConn.getInputStream());

                                // 為輸出建立Buffer;
                                BufferedReader httpBuffer = new BufferedReader(httpStream);
                                StringBuilder htmlContent = new StringBuilder();
                                String inputLine = null;

                                while ((inputLine = httpBuffer.readLine()) != null) {

                                    // 結果只有一行，所以讀取一行即可。
                                    // 未來如果要變成一個XML，再做另外的處理。
                                    // resultString += inputLine;
                                    htmlContent.append(inputLine);

                                }

                                httpStream.close();
                                urlConn.disconnect();
                                resultString = htmlContent.toString();
                                result.setResultString(resultString);
                            }
                            else
                            {
                                if (urlConn.getErrorStream()!=null)
                                {
                                    InputStreamReader ErrorReader = new InputStreamReader(urlConn.getErrorStream());

                                    // 為輸出建立Buffer;
                                    BufferedReader httpBuffer = new BufferedReader(ErrorReader);
                                    StringBuilder htmlContent = new StringBuilder();
                                    String inputLine = null;

                                    while ((inputLine = httpBuffer.readLine()) != null) {

                                        // 結果只有一行，所以讀取一行即可。
                                        // 未來如果要變成一個XML，再做另外的處理。
                                        // resultString += inputLine;
                                        htmlContent.append(inputLine);

                                    }

                                    ErrorReader.close();
                                }

                                urlConn.disconnect();

                                if(httpCode >= 300 && httpCode < 400){
                                    result.setResultString("httpCode:"+httpCode+"\n請確認您的Wifi連線狀態");
                                }else{
                                    result.setResultString(resultString);
                                }                            }
                        }
                    }
                    catch (SocketTimeoutException e)
                    {
                        e.printStackTrace();

                        result.setResultString(TIME_OUT_ERROR);
                        result.setExceptionString(String.valueOf(e.getStackTrace()));
                        return result;
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                        // Log.d("WEBERROR", e.toString());

                        if (urlConn != null) {
                            urlConn.disconnect();
                        }

                        // Log.d("WEBERROR", "resultString: "+resultString);

                        // alertbox("網路狀態不正常","目前無法與主機取得正確的連線，請稍後再試!");
                        // resultString = "OpenConnection 錯誤!!" + e.toString();

                        result.setResultString(WEBERROR);
                        result.setExceptionString(String.valueOf(e.getStackTrace()));
                    }
                    finally {
                        if (urlConn != null) {
                            urlConn.disconnect();
                        }
                    }
                }
            }

            return result;
        }
        else {
            result.setResponseCode(ERROR_NET);
            result.setResultString(IOERROR);

            return result;
        }
    }

    public V7HttpResult getStringFromURLByPostAddHeader(String strUrl, List<V7NameValuePair> content){
        return getStringFromURLByPostAddHeader(strUrl, null, null, defaultContentType, content);
    }

    public V7HttpResult getStringFromURLByPostAddHeader(String strUrl, String strHeaderKeyName, String strHeaderValue, String strContentType, List<V7NameValuePair> content) {
        BasicV7HttpResult result = new BasicV7HttpResult();

        if (isConnectInternet()) {
             DebugLog.d("httpRequest:Start", "進行Request:" + strUrl);
            // Log.d("PressAdvertisingGetPointTask", "data: "+data.toString());

            int ifError = 0;
            String resultString = "";
            URL submitURL = null;

            ifError = 0;

            if (strUrl == "" || strUrl == null)
                ifError = 1; // 檢查目前的傳入值是否非空字串。

            if (ifError == 0) {

                try {
                    // String encodedurl = URLEncoder.encode(thisURLString,"UTF-8");
                    submitURL = new URL(strUrl);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    // Log.d("URL_IOERROR", e.toString());
                    // resultString = "URL 錯誤!!" + e.toString();

                    result.setResponseCode(ERROR_URL);
                    result.setResultString(IOERROR);

                    // alertbox("網路狀態不正常","請檢查您的裝置是否已經連上網路!!");
                    result.setExceptionString(String.valueOf(e.getStackTrace()));
                }

                if (submitURL != null) {
                    HttpURLConnection urlConn = null;

                    try {
                        // 使用HttpURLConnection 進行連結;
                        urlConn = (HttpURLConnection) submitURL.openConnection();
                        urlConn.setConnectTimeout(ConnectionTimeOut);
                        urlConn.setReadTimeout(ReadTimeOut);
                        urlConn.setRequestMethod("POST");

                        if (AirApplication.isStringNotNullAndEmpty(strHeaderKeyName) && AirApplication.isStringNotNullAndEmpty(strHeaderValue)) {
                            urlConn.setRequestProperty(strHeaderKeyName, strHeaderValue);
                        }
                        urlConn.setRequestProperty(this.contentType, strContentType);
                        urlConn.setDoInput(true);
                        urlConn.setDoOutput(true);

                        if(content != null && !content.isEmpty())
                        {
                            DebugLog.d("content:Start", "content:" + content.toString());
                            //獲取urlConn的輸出流
                            OutputStream os = urlConn.getOutputStream();
                            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(os, "UTF-8"));
                            //將request寫入到urlConn的輸出流中
                            writer.write(getQuery(content));
                            //調用輸出流的flush方法
                            writer.flush();
                            //關閉輸出流
                            writer.close();
                        }

                        urlConn.connect();

                        int httpCode = urlConn.getResponseCode();

                        result.setResponseCode(httpCode);

                        if(httpCode == HttpsURLConnection.HTTP_OK)
                        {

                            // 得到讀取的內容;
                            InputStreamReader httpStream = new InputStreamReader(urlConn.getInputStream());

                            // 為輸出建立Buffer;
                            BufferedReader httpBuffer = new BufferedReader(httpStream);
                            StringBuilder htmlContent = new StringBuilder();
                            String inputLine = null;

                            while ((inputLine = httpBuffer.readLine()) != null) {

                                // 結果只有一行，所以讀取一行即可。
                                // 未來如果要變成一個XML，再做另外的處理。
                                // resultString += inputLine;
                                htmlContent.append(inputLine);

                            }

                            httpStream.close();
                            urlConn.disconnect();
                            resultString = htmlContent.toString();

                            result.setResultString(resultString);
                        }
                        else
                        {
                            if (urlConn.getErrorStream()!=null)
                            {
                                InputStreamReader ErrorReader = new InputStreamReader(urlConn.getErrorStream());

                                // 為輸出建立Buffer;
                                BufferedReader httpBuffer = new BufferedReader(ErrorReader);
                                StringBuilder htmlContent = new StringBuilder();
                                String inputLine = null;

                                while ((inputLine = httpBuffer.readLine()) != null) {

                                    // 結果只有一行，所以讀取一行即可。
                                    // 未來如果要變成一個XML，再做另外的處理。
                                    // resultString += inputLine;
                                    htmlContent.append(inputLine);

                                }

                                ErrorReader.close();
                            }
                            urlConn.disconnect();

                            if(httpCode >= 300 && httpCode < 400){
                                result.setResultString("httpCode:"+httpCode+"\n請確認您的Wifi連線狀態");
                            }else{
                                result.setResultString(resultString);
                            }
                        }
                    }
                    catch (SocketTimeoutException e)
                    {
                        e.printStackTrace();

                        result.setResultString(TIME_OUT_ERROR);
                        result.setExceptionString(String.valueOf(e.getStackTrace()));
                        return result;
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        // Log.d("WEBERROR", e.toString());

                        if (urlConn != null) {
                            urlConn.disconnect();
                        }

                        // Log.d("WEBERROR", "resultString: "+resultString);

                        // alertbox("網路狀態不正常","目前無法與主機取得正確的連線，請稍後再試!");
                        // resultString = "OpenConnection 錯誤!!" + e.toString();

                        result.setResultString(WEBERROR);
                        result.setExceptionString(String.valueOf(e.getStackTrace()));
                    } finally {
                        if (urlConn != null) {
                            urlConn.disconnect();
                        }
                    }
                }
            }

            // // Log.d("APP:HttpWebRequest", resultString);
            return result;
        } else {
            result.setResponseCode(ERROR_NET);
            result.setResultString(IOERROR);

            return result;
        }
    }

    /**
     * http://stackoverflow.com/questions/9767952/how-to-add-parameters-to-httpurlconnection-using-post
     *
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getQuery(List<V7NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (V7NameValuePair pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public URL getUrl(String urlString) {
        URL submitURL = null;

        try {
            submitURL = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            submitURL = null;
        }

        return submitURL;
    }

    public HttpURLConnection getHttpURLConnection(URL url, String httpMethod) throws IOException {
        if (url != null) {
            // 使用HttpURLConnection 進行連結;
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setConnectTimeout(30000);
            urlConn.setReadTimeout(30000);

            if (httpMethod != null && (httpMethod.contentEquals("GET") || httpMethod.contentEquals("POST")
                    || httpMethod.contentEquals("PUT") || httpMethod.contentEquals("DELETE"))) {
                urlConn.setRequestMethod(httpMethod);
            } else {
                return null;
            }

            return urlConn;
        }

        return null;
    }
}
