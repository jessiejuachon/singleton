/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.base.instances.Formatting;
import com.vmware.vipclient.i18n.base.instances.Message;

/**
 * provide a factory to create all kind of I18n instances
 */
public class I18nFactory {

	Logger logger = LoggerFactory.getLogger(I18nFactory.class);

	// provide global setting
	private VIPCfg cfg;

	// define global instance of I18nFactory
	private static I18nFactory factory = null;

	// store Message instance
	private Map<String, Message> messages = new HashMap<String, Message>();

	// store Formatting instance
	private Map<String, Formatting> formattings = new HashMap<String, Formatting>();

	/**
	 * create I18nFactory
	 * 
	 * @param cfg
	 *            define the environment setting of the framework
	 */
	private I18nFactory(VIPCfg cfg) {
		this.cfg = cfg;
	}

	/**
	 * get an instance of I18nFactory
	 * 
	 * @param cfg
	 * @return
	 */
	public static synchronized I18nFactory getInstance(VIPCfg cfg) {
		if (factory == null) {
			factory = new I18nFactory(cfg);
		}
		return factory;
	}

	/**
	 * get the instance of I18nFactory
	 * 
	 * @return
	 */
	public static I18nFactory getInstance() {
		return factory;
	}

	/**
	 * get an instance of com.vmware.vipclient.i18n.base.instances.Message
	 * 
	 * @param c
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Message getMessageInstance(Class c) {
		Message i = null;
		if (c == null) {
			return i;
		} else if (this.getCfg().getVipServer() == null
				|| this.getCfg().getProductName() == null
				|| this.getCfg().getVersion() == null) {
			logger.error("VipServer|ProductName|Version is null!");
			return i;
		}
		String key = c.getCanonicalName();
		if (messages.containsKey(key)) {
			return messages.get(key);
		} else {
			try {
				Object o = c.newInstance();
				if (o instanceof Message) {
					i = (Message) o;
					messages.put(key, i);
				}
			} catch (InstantiationException | IllegalAccessException e) {
				logger.error(e.getMessage());
			}
		}
		return i;
	}

	/**
	 * get a instance of com.vmware.vipclient.i18n.base.instances.Formatting
	 * 
	 * @param c
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Formatting getFormattingInstance(Class c) {
		Formatting i = null;
		if (c == null) {
			logger.error("the parameter class is null!");
			return i;
		} else if(this.getCfg().getI18nScope() == null) {
			logger.error("i18nScope is null!");
			return i;
		}else {
			String key = c.getCanonicalName();
			if (formattings.containsKey(key)) {
				return formattings.get(key);
			} else {
				try {
					Object o = c.newInstance();
					if (o instanceof Formatting) {
						i = (Formatting) o;
						formattings.put(key, i);
					}
				} catch (InstantiationException | IllegalAccessException e) {
					logger.error(e.getMessage());
				}
			}
			return i;
		}
	}

	public Map<String, Message> getMessages() {
		return messages;
	}

	public void setMessages(Map<String, Message> messages) {
		this.messages = messages;
	}

	public Map<String, Formatting> getFormattings() {
		return formattings;
	}

	public void setFormattings(Map<String, Formatting> formattings) {
		this.formattings = formattings;
	}

	public VIPCfg getCfg() {
		return cfg;
	}

	public void setCfg(VIPCfg cfg) {
		this.cfg = cfg;
	}

}