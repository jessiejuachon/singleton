/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.vmware.vip.i18n.BaseTestClass;
import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.base.cache.TranslationCacheManager;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;

public class CacheServiceTest extends BaseTestClass {

	String component = "JAVA";
    String key = "LeadTest";
    String source = "[{0}] Test alert";
    Locale locale = new Locale("de");
    String comment = "comment";
    Object[] args = { "a" };

    MessagesDTO dto = new MessagesDTO();
    
    @Before
    public void init() {
        dto.setComponent(component);
        dto.setKey(key);
        dto.setSource(source);
        dto.setLocale(locale.toLanguageTag());
        VIPCfg.resetInstance();
    }
    
    @Test
    public void testCacheNoUpdateIfErrorResponse() {
    	VIPCfg gc = VIPCfg.getInstance();
        try {
            gc.initialize("vipconfig");
        } catch (VIPClientInitException e) {
            logger.error(e.getMessage());
        }
    	gc.initializeVIPService();
        
        Cache c = VIPCfg.getInstance().createTranslationCache(MessageCache.class);
        TranslationCacheManager.cleanCache(c);
        I18nFactory i18n = I18nFactory.getInstance(VIPCfg.getInstance());
        TranslationMessage translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);
        
        dto.setProductID(VIPCfg.getInstance().getProductName());
        dto.setVersion(VIPCfg.getInstance().getVersion());
        
        // Explicitly set an empty string component to get an HTTP 404 from the service
        String emptyComponent = "";
    	dto.setComponent(emptyComponent);
    	CacheService cs = new CacheService(dto);
    	
    	MessageCacheItem cacheItem = cs.getCacheOfComponent();
    	assertNull(cacheItem);
    	
        // This triggers the first http call
    	translation.getMessage(locale, emptyComponent, key, args);
    	
