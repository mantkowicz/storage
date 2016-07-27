package com.mantkowicz.server.handler;


public abstract class AbstractServerHandler {
	public AbstractServerHandler() {
	}
		
	public abstract void connect();
	public abstract Long getServerTime();
}
