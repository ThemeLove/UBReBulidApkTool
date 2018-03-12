package com.umbrella.ubsdk.rebuild;

import java.io.File;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class KeystoreXMLParser {
	public static Keystore parser(String xmlPath) {
		Keystore keystore = null;
		try {
			SAXReader saxReader = new SAXReader();
	        Element root = saxReader.read(new File(xmlPath)).getRootElement();
	        if(root==null) {
	        	System.out.println("该Keystore没有配置，请配置" + xmlPath);
	        	System.exit(0);
	        } else {
	        	keystore = new Keystore();
	        	keystore.setName(root.attributeValue("name"));
	        	keystore.setPasword(root.attributeValue("password"));
	        	Element alias = root.element("alias");
	        	keystore.setAlias(alias.attributeValue("name"));
	        	keystore.setAliasPwd(alias.attributeValue("password"));
        	}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return keystore;
	}
}