    	cacheItem = cs.getCacheOfComponent();
    	assertNull(cacheItem);
    }
    
    @Test
    public void testNotExpired() {
    	VIPCfg gc = VIPCfg.getInstance();
        try {
            gc.initialize("vipconfig-no-cache-expired-time");
        } catch (VIPClientInitException e) {
            logger.error(e.getMessage());
        }
    	gc.initializeVIPService();
        
        Cache c = VIPCfg.getInstance().createTranslationCache(MessageCache.class);
        TranslationCacheManager.cleanCache(c);
        I18nFactory i18n = I18nFactory.getInstance(VIPCfg.getInstance());
        TranslationMessage translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);
        
        dto.setProductID(VIPCfg.getInstance().getProductName());
        dto.setVersion(VIPCfg.getInstance().getVersion());
        CacheService cs = new CacheService(dto);
        
        // CacheItem does not exist yet
        MessageCacheItem cacheItem = cs.getCacheOfComponent();
        assertNull(cacheItem);
        
        // This triggers the first http call
    	translation.getMessage(locale, component, key, args);
    	
    	cacheItem = cs.getCacheOfComponent();
        Long responseTime = (Long) cacheItem.getTimestamp();
        assertTrue(!cacheItem.isExpired());
        
        // Second request for the same message fetches from cache.
        translation.getMessage(locale, component, key, args);
        
        // No update should happen.
        try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        cacheItem = cs.getCacheOfComponent();
        Long responseTime2 = cacheItem.getTimestamp();
        assertEquals(responseTime2,responseTime); 
    }
    
    @Test
    public void testExpireUsingCacheControlMaxAge() {
    	VIPCfg gc = VIPCfg.getInstance();
        try {
            gc.initialize("vipconfig-no-cache-expired-time");
        } catch (VIPClientInitException e) {
            logger.error(e.getMessage());
        }
    	gc.initializeVIPService();
        
        Cache c = VIPCfg.getInstance().createTranslationCache(MessageCache.class);
        TranslationCacheManager.cleanCache(c);
        I18nFactory i18n = I18nFactory.getInstance(VIPCfg.getInstance());
        TranslationMessage translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);
        
        dto.setProductID(VIPCfg.getInstance().getProductName());
        dto.setVersion(VIPCfg.getInstance().getVersion());
        CacheService cs = new CacheService(dto);
        
        // CacheItem does not exist yet
        MessageCacheItem cacheItem = cs.getCacheOfComponent();
        assertNull(cacheItem);
        
        // This triggers the first http call
    	translation.getMessage(locale, component, key, args);
    	
    	cacheItem = cs.getCacheOfComponent();
        Long responseTime = (Long) cacheItem.getTimestamp();
        
        // Set max age to 0 to explicitly expire the cache for testing purposes.
        cacheItem.setMaxAgeMillis(0l);
        
        // Second request for the same message triggers an HTTP request because cacheItem has expired.
        // The http request includes an If-None-Match header that is set to the previously received eTag value.
        // The server responds with a 304 Not Modified.
        translation.getMessage(locale, component, key, args);
        
        // The responseTime will not be updated right away because it happens in a separate thread.
        Long responseTime2 = cacheItem.getTimestamp();
        assertTrue(responseTime2.equals(responseTime)); 
        assertTrue(cacheItem.getMaxAgeMillis() == 0l);
        
        // TODO: Teasting for asynchronous thread
        // The response time has been updated by the separate thread 
        // responseTime2 = cacheItem.getTimestamp();
        // assertTrue(responseTime2 > responseTime); 
        // assertTrue(cacheItem.getMaxAgeMillis() > 0l);
    }
    
    @Test
    @Deprecated
    public void testExpireUsingCacheExpiredTimeConfig() { 
    	VIPCfg gc = VIPCfg.getInstance();

        try {
            gc.initialize("vipconfig");
        } catch (VIPClientInitException e) {
            logger.error(e.getMessage());
        }
    	gc.initializeVIPService();
    	
    	// If cacheExpiredTime config is set, it means  that the value of this config will be used 
    	// to indicate cache expiration. Cache control max age from http response will be ignored.
    	assertTrue (VIPCfg.getInstance().getCacheExpiredTime() > 0l);
    	
    	Cache c = VIPCfg.getInstance().createTranslationCache(MessageCache.class);
    	TranslationCacheManager.cleanCache(c);
        I18nFactory i18n = I18nFactory.getInstance(VIPCfg.getInstance());
        TranslationMessage translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);
        
        dto.setProductID(VIPCfg.getInstance().getProductName());
        dto.setVersion(VIPCfg.getInstance().getVersion());
        CacheService cs = new CacheService(dto);
        
        // CacheItem does not exist yet
        MessageCacheItem cacheItem = cs.getCacheOfComponent();
        assertNull(cacheItem);
        
        // This triggers the first http call
    	translation.getMessage(locale, component, key, args);
    	
    	cacheItem = cs.getCacheOfComponent();
    	Long responseTime = cacheItem.getTimestamp();
         
    	//Explicitly expire the cache
    	c.setExpiredTime(0l);
        TranslationCacheManager.getCache(VIPCfg.CACHE_L3);
        
        // Second request for the same message triggers an HTTP request because cacheItem has expired.
        // The http request includes an If-None-Match header that is set to the previously received eTag value.
        // The server responds with a 304 Not Modified.
        translation.getMessage(locale, component, key, args);
        
        // The responseTime will not be updated right away because it happens in a separate thread.
        Long responseTime2 = cacheItem.getTimestamp();
        assertTrue(responseTime2.equals(responseTime)); 
        
        // Put the expiry time back
        c.setExpiredTime(VIPCfg.getInstance().getCacheExpiredTime());
        
        // Give time for the separate thread to finish.
        try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // Timestamp has been updated by the separate thread.
        cacheItem = cs.getCacheOfComponent();
        responseTime2 = cacheItem.getTimestamp();
        assertTrue(responseTime2 > responseTime); 
    }  
}
