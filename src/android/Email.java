package com.jse52.email;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class Email extends CordovaPlugin {

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("initialize")) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    initialize(args, callbackContext);
                }
            });

            return true;
        } else if (action.equals("send")) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    connect(args, callbackContext);
                }
            });

            return true;
        } 
        return false;
    }

    protected void initialize(JSONArray args, CallbackContext callbackContext) {

    }

    protected void send(JSONArray args, final CallbackContext callbackContext) {

    }
}
