package com.example.printerhelper;

import java.util.List;

/**
 * PrintInfo 类
 * 
 * @author hcou
 * @date 2014年5月20日
 */
public class PrintInfo {
	private int type;
	private List<String> textlist;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<String> getTextlist() {
		return textlist;
	}

	public void setTextlist(List<String> textlist) {
		this.textlist = textlist;
	}
}
