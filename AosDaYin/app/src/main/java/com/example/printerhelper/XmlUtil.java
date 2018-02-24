package com.example.printerhelper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

/**
 * 解析XML工具类 xml 格式 <?xml version="1.0"?> <Data> <row> <type>1</type>
 * <text>二堆码,xx分公司,xx局站,xx机房,负责人,联系电话,启用时间</text> </row> <row> <type>1</type>
 * <text>二堆码,xx分公司,xx局站,xx机房,负责人,联系电话,启用时间</text> </row> </Data>
 */
public class XmlUtil {
	public static List<PrintInfo> getPrintInfos(String xmlStr) throws Exception {
		List<PrintInfo> printInfos = null;
		PrintInfo printInfo = null;
		List<String> list = null;

		XmlPullParser parser = Xml.newPullParser();
		InputStream is = new ByteArrayInputStream(xmlStr.getBytes("UTF-8"));
		parser.setInput(is, "UTF-8");
		int event = parser.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				printInfos = new ArrayList<PrintInfo>();
				break;
			case XmlPullParser.START_TAG:
				if ("row".equals(parser.getName())) {
					printInfo = new PrintInfo();
					list = new ArrayList<String>();
				} else if ("type".equals(parser.getName())) {
					int type = Integer.valueOf(parser.nextText());
					printInfo.setType(type);
				} else if ("text".equals(parser.getName())) {
					String text = parser.nextText();
					list.add(text);
				}
				break;
			case XmlPullParser.END_TAG:
				if ("row".equals(parser.getName())) {
					printInfo.setTextlist(list);
					printInfos.add(printInfo);
					printInfo = null;
				}
				break;
			}
			event = parser.next();
		}

		return printInfos;
	}
}
