package org.easysoa.test.rest.tools;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RestNotificationFactory {

	public final static String NUXEO_URL_LOCALHOST = "http://localhost:8080/nuxeo";
	
	public enum RestNotificationAPI {
		APPLIIMPL, SERVICEAPI, SERVICE;
	}

	private static final Log log = LogFactory.getLog(RestNotificationFactory.class);
	
	private final static String API_PATH = "/site/easysoa/notification/";
	private final static String API_APPLIIMPL = "appliimpl";
	private final static String API_SERVICEAPI = "api";
	private final static String API_SERVICE = "service";
	
	private Map<RestNotificationAPI, String> apiUrls;
	
	/**
	 * Creates a new notification factory.
	 * @param server (ex: RestNotificationFactory.NUXEO_URL_LOCALHOST)
	 * @throws IOException 
	 */
	public RestNotificationFactory(String nuxeoUrl) throws IOException {
		
		// Test connection
		new URL(nuxeoUrl+"/site").openConnection();
		
		// Store API URLs
		String notificationApiUrl = nuxeoUrl + API_PATH;
		apiUrls = new HashMap<RestNotificationAPI, String>();
		apiUrls.put(RestNotificationAPI.APPLIIMPL, notificationApiUrl + API_APPLIIMPL);
		apiUrls.put(RestNotificationAPI.SERVICEAPI, notificationApiUrl + API_SERVICEAPI);
		apiUrls.put(RestNotificationAPI.SERVICE, notificationApiUrl + API_SERVICE);
		
	}
	
	public RESTNotification createNotification(RestNotificationAPI api) {
		try {
			RESTNotificationImpl notif = new RESTNotificationImpl(apiUrls.get(api));
			return notif;
		} catch (MalformedURLException e) {
			log.error(e);
			return null;
		}
	}

}
