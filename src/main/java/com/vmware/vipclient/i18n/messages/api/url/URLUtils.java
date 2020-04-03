/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.url;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.vmware.vipclient.i18n.base.HttpRequester;

/**
 * 
 * Encapsulates some methods related to vIP Server.
 *
 */
public class URLUtils {

	public static final String BODY = "body";
    public static final String HEADERS = "headers";
    public static final String RESPONSE_CODE = "response_code";
    public static final String RESPONSE_MSG = "response_msg";
    public static final String RESPONSE_TIMESTAMP = "response_timestamp";
    public static final String IF_NONE_MATCH_HEADER = "If-None-Match";
    public static final String ETAG = "ETag";
    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String MAX_AGE_MILLIS = "max-age";
    
    private URLUtils() {

    }

    public static String appendParamToURL(final StringBuilder u, String key,
            String value) {
        if (u.toString().indexOf('?') >= 0 && u.toString().indexOf('=') >= 0) {
            if ("".equalsIgnoreCase(key)) {
                u.append("&").append(value);
            } else {
                u.append("&").append(key).append("=").append(value);
            }
        } else {
            if ("".equalsIgnoreCase(key)) {
                u.append("?").append(value);
            } else {
                u.append("?").append(key).append("=").append(value);
            }
        }
        return u.toString();
    }

    /**
     * Is the target String in list
     * 
     * @param list
     * @param containStr
     * @return if contain return true, else return false.
     */
    public static boolean isStringInListIgnoreCase(List<String> list,
            String targetStr) {
        for (String str : list) {
            if (null != str && str.equalsIgnoreCase(targetStr)) {
                return true;
            }
        }
        return false;
    }
    
    public static void addIfNoneMatchHeader(Map<String, Object> cacheProps, final HttpRequester requester) {
    	if (cacheProps != null && !cacheProps.isEmpty()) {
        	Map<String, List<String>> responseHeaders = (Map<String, List<String>>) cacheProps.get(HEADERS);
        	if (responseHeaders != null) {
	        	List<String> etags = (List<String>) responseHeaders.get(ETAG);
        		String ifNoneMatch = createIfNoneMatchValue(etags);
        		if (ifNoneMatch != null) {
        			Map<String, String> headers = new HashMap<String, String>();
        			headers.put(IF_NONE_MATCH_HEADER,ifNoneMatch);
        			requester.setCustomizedHeaderParams(headers);
        			return;
        		}
        	}
        }
        requester.removeCustomizedHeaderParams(IF_NONE_MATCH_HEADER);
    }
    
    public static Long getMaxAgeMillis(Map<String, List<String>> responseHeaders) {
    	Long maxAge = null;
    	if (responseHeaders != null) {
    		List<String> cacheCtrlString = (List<String>) responseHeaders.get(URLUtils.CACHE_CONTROL);
    		if (cacheCtrlString != null && !cacheCtrlString.isEmpty()) {
				for (String ccs : cacheCtrlString) { 
		    		String[] cacheCtrlDirectives = ccs.split(",");
		    		for (String ccd: cacheCtrlDirectives) {
		    			String[] ccdString = ccd.split("=");
		    			if (ccdString[0].equals(URLUtils.MAX_AGE_MILLIS)) {
		    				return Long.parseLong(ccdString[1]) * 1000l;
		    			}
		    		}
				}
	    	}
    	}
    	return maxAge;
    }
    
    private static String createIfNoneMatchValue(List<String> etags) {
    	if(etags == null || etags.isEmpty()) {
            return null;
        }
        final StringBuilder b = new StringBuilder();
        final Iterator<String> it = etags.iterator();
        b.append(it.next());
        while(it.hasNext()) {
            b.append(", ").append(it.next());
        }
        return b.toString();
    }
}
