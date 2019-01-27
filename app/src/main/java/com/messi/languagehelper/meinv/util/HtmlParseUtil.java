package com.messi.languagehelper.meinv.util;

import android.text.TextUtils;

import com.messi.languagehelper.meinv.bean.MiaosouLinkBean;
import com.messi.languagehelper.meinv.box.CNWBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HtmlParseUtil {

	public static List<CNWBean> parseMiaosouHtml(String url,String html) {
		List<CNWBean> list = new ArrayList<CNWBean>();
		try {
			Document doc = Jsoup.parse(html);
			Elements basicmean = doc.select("div.content > li > a");
			if (basicmean != null && basicmean.size() > 0) {
				for(Element item : basicmean){
					String mhref = item.attr("href");
					String title = "";
					String update = "";
					String img = "";
					if(!TextUtils.isEmpty(mhref)){
						mhref = joinUrl(url,mhref);
						Element titleItem = item.getElementsByTag("span").first();
						if(titleItem != null){
							title = titleItem.text();
						}
						Element updateItem = item.getElementsByTag("i").first();
						if(updateItem != null){
							update = updateItem.text();
						}
						Element imgItem = item.getElementsByTag("img").first();
						if(imgItem != null){
							img = joinUrl(url,imgItem.attr("src"));
							LogUtil.DefalutLog("img:"+img);
						}
						CNWBean mCNWBean = new CNWBean();
						mCNWBean.setItemId(MD5.encode(mhref));
						mCNWBean.setTable(AVOUtil.Caricature.Caricature);
						mCNWBean.setTitle(title);
						mCNWBean.setUpdate_des(update);
						mCNWBean.setImg_url(img);
						mCNWBean.setRead_url(mhref);
						mCNWBean.setSource_url(mhref);
						mCNWBean.setSource_name("找漫画");
						LogUtil.DefalutLog("CNWBean:"+mCNWBean.toString());
						list.add(mCNWBean);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void parseMiaosouDetailHtml(String html,CNWBean mCNWBean) {
		try {
			List<MiaosouLinkBean> beans = new ArrayList<>();
			Document doc = Jsoup.parse(html);
			String title = "";
			String update = "";
			String author = "";
			String img = "";
			String tag = "";
			String des = "";
			Element titleElement = doc.select("div.show > h1").first();
			if(titleElement != null){
				title = titleElement.text();
				if(!TextUtils.isEmpty(title)){
					mCNWBean.setTitle(title);
				}
			}
			Element imgElement = doc.select("div.show > img").first();
			if(imgElement != null){
				img = joinUrl(mCNWBean.getSource_url(),imgElement.attr("src"));
				if(!TextUtils.isEmpty(img)){
					mCNWBean.setImg_url(img);
				}
			}
			Element desElement = doc.select("pre > code").first();
			if(desElement != null){
				des = desElement.text();
				if(!TextUtils.isEmpty(des)){
					mCNWBean.setDes(title);
				}
			}
			Elements lis = doc.select("div.show > ul > li");
			if(lis != null && lis.size() > 4){
				tag = lis.get(1).text();
				author = lis.get(2).text();
				update = lis.get(3).text();
				if(!TextUtils.isEmpty(tag)){
					mCNWBean.setTag(tag);
				}
				if(!TextUtils.isEmpty(author)){
					mCNWBean.setAuthor(author);
				}
				if(!TextUtils.isEmpty(update)){
					mCNWBean.setUpdate_des(update);
				}
			}
			Elements pagers = doc.select("div#comic > a");
			if (pagers != null && pagers.size() > 0) {
				for(Element item : pagers){
					MiaosouLinkBean bean = new MiaosouLinkBean();
					bean.setDes(item.text());
					bean.setLink(joinUrl(mCNWBean.getSource_url(),item.attr("href")));
					beans.add(bean);
				}
				if(beans.size() > 0){
					mCNWBean.setRead_url(beans.get(beans.size()-1).getLink());
				}
				mCNWBean.setMiaosouLinks(beans);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<CNWBean> parseOwllookListHtml(String url,String html) {
		List<CNWBean> list = new ArrayList<CNWBean>();
		try {
			Document doc = Jsoup.parse(html);
			Elements basicmean = doc.select("div.result_item.col-sm-9.col-xs-12");
			if (basicmean != null && basicmean.size() > 0) {
				for(Element item : basicmean){
					CNWBean mCNWBean = new CNWBean();
					mCNWBean.setTable(AVOUtil.Novel.Novel);
					mCNWBean.setSource_name("找小说");
					Element alink = item.select("li > a").first();
					if(alink != null){
						mCNWBean.setRead_url(joinUrl(url,alink.attr("href")));
						mCNWBean.setItemId(MD5.encode(mCNWBean.getRead_url()));
						mCNWBean.setTitle(alink.text());
					}
					Element slink = item.select("li > div.netloc > i > a").first();
					if(slink != null){
						mCNWBean.setSource_url(slink.attr("href"));
					}
					Elements tagsElement = item.select("div.tags > span");
					String subTitle = "";
					for (Element tagElement : tagsElement){
						subTitle += tagElement.text();
						subTitle += "   ";
					}
					mCNWBean.setSub_title(subTitle.trim().replace("解析","优化"));
//					if(!mCNWBean.getSub_title().contains("未优化")){
					list.add(mCNWBean);
//					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static String joinUrl(String curl,String file){
		URL url = null;
		String q = "";
		try {
			url = new   URL(new   URL(curl),file);
			q = url.toExternalForm();
		} catch (Exception e) {
			e.printStackTrace();
		}
		url = null;
		if(q.indexOf("#")!=-1)q = q.replaceAll("^(.+?)#.*?$", "$1");
		return q;
	}
}
