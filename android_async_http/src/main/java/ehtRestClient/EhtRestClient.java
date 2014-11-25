package ehtRestClient;

import android.util.Log;

import com.loopj.android_async_http.http.AsyncHttpClient;
import com.loopj.android_async_http.http.AsyncHttpResponseHandler;
import com.loopj.android_async_http.http.JsonHttpResponseHandler;
import com.loopj.android_async_http.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
* Created by Administrator on 2014/9/23.
*/
public class EhtRestClient {
    private static final String TAG = "EhtRestClient";
    private static final String BASE_URL = "http://182.254.137.149:9000/ehtrest/api/";
    private static final String APP_KEY = "EHTAPPKEY3";
    private static final String SECRET = "SECRET3";
    public static String TOKEN = null;
    private static final int SUCCESS = 1;
    private static final int FAIL = 0;
    private static AsyncHttpClient client = new AsyncHttpClient();

    private static String getAbsoluteUrl(String relativeUrl, List<String> params){
        StringBuilder paramStr = new StringBuilder();
        for(String s: params){
            paramStr.append("/").append(s);
        }
        return BASE_URL + relativeUrl + paramStr;
    }

    /**
     * 使用GET方式调用接口
     * @param url
     * @param params
     * @param responseHandler
     */
    public static void get(String url,List<String> params, AsyncHttpResponseHandler responseHandler){
        if(TOKEN==null || TOKEN.equals("")){
            getWithNoToken(url,params,responseHandler);
        }else {
            Long time = System.currentTimeMillis();
            //get方式params参数为空
            String apiSignature = EhtWebUtil.sgin(APP_KEY, Long.toString(time), SECRET, TOKEN, "");
            client.addHeader("signature", apiSignature);
            client.addHeader("timestamp", Long.toString(time));
            client.addHeader("token", TOKEN);
            client.addHeader("appKey", APP_KEY);
            client.get(getAbsoluteUrl(url,params), null, responseHandler);
        }
    }

    public static void getWithNoToken(final String url, final List<String> params, final AsyncHttpResponseHandler responseHandler){
        Log.d(TAG, "getWithNoToken");
        client.get(BASE_URL + "token/getToken", new JsonHttpResponseHandler(){
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    int code = response.getInt("code");
                    if(code == SUCCESS){
                        TOKEN = response.getString("object");
                        //
                        Log.d(TAG, "get url"+url);
                        Long time = System.currentTimeMillis();
                        //get方式params参数为空
                        String apiSignature = EhtWebUtil.sgin(APP_KEY, Long.toString(time), SECRET, TOKEN, "");
                        client.addHeader("signature", apiSignature);
                        client.addHeader("timestamp", Long.toString(time));
                        client.addHeader("token", TOKEN);
                        client.addHeader("appKey", APP_KEY);
                        client.get(getAbsoluteUrl(url, params), null, responseHandler);
                    }else {
                        String msg = response.getString("msg");
                        JSONObject object = response.getJSONObject("object");
                        Log.d(TAG, "msg=" + msg + ",object=" + object);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //TODO
                Log.d(TAG, "get Token fail.");
            }
        });
    }

    public static void getToken(){
        client.get(BASE_URL + "token/getToken", new JsonHttpResponseHandler(){
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    int code = response.getInt("code");
                    if(code == SUCCESS){
                        TOKEN = response.getString("object");
                    }else {
                        String msg = response.getString("msg");
                        JSONObject object = response.getJSONObject("object");
                        Log.d(TAG, "msg=" + msg + ",object=" + object);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //TODO
                Log.d(TAG, "get Token fail.");
            }
        });
    }


}
