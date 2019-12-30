package com.wanfangdata.gne.extract;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class TitleExtractor{

	private static final String[] patterns = {"h3.text-center","title","h1", "h2", "h3"};

	public String extract(Element ele) {
		String result;
		result = extractByAuto(ele);
		int index = result.lastIndexOf("-");
		if (index > 0 && result.length() / 2 < index)
			result = result.substring(0, index);
//		System.out.println("title: " + result);
		return result;
	}

	public String extractByAuto(Element ele) {
		String result = extractByMeta(ele);
		if(result == "") {
			result = extractByTag(ele);
		}
		return result;
	}
	
	public void extract(Element ele, String pattern) {
		String result = extractByPattern(ele,pattern);
		if(result == null || "".equals(result)) {
			result = extractByTag(ele);
		}
		System.out.println("title" + result);
	}

	private String extractByTag(Element ele) {
		String result = "";
		for (int i = 0; i < patterns.length; i++) {
			Element selectFirst = ele.selectFirst(patterns[i]);
			if(selectFirst !=null) {
				result = selectFirst.text();
				return result;
			}
		}
		return "";
	}

	private String extractByMeta(Element ele) {
		String result;
		Elements select = ele.select("meta");
		if(select!=null) {
			for(Element child:select) {
				Attributes attributes = child.attributes();
				for(Attribute attr:attributes) {
					if(attr.getValue()!="" && attr.getValue().contains("title")) {
						result = child.attr("content");
						return result;
					}
				}
			}
		}
		return "";
	}
	private String extractByPattern(Element ele, String pattern) {
		String content = ele.text();
		Pattern compile = Pattern.compile(pattern);
		Matcher matcher = compile.matcher(content);
		if (matcher.find()) {
			String title = matcher.group(1);
			System.out.println("title: "+ title);
			return title;
		}
		return "";
	}

}
