package com.argo.elasticsearch.spring;

import org.w3c.dom.Element;

/**
 * XML Parser helpers Source from:
 * http://www.java2s.com/Tutorial/Java/0440__XML/GetElementBooleanValue.htm
 */
public class XMLParserUtil {
	public static boolean getElementBooleanValue(Element element,
			String attribute) {
		return getElementBooleanValue(element, attribute, false);
	}

	public static boolean getElementBooleanValue(Element element,
			String attribute, boolean defaultValue) {
		if (!element.hasAttribute(attribute))
			return defaultValue;
		return Boolean.valueOf(getElementStringValue(element, attribute))
				.booleanValue();
	}

	public static String getElementStringValue(Element element, String attribute) {
		return element.getAttribute(attribute);
	}
}
