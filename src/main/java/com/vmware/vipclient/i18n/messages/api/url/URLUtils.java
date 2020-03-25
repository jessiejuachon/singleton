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
        	Map<String, List<String>> responseHeaders = (Map<String, List<String>>) cacheProps.get(HttpRequester.HEADERS);
        	if (responseHeaders != null) {
	        	List<String> etags = (List<String>) responseHeaders.get(requester.ETAG);
	        	if (etags != null) {
	        		String ifNoneMatch = createIfNoneMatchValue(etags);
	        		Map<String, String> headers = new HashMap<String, String>();
	        		headers.put(HttpRequester.IF_NONE_MATCH_HEADER,ifNoneMatch);
	        		requester.setCustomizedHeaderParams(headers);
	        	}
        	}
        } else {
        	requester.removeCustomizedHeaderParams(HttpRequester.IF_NONE_MATCH_HEADER);
        }
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
