/**
  * Copyright 2018 bejson.com 
  */
package com.messi.languagehelper.meinv.bean;
import java.util.List;

/**
 * Auto-generated: 2018-08-24 23:13:55
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class JsonRootBean {

    private String category;
    private String tag;
    private int startIndex;
    private int maxEnd;
    private String items;
    private List<SougoItem> all_items;
    private String newsResult;
    private int itemsOnPage;
    private String fromItem;
    private String groupList;
    private String resolution;

    public JsonRootBean(){}

    public void setCategory(String category) {
         this.category = category;
     }
     public String getCategory() {
         return category;
     }

    public void setTag(String tag) {
         this.tag = tag;
     }
     public String getTag() {
         return tag;
     }

    public void setStartIndex(int startIndex) {
         this.startIndex = startIndex;
     }
     public int getStartIndex() {
         return startIndex;
     }

    public void setMaxEnd(int maxEnd) {
         this.maxEnd = maxEnd;
     }
     public int getMaxEnd() {
         return maxEnd;
     }

    public void setItems(String items) {
         this.items = items;
     }
     public String getItems() {
         return items;
     }

    public void setAll_items(List<SougoItem> all_items) {
         this.all_items = all_items;
     }
     public List<SougoItem> getAll_items() {
         return all_items;
     }

    public void setNewsResult(String newsResult) {
         this.newsResult = newsResult;
     }
     public String getNewsResult() {
         return newsResult;
     }

    public void setItemsOnPage(int itemsOnPage) {
         this.itemsOnPage = itemsOnPage;
     }
     public int getItemsOnPage() {
         return itemsOnPage;
     }

    public void setFromItem(String fromItem) {
         this.fromItem = fromItem;
     }
     public String getFromItem() {
         return fromItem;
     }

    public void setGroupList(String groupList) {
         this.groupList = groupList;
     }
     public String getGroupList() {
         return groupList;
     }

    public void setResolution(String resolution) {
         this.resolution = resolution;
     }
     public String getResolution() {
         return resolution;
     }

}