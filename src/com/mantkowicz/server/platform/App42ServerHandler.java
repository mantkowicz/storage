package com.mantkowicz.server.platform;

import org.json.JSONException;
import org.json.JSONObject;

import com.mantkowicz.server.handler.AbstractServerHandler;
import com.shephertz.app42.paas.sdk.java.ServiceAPI;
import com.shephertz.app42.paas.sdk.java.customcode.CustomCodeService;

public class App42ServerHandler extends AbstractServerHandler {

	private ServiceAPI serviceAPI;
	private String API_KEY, SECRET_KEY;
	
	CustomCodeService customCodeService;
	
	public App42ServerHandler(String API_KEY, String SECRET_KEY) {
		super();
		this.API_KEY = API_KEY;
		this.SECRET_KEY = SECRET_KEY;
		
		//this.gson = new GsonBuilder().create();
	}
	
	@Override
	public void connect() {
		serviceAPI = new ServiceAPI(this.API_KEY, this.SECRET_KEY); 
		customCodeService = serviceAPI.buildCustomCodeService();
	}

	@Override
	public Long getServerTime() {
		Long serverTime = null;
		String name  = "ServerTimeProvider";
		JSONObject requestBody = new JSONObject();
		
		try {
			requestBody.put("name", name);
			JSONObject responseObject = customCodeService.runJavaCode(name, requestBody);
			
			serverTime = responseObject.getLong("time");
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}
		
		return serverTime;
	}
}
