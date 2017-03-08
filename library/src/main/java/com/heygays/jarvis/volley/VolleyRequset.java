package com.heygays.jarvis.volley;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Volley轻量封装，适用常规的get，post方式，方便快速
 *
 * @version 1.0.1
 * @time 2016-03-13
 */
public class VolleyRequset {

    public static RequestQueue requestQueue;
    public static final DefaultRetryPolicy RETRY_POLICY = new DefaultRetryPolicy(30 * 1000, 0, 0f);// 超时30s,重试0次

    /**
     * 初始化请求队列a
     *
     * @param context
     */
    public static boolean initQueue(Context context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return true;
    }

    /**
     * get请求
     *
     * @param ob_this  fragment或Activity
     * @param url      请求地址
     * @param param    参数列表
     * @param tag      设置tag
     * @param listener 回调监听
     */
    public static void doGet(Object ob_this, String url, final Map<String, String> param, Object tag,
                             final VolleyListener listener) {
        Context context = null;
        if (ob_this instanceof Fragment) {
            context = ((Fragment) ob_this).getActivity();
        } else if (ob_this instanceof Activity) {
            context = (Context) ob_this;
        } else {
            Log.e("VolleyRequset Error", "ob_this类型错误，请传入Fragment或者Activity实例");
            return;
        }
        listener.setThis(ob_this);
        initQueue(context);
        removeQueue(tag);
        if (!TextUtils.isEmpty(url) && param != null) {
            url = makeUrl(url, param);
        }
        StringRequest strRequest = new StringRequest(Method.GET, url, new Listener<String>() {

            @Override
            public void onResponse(String result) {
                listener.onFinish();
                listener.onSuccess(result);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                doErrorEvent(listener, error);
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                try {
                    parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers, getResponseCharset(param)));
                } catch (UnsupportedEncodingException var4) {
                    parsed = new String(response.data);
                }
                return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (param != null) {
                    return param;
                } else {
                    return new HashMap<String, String>();
                }
            }
        };
        strRequest.setRetryPolicy(RETRY_POLICY);
        strRequest.setTag(tag);
        strRequest.setShouldCache(true);
        requestQueue.add(strRequest);
        requestQueue.start();
        listener.onLoading();
    }

    /**
     * 拼接url参数
     *
     * @param url
     * @param params
     * @return
     */
    private static String makeUrl(String url, Map<String, String> params) {
        Iterator<Entry<String, String>> it = params.entrySet().iterator();
        url = url.replaceFirst("\\?", "\\&");
        StringBuffer bufferUrl = new StringBuffer(url);
        while (it.hasNext()) {
            Entry<String, String> entry = (Entry<String, String>) it.next();
            try {
                String encodeValue = URLEncoder.encode(entry.getValue(), "utf-8");
                bufferUrl.append("&").append(entry.getKey()).append("=").append(encodeValue);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return bufferUrl.toString().replaceFirst("\\&", "\\?");
    }

    /**
     * post请求
     *
     * @param ob_this  fragment和Activity
     * @param url      请求地址
     * @param param    请求参数
     * @param tag      设置tag
     * @param listener 回调监听
     */
    public static void doPost(Object ob_this, String url, final Map<String, String> param, Object tag,
                              final VolleyListener listener) {
        Context context = null;
        if (ob_this instanceof Fragment) {
            context = ((Fragment) ob_this).getActivity();
        } else if (ob_this instanceof Activity) {
            context = (Context) ob_this;
        } else {
            Log.e("VolleyRequset Error", "ob_this类型错误，请传入Fragment或者Activity实例");
            return;
        }
        listener.setThis(ob_this);
        initQueue(context);
        removeQueue(tag);
        StringRequest strRequest = new StringRequest(Method.POST, url, new Listener<String>() {

            @Override
            public void onResponse(String result) {
                listener.onFinish();
                listener.onSuccess(result);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                doErrorEvent(listener, error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return param;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                try {
                    parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers, getResponseCharset(param)));
                } catch (UnsupportedEncodingException var4) {
                    parsed = new String(response.data);
                }
                return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (param != null) {
                    return param;
                } else {
                    return new HashMap<String, String>();
                }
            }
        };
        strRequest.setRetryPolicy(RETRY_POLICY);
        strRequest.setTag(tag);
        strRequest.setShouldCache(true);
        requestQueue.add(strRequest);
        requestQueue.start();
        listener.onLoading();
    }

    /**
     * 服务器响应的编码，防止乱码
     *
     * @param param
     * @return
     */
    private static String getResponseCharset(Map<String, String> param) {
        String responseCharset = "utf-8";
        if (param != null) {
            responseCharset = param.get("charset");
            if (TextUtils.isEmpty(responseCharset)) {
                responseCharset = param.get("Charset");
            }
            if (TextUtils.isEmpty(responseCharset)) {
                responseCharset = "utf-8";
            }
        }
        return responseCharset;
    }

    /**
     * 请求失败处理逻辑
     *
     * @param listener
     * @param error
     */
    private static void doErrorEvent(VolleyListener listener, VolleyError error) {
        listener.onFinish();
        // 连接超时
        if (error instanceof TimeoutError) {
            listener.onTimeOut();
        }
        // 无网络连接
        else if (error instanceof NetworkError || error instanceof NoConnectionError) {
            listener.onNoNetwork();
        }
        // 连接服务器失败
        else if (error instanceof ServerError || error instanceof AuthFailureError) {
            listener.onServiceError();
        }
        listener.onFail(error);
    }

    /**
     * 将标识为tag的请求移除队列
     *
     * @param tag
     */
    public static void removeQueue(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }

}
