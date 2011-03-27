package com.dropbox.android;

public abstract class DropboxLoginListener {
	public abstract void loginSuccessfull();
	public abstract void loginFailed(String message);
}
