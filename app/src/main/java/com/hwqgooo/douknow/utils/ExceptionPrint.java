package com.hwqgooo.douknow.utils;

import android.util.Log;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by weiqiang on 2016/7/27.
 */
public class ExceptionPrint {
    public static void print(Throwable e, String TAG) {
        if (e instanceof UnknownHostException) {
            Log.d(TAG, "ExceptionPrint: UnknownHostException");
        } else if (e instanceof SocketTimeoutException) {
            Log.d(TAG, "ExceptionPrint: SocketTimeoutException");
        } else if (e instanceof SocketException) {
            Log.d(TAG, "onError: " + e.toString());
        } else if (e instanceof HttpException) {
            Log.d(TAG, "onError: " + e.toString());
        } else {
            e.printStackTrace();
        }
    }
}
