package com.mantkowicz.server.platform;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import com.mantkowicz.server.handler.AbstractServerHandler;
import com.shephertz.app42.paas.sdk.java.ServiceAPI;
import com.shephertz.app42.paas.sdk.java.customcode.CustomCodeService;

public class App42ServerHandler extends AbstractServerHandler {
	private static final Logger LOGGER = Logger.getLogger(App42ServerHandler.class.getName());
	
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
	}

	public static Long getServerTime() {
		try {
			String TIME_SERVER = "0.europe.pool.ntp.org";
			NTPUDPClient timeClient = new NTPUDPClient();
			InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);

			TimeInfo timeInfo = timeClient.getTime(inetAddress);
			long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();

			LOGGER.log(Level.INFO, "Current time: " + returnTime);
			return returnTime;

		} catch (UnknownHostException e) {
			LOGGER.log(Level.SEVERE, "Couldn't get server time", e);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Couldn't get server time", e);
		}
		return null;
	}
}
