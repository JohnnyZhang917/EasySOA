package org.easysoa.doctypes;

import java.util.HashMap;
import java.util.Map;

/**
 * ServiceAPI specific constants
 * @author mkalam-alami
 *
 */
public class ServiceAPI extends Doctype {

	public static final String DOCTYPE = "ServiceAPI";
	public static final String SCHEMA = "serviceapidef";
	public static final String SCHEMA_PREFIX = "api:";
	
	public static final String PROP_URL = "url";
	public static final String PROP_PARENTURL = "parentUrl";
	public static final String PROP_APPLICATION = "application";
	public static final String PROP_PROTOCOLS = "protocols";

	private static Map<String, String> propertyList = null;
	
	public static Map<String, String> getPropertyList() {
		if (propertyList == null) {
			propertyList = new HashMap<String, String>(); 
			propertyList.put(PROP_URL, "(mandatory) Service API url (WSDL address, parent path...).");
			propertyList.put(PROP_PARENTURL, "The parent URL, which is either another service API, or the service root.");
			propertyList.put(PROP_APPLICATION, "The related business application.");
			propertyList.put(PROP_PROTOCOLS, "The supported protocols.");
		}
		return propertyList;
	}
	
}
