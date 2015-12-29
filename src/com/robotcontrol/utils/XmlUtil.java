package com.robotcontrol.utils;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class XmlUtil {

	public static String xml(InputStream input, String code) {
		try {
			XmlPullParser xmlPullParser = Xml.newPullParser();
			xmlPullParser.setInput(input, "utf-8");
			int eventtype = xmlPullParser.getEventType();
			while (eventtype != XmlPullParser.END_DOCUMENT) {
				switch (eventtype) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if (code.equals("xin")) {
						if (xmlPullParser.getName().equals("version")) {

							return xmlPullParser.nextText();
						}
					} else if (code.equals("download")) {
						if (xmlPullParser.getName().equals("url")) {

							return xmlPullParser.nextText();
						}
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				case XmlPullParser.END_DOCUMENT:
					break;
				default:
					break;
				}
				eventtype = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
