/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.local;

import java.util.Map;

import org.json.simple.JSONObject;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.messages.api.opt.MessageOpt;
import com.vmware.vipclient.i18n.messages.api.opt.Opt;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.FormatUtils;
import com.vmware.vipclient.i18n.util.JSONBundleUtil;

public class LocalMessagesOpt implements Opt, MessageOpt {
	
	private static final String OFFLINE_RESOURCE_PATH = "{0}/messages_{1}.json";
    private MessagesDTO dto;

    public LocalMessagesOpt(MessagesDTO dto) {
        this.dto = dto;
    }

    @Deprecated
    public JSONObject getComponentMessages() {
        return JSONBundleUtil.getMessages(dto.getLocale(), dto.getProductID(),
                dto.getVersion(), dto.getComponent());
    }
    
    @Override
    public void getComponentMessages(MessageCacheItem cacheItem) {
    	String pathName = VIPCfg.getInstance().getOfflineResourcesBaseUrl();
    	String filePath = FormatUtils.format(OFFLINE_RESOURCE_PATH, dto.getComponent(), dto.getLocale());
    	if (pathName != null) {
    		pathName = pathName.concat(filePath);
    	} else {
    		pathName = filePath;
    	}
    	Map<String, String> messages = JSONBundleUtil.getMessages(pathName);
    	cacheItem.addCachedData(messages);
    }

    @Override
    public String getString() {
        JSONObject jo = this.getComponentMessages();
        String k = dto.getKey();
        String v = "";
        if (jo != null) {
            v = jo.get(k) == null ? "" : v;
        }
        return v;
    }
	
}
