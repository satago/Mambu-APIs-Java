/**
 * 
 */
package com.mambu.apisdk.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mambu.apisdk.model.Domain;
import com.mambu.apisdk.util.RequestExecutor.ContentType;
import com.mambu.apisdk.util.RequestExecutor.Method;

/**
 * Helper class for operations with the URL adresses
 * 
 * @author ipenciuc
 * 
 */
@Singleton
public class URLHelper {

	private String domainName;
	private static String WEB_PROTOCOL = "https";
	private static String API_ENDPOINT = "/api/";
	private static String DELIMITER = "?";

	private final static Logger LOGGER = Logger.getLogger(URLHelper.class.getName());

	@Inject
	public URLHelper(@Domain String domainName) {
		this.domainName = domainName;
	}

	/**
	 * Creates an URL String with an protocol, a domainName and some given details
	 * 
	 * @param details
	 *            the extra details
	 * 
	 * @return the created URL String in url-encoded format
	 */
	public String createUrl(String details) {
		details = details == null ? "" : details;

		// URL String must be url-encoded to handle spaces and UTF-8 chars (See MBU-4669, implemented in Mambu 3.4)
		String encodedUrl;
		try {

			URI uri = new URI(WEB_PROTOCOL, domainName, API_ENDPOINT + details, null);
			encodedUrl = uri.toString();

			return encodedUrl;

		} catch (URISyntaxException e) {

			LOGGER.severe("Exception message=" + e.getMessage() + " Failed to create URI for Domain Name=" + domainName
					+ " with url details=" + details);
			return "";
		}

	}

	/***
	 * Appends some params to a given URL String
	 * 
	 * @param urlString
	 *            the already created URL String
	 * @param paramsMap
	 *            the params which must be added
	 * 
	 * @return the complete URL
	 */
	public String createUrlWithParams(String urlString, ParamsMap paramsMap) {
		if (paramsMap != null) {
			return urlString + DELIMITER + paramsMap.getURLString();
		} else {
			return urlString;
		}
	}

	/**
	 * Add pagination params to a given URL String for POST with application/json content type. Pagination parameters
	 * shall be be added to the URL string. See MBU-8975. Only "offset" and "limit" parameters are added. For example,
	 * in API call POST {JSONFilterConstraints} /api/loans/search?offset=0&limit=5
	 * 
	 * @param urlString
	 *            original URL string
	 * @param method
	 *            method
	 * @param contentTypeFormat
	 *            content type format
	 * @param params
	 *            input parameters. If pagination parameters are added to the URL string then they are removed from the
	 *            original params map
	 * @return URL string with pagination parameters added for POST with application/json content type
	 */
	public String addJsonPaginationParams(String urlString, Method method, ContentType contentTypeFormat,
			ParamsMap params) {
		// Add only for POST with ContentType.JSON (for ContentType.WWW_FORM all params will be added to the URL)
		if (params == null || !(method == Method.POST && contentTypeFormat == ContentType.JSON)) {
			return urlString;
		}

		if (params.get(APIData.OFFSET) == null && params.get(APIData.LIMIT) == null) {
			return urlString;
		}
		// Create temporary params map
		ParamsMap paginationParams = new ParamsMap();
		paginationParams.put(APIData.OFFSET, params.get(APIData.OFFSET));
		paginationParams.put(APIData.LIMIT, params.get(APIData.LIMIT));

		// Add offset/limit to the URL string
		String urlWithParams = createUrlWithParams(urlString, paginationParams);

		// Remove pagination params already added to the URL
		params.remove(APIData.OFFSET);
		params.remove(APIData.LIMIT);

		return urlWithParams;
	}
}
