package com.wanfangdata.gne.extract;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ListExtractor{

	private static final int MAXCHILD = 100;
	private Set<String> urls = new LinkedHashSet<String>();
	private Set<Element> eles = new LinkedHashSet<Element>();

	public boolean extract(Element ele) {
		findList(ele.selectFirst("body"));
		if(eles.size()>0) {
			System.out.println("列表页");
			return true;
		}
		System.out.println("详情页");
		return false;
	}

	/**
	 * 子元素大于10,且标签相同，有a孙元素, a孙元素文本>4
	 * 递归查找list 集合
	 * @param ele
	 */
	public void findList(Element ele) {
		if (ele.children() != null) {
			if (ele.children().size() >= MAXCHILD) {
				
				Element grandChild = ele.child(1);
				String tag = grandChild.tagName();
				String selector;
				{
					selector = ele.cssSelector() + " > "+ tag;
				}
				// item 下有a标签,并且a标签文本长度>4 或有img
				if (ele.select(selector).size() >= MAXCHILD && grandChild.select(grandChild.tagName() + " a") != null && (grandChild.select(grandChild.tagName() + " a").text().length()>4 || grandChild.select(grandChild.tagName() + " a>img")!=null)) {
					eles.add(ele);
				}
			}
			for (Element child : ele.children()) {
				findList(child);
			}
		}
	}

	public boolean isListPage(Document doc)
	{
		boolean bListPage = false;

		Element eContent = doc.body();
		Elements contentLinks = eContent.select("a[href]");
		List<String> linkPath = new ArrayList<String>();

		for (Element link : contentLinks) {
			String pathstr = "<a";
			//Link的全部属性
			Attributes linkAttrs = link.attributes();
			List<Attribute> attrlistl = linkAttrs.asList();
			if(attrlistl!=null && attrlistl.size()>0){
				pathstr += " attrs='";
				for (Attribute attr : attrlistl) {
					pathstr += attr.getKey() + " ";
				}
				pathstr += "'";
			}
			//Link的全部class
			Set<String> linkClasses = link.classNames();
			if(linkClasses!=null && linkClasses.size()>0){
				pathstr += " class='";
				for (String str : linkClasses) {
					pathstr += str + " ";
				}
				pathstr += "'";
			}
			pathstr += "> | ";

			Element linkPrent = link.parent();
			while(linkPrent!=null )
			{
				pathstr += "<"+linkPrent.tagName();
				//Link父元素的所有属性
				Attributes parentAttrs = linkPrent.attributes();
				List<Attribute> attrlistp = parentAttrs.asList();
				if(attrlistp!=null && attrlistp.size()>0){
					pathstr += " attrs='";
					for (Attribute attr : attrlistp) {
						pathstr += attr.getKey() + " ";
					}
					pathstr += "'";
				}
				//Link父元素的所有class
				Set<String> parentClasses = linkPrent.classNames();
				if(parentClasses!=null && parentClasses.size()>0){
					pathstr += " class='";
					for (String str : parentClasses) {
						pathstr += str + " ";
					}
					pathstr += "'";
				}
				pathstr += "> | ";

				linkPrent = linkPrent.parent();
			}
			//System.out.println(pathstr);
			linkPath.add(pathstr);
		}

		for(int i=0; i<linkPath.size(); i++)
		{
			String currentLinkPath = linkPath.get(i);
			int count = 0;
			for(int j=0; j<linkPath.size(); j++)
			{
				String compareLinkPath = linkPath.get(j);
				if(compareLinkPath.equals(currentLinkPath))
				{
					count++;
				}
			}

			if(count>=5)
			{
				bListPage = true;
				break;
			}
		}

		return bListPage;
	}
}
