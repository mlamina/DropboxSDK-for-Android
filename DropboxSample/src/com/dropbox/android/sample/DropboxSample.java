/*
 * Copyright (c) 2010 Evenflow, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.dropbox.android.sample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.dropbox.android.*;
import com.dropbox.client.DropboxAPI;


public class DropboxSample extends Activity {
    //private static final String TAG = "DropboxSample";
    
    final static private String CONSUMER_KEY = "PUT_YOUR_CONSUMER_KEY_HERE";
    final static private String CONSUMER_SECRET = "PUT_YOUR_CONSUMER_SECRET_HERE";  
    
    private boolean mLoggedIn = false;
    private EditText mLoginEmail;
    private EditText mLoginPassword;
    private Button mSubmit;
    private TextView mText;
    private ProgressDialog mProgress;
    private Dropbox mDropbox;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mDropbox = new Dropbox(this, CONSUMER_KEY, CONSUMER_SECRET);
        
        mLoginEmail = (EditText)findViewById(R.id.login_email);
        mLoginPassword = (EditText)findViewById(R.id.login_password);
        mSubmit = (Button)findViewById(R.id.login_submit);
        mText = (TextView)findViewById(R.id.text);
        
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Please wait...");
        mProgress.setTitle("Signing in");
        
        mSubmit.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	if (mLoggedIn) {
            		// We're going to log out
            		mDropbox.deauthenticate();
            		setLoggedIn(false);
            		mText.setText("");
            	} else {
            		// Try to log in
            		getAccountInfo();
            	}
            }
        });
        
        
        if (mDropbox.authenticate()) {
        	// We can query the account info already, since we have stored 
        	// credentials
        	getAccountInfo();
        } 
    }
    
    
    /**
     * Notifies our Activity when a login process succeeded or failed.
     */
    DropboxLoginListener mLoginListener = new DropboxLoginListener() {

		@Override
		public void loginFailed(String message) {
			mProgress.dismiss();
			showToast("Login failed: "+message);
			setLoggedIn(false);
		}

		@Override
		public void loginSuccessfull() {
			mProgress.dismiss();
			showToast("Logged in!");
			setLoggedIn(true);		
			displayAccountInfo(mDropbox.accountInfo());
		}
    	
    };

    /**
     * Convenience function to change UI state based on being logged in
     */
    public void setLoggedIn(boolean loggedIn) {
    	mLoggedIn = loggedIn;
    	mLoginEmail.setEnabled(!loggedIn);
    	mLoginPassword.setEnabled(!loggedIn);
    	if (loggedIn) {
    		mSubmit.setText("Log Out of Dropbox");
    	} else {
    		mSubmit.setText("Log In to Dropbox");
    	}
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }
    
    private void getAccountInfo() {
    	
    	if (mDropbox.isAuthenticated()) {
    		// If we're already authenticated, we don't need to get the login info
    		mProgress.show();
    		mDropbox.login(mLoginListener);
	            		
    	} else {
    	
	        String email = mLoginEmail.getText().toString();
	        if (email.length() < 5 || email.indexOf("@") < 0 || email.indexOf(".") < 0) {
	            showToast("Error, invalid e-mail");
	            return;
	        }
	
	        String password = mLoginPassword.getText().toString();
	        if (password.length() < 6) {
	            showToast("Error, password too short");
	            return;
	        }

	        // It's good to do Dropbox API (and any web API) calls in a separate thread,
	        // so we don't get a force-close due to the UI thread stalling.
	        mProgress.show();
	        mDropbox.login(mLoginListener, email, password);
	        
    	}
    }

    /**
     * Displays some useful info about the account, to demonstrate
     * that we've successfully logged in
     * @param account
     */
    public void displayAccountInfo(DropboxAPI.Account account) {
    	if (account != null) {
    		String info = "Name: " + account.displayName + "\n" +
    			"E-mail: " + account.email + "\n" + 
    			"User ID: " + account.uid + "\n" +
    			"Quota: " + account.quotaQuota;
    		mText.setText(info);
    	}
    }
    
        
   
}