package com.example.printerhelper;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Paint;

public class StringUtil {
	/**
	 * 自动分割文本
	 * 
	 * @param contentStr
	 *            需要分割的文本
	 * @param p
	 *            画笔，用来根据字体测量文本的宽度
	 * @param width
	 *            指定的宽度
	 * @return 一个字符串数组，保存每行的文本
	 */
	public static ArrayList<String> autoSplit(String contentStr, Paint p,
			float width) {
		ArrayList<String> reList = new ArrayList<String>();
		String[] tempSplit = contentStr.split("\\n+");

		for (int x = 0; x < tempSplit.length; x++) {
			String content = tempSplit[x];
			if (content.length() > 0) {
				int length = content.length();
				float textWidth = p.measureText(content);
				if (textWidth <= width)
					reList.add(content);
				else {
					int start = 0, end = 1;
					while (start < length) {
						try {
							if (p.measureText(content, start, end) > width) {
								// 文本宽度超出控件宽度时
								reList.add((String) content.subSequence(start,
										end - 1));
								start = end - 1;
							}
							if (end == length) {
								// 不足一行的文本
								reList.add((String) content.subSequence(start,
										end));
								break;
							}
							end += 1;
						} catch (Exception ex) {
							break;
						}
					}
				}
			}
		}
		return reList;
	}

	public static List<String> reAutoSplit(List<String> list) {
		if (list.size() > 4) {
			String str = list.get(3);
			String newStr = str.substring(0, str.length() - 2) + "...";
			list.set(3, newStr);
		}
		return list;
	}

	/**
	 * 按要求处理字符串
	 * 
	 * @date 2013-11-12
	 * @param msg
	 * @return
	 */
	public static String handleString(String msg) {
		if (null != msg && msg.split("\\)\\(").length == 4) {
			String str[] = msg.split("\\)\\(");
			String newMsg = str[0] + ")~(" + str[1] + ")(" + str[2] + ")~("
					+ str[3];
			return newMsg;
		} else {
			return msg;
		}
	}

	public static String subString(String msg1, Paint paint) {
		List<String> list = autoSplit(msg1, paint, 340);
		if (list.size() > 4) {
			return list.get(0) + list.get(1) + "..."
					+ list.get(list.size() - 2) + list.get(list.size() - 1);
		} else {
			return msg1;
		}
	}

	/**
	 * 
	 * 按指定长度和编码拆分中英混合字符串
	 * 
	 * @param text
	 *            被拆分字符串
	 * 
	 * @param length
	 *            指定长度，即子字符串的最大长度
	 * 
	 * @param encoding
	 *            字符编码
	 * 
	 * @return 拆分后的子字符串列表
	 * 
	 * @throws Exception
	 */

	public static List<String> split(String text, int length, String encoding)
			throws Exception {
		List<String> texts = new ArrayList<String>();
		int pos = 0;
		int startInd = 0;

		for (int i = 0; text != null && i < text.length();) {
			byte[] b = String.valueOf(text.charAt(i)).getBytes(encoding);
			if (b.length > length) {
				i++;
				startInd = i;
				continue;
			}

			pos += b.length;
			if (pos >= length) {
				int endInd;
				if (pos == length) {
					endInd = ++i;
				} else {
					endInd = i;
				}

				texts.add(text.substring(startInd, endInd));
				pos = 0;
				startInd = i;
			} else {
				i++;
			}
		}
		if (startInd < text.length()) {
			texts.add(text.substring(startInd));
		}
		return texts;
	}
}
