package com.messi.languagehelper.meinv.util;

import com.avos.avoscloud.AVObject;

public class AVOUtil {

	public static void addObject(){
		AVObject testObject = new AVObject(PracticeCategory.PracticeCategory);
		testObject.put(PracticeCategory.PCCode, "002");
		testObject.saveInBackground();
	}
	
	public static class PracticeCategory {
		
		public final static String PracticeCategory = "PracticeCategory";
		
		public final static String PCCode = "PCCode";
		
		public final static String PCName = "PCName";
		
		public final static String PCType = "PCType";
		
		public final static String PCIsValid = "PCIsValid";
		
		public final static String PCSign = "PCSign";
		
		public final static String PCImgUrl = "PCImgUrl";
		
		public final static String PCOrder = "PCOrder";
		
	}
	
	public static class PracticeCategoryList {
		
		public final static String PracticeCategoryList = "PracticeCategoryList";
		
		public final static String PCLCode = "PCLCode";
		
		public final static String PCLName = "PCLName";
		
		public final static String PCCode = "PCCode";
		
		public final static String PCLType = "PCLType";
		
		public final static String PCLIsValid = "PCLIsValid";
		
		public final static String PCLSign = "PCLSign";
		
		public final static String PCLImgUrl = "PCLImgUrl";
		
		public final static String PCLOrder = "PCLOrder";
		
	}
	
	public static class PracticeDetail {
		
		public final static String PracticeDetail = "PracticeDetail";
		
		public final static String PDCode = "PDCode";
		
		public final static String PDContent = "PDContent";
		
		public final static String PCCode = "PCCode";
		
		public final static String PCLCode = "PCLCode";
		
		public final static String PDType = "PDType";
		
		public final static String PDIsValid = "PDIsValid";
		
	}
	
	public static class StudyDialogCategory {
		
		public final static String StudyDialogCategory = "StudyDialogCategory";
		
		public final static String SDCode = "SDCode";
		
		public final static String SDName = "SDName";
		
		public final static String SDType = "SDType";
		
		public final static String SDIsValid = "SDIsValid";
		
		public final static String SDSign = "SDSign";
		
		public final static String SDImgUrl = "SDImgUrl";
		
		public final static String SDOrder = "SDOrder";
	}
	
	public static class StudyDialogListCategory {
		
		public final static String StudyDialogListCategory = "StudyDialogListCategory";
		
		public final static String SDLCode = "SDLCode";
		
		public final static String SDLName = "SDLName";
		
		public final static String SDCode = "SDCode";
		
		public final static String SDLType = "SDLType";
		
		public final static String SDLIsValid = "SDLIsValid";
		
		public final static String SDLSign = "SDLSign";
		
		public final static String SDLImgUrl = "SDLImgUrl";
		
		public final static String SDLOrder = "SDLOrder";
	}
	
	public static class StudyDialogDetail {
		
		public final static String StudyDialogDetail = "StudyDialogDetail";
		
		public final static String SDDCode = "SDDCode";
		
		public final static String SDDContent = "SDDContent";
		
		public final static String SDCode = "SDCode";
		
		public final static String SDLCode = "SDLCode";
		
		public final static String SDDType = "SDDType";
		
		public final static String SDDIsValid = "SDDIsValid";
		
	}
	
	public static class EvaluationType {
		
		public final static String EvaluationType = "EvaluationType";
		
		public final static String ETCode = "ETCode";
		
		public final static String ETName = "ETName";
		
		public final static String ETType = "ETType";
		
		public final static String ETIsValid = "ETIsValid";
		
		public final static String ECImgUrl = "ECImgUrl";
		
		public final static String ETOrder = "ETOrder";
	}
	
	public static class EvaluationCategory {
		
		public final static String EvaluationCategory = "EvaluationCategory";
		
		public final static String ECCode = "ECCode";
		
		public final static String ETCode = "ETCode";
		
		public final static String ECName = "ECName";
		
		public final static String ECType = "ECType";
		
		public final static String ECIsValid = "ECIsValid";
		
		public final static String ECSign = "ECSign";
		
		public final static String ECImgUrl = "ECImgUrl";
		
		public final static String ECOrder = "ECOrder";
	}
	
	public static class EvaluationCategoryList {
		
		public final static String EvaluationCategoryList = "EvaluationCategoryList";
		
		public final static String ECLCode = "ECLCode";
		
		public final static String ECLName = "ECLName";
		
		public final static String ECCode = "ECCode";
		
		public final static String ECLType = "ECLType";
		
		public final static String ECLIsValid = "ECLIsValid";
		
		public final static String ECLSign = "ECLSign";
		
		public final static String ECLImgUrl = "ECLImgUrl";
		
		public final static String ECLOrder = "ECLOrder";
	}
	
	public static class EvaluationDetail {
		
		public final static String EvaluationDetail = "EvaluationDetail";
		
		public final static String EDCode = "EDCode";
		
		public final static String EDContent = "EDContent";
		
		public final static String ECCode = "ECCode";
		
		public final static String ECLCode = "ECLCode";
		
		public final static String mp3 = "mp3";
		
		public final static String EDIsValid = "EDIsValid";
		
	}
	
	public static class EnglishWebsite {
		
		public final static String EnglishWebsite = "EnglishWebsite";
		
		public final static String Title = "Title";
		
		public final static String Describe = "Describe";

		public final static String ad_filte = "ad_filte";

		public final static String ImgUrl = "ImgUrl";

		public final static String category = "category";

		public final static String ShareMsg = "ShareMsg";
		
		public final static String Url = "Url";
		
		public final static String Order = "Order";
		
		public final static String IsValid = "IsValid";
		
	}
	
	public static class AppRecommendList {
		
		public final static String AppRecommendList = "AppRecommendList";
		
		public final static String AppTypeName = "AppTypeName";
		
		public final static String AppTypeCode = "AppTypeCode";
		
		public final static String AppTypeDescribe = "AppTypeDescribe";
		
		public final static String AppTypeIcon = "AppTypeIcon";
		
		public final static String Order = "Order";
		
		public final static String IsValid = "IsValid";
		
	}
	
	public static class AppRecommendDetail {
		
		public final static String AppRecommendDetail = "AppRecommendDetail";
		
		public final static String AppName = "AppName";
		
		public final static String AppCode = "AppCode";
		
		public final static String Apk = "Apk";
		
		public final static String DownloadType = "DownloadType";//apk  url
		
		public final static String AppTypeCode = "AppTypeCode";
		
		public final static String AppDescribe = "AppDescribe";
		
		public final static String AppSize = "AppSize";
		
		public final static String APPIcon = "APPIcon";
		
		public final static String APPUrl = "APPUrl";
		
		public final static String Order = "Order";
		
		public final static String IsValid = "IsValid";
		
		public final static String DownloadTimes = "DownloadTimes";
		
	}
	
	public static class UpdateInfo {
		
		public final static String UpdateInfo = "UpdateInfo";
		
		public final static String AppName = "AppName";

		public final static String AppCode = "AppCode";

		public final static String VersionCode = "VersionCode";

		public final static String Apk = "Apk";

		public final static String ad_ids = "ad_ids";

		public final static String no_ad_channel = "no_ad_channel";

		public final static String ad_type = "ad_type";

		public final static String uctt_url = "uctt_url";

		public final static String ucsearch_url = "ucsearch_url";

		public final static String wyyx_url = "wyyx_url";

		public final static String DownloadType = "DownloadType";//apk  url
		
		public final static String AppUpdateInfo = "AppUpdateInfo";
		
		public final static String AppSize = "AppSize";
		
		public final static String APPUrl = "APPUrl";
		
		public final static String IsValid = "IsValid";
		
		public final static String DownloadTimes = "DownloadTimes";

	}
	
	public static class SymbolDetail {
		
		public final static String SymbolDetail = "SymbolDetail";
		
		public final static String SDCode = "SDCode";
		
		public final static String SDName = "SDName";
		
		public final static String SDDes = "SDDes";
		
		public final static String SDInfo = "SDInfo";
		
		public final static String SDAudioMp3 = "SDAudioMp3";

		public final static String SDAudioMp3Url = "SDAudioMp3Url";

		public final static String SDTeacherMp3 = "SDTeacherMp3";

		public final static String SDTeacherMp3Url = "SDTeacherMp3Url";

		public final static String SDEnVideoFile = "SDEnVideoFile";

		public final static String SDEnVideoUrl = "SDEnVideoUrl";

		public final static String SDEnVideoImgUrl = "SDEnVideoImgUrl";

		public final static String SDIsValid = "SDIsValid";
		
	}
	
	public static class WordCategory {
		
		public final static String WordCategory = "WordCategory";
		
		public final static String category_id = "category_id";
		
		public final static String name = "name";
		
		public final static String isvalid = "isvalid";
		
		public final static String order = "order";
		
	}

	public static class HJWordStudyCategory {

		public final static String HJWordStudyCategory = "HJWordStudyCategory";

		public final static String category = "category";

		public final static String name = "name";

		public final static String ltype = "ltype";

		public final static String list_type = "list_type";

		public final static String isValid = "isValid";

		public final static String order = "order";

		public final static String type_code = "type_code";

	}

	public static class HJWordStudyCList {

		public final static String HJWordStudyCList = "HJWordStudyCList";

		public final static String category = "category";

		public final static String title = "title";

		public final static String word = "word";

		public final static String type = "type";

		public final static String word_des = "word_des";

		public final static String createdAt = "createdAt";

	}
	
	public static class WordStudyType {
		
		public final static String WordStudyType = "WordStudyType";
		
		public final static String category_id = "category_id";
		
		public final static String type_id = "type_id";
		
		public final static String title = "title";

		public final static String is_valid = "is_valid";
		
		public final static String word_num = "word_num";
		
		public final static String course_num = "course_num";
		
		public final static String img = "img";
		
		public final static String child_list_json = "child_list_json";
	}
	
	public static class WordStudyDetail {
		
		public final static String WordStudyDetail = "WordStudyDetail";
		
		public final static String class_id = "class_id";

		public final static String course = "course";

		public final static String class_title = "class_title";
		
		public final static String sound = "sound";
		
		public final static String desc = "desc";
		
		public final static String item_id = "item_id";
		
		public final static String symbol = "symbol";
		
		public final static String name = "name";
	}
	
	public static class CompositionType {
		
		public final static String CompositionType = "CompositionType";
		
		public final static String type_id = "type_id";
		
		public final static String type_name = "type_name";
		
		public final static String order = "order";
		
		public final static String is_valid = "is_valid";
	}
	
	public static class Reading {
		
		public final static String Reading = "Reading";

		public final static String type_id = "type_id";
		
		public final static String type_name = "type_name";
		
		public final static String title = "title";
		
		public final static String content = "content";
		
		public final static String item_id = "item_id";
		
		public final static String img_url = "img_url";
		
		public final static String publish_time = "publish_time";
		
		public final static String img_type = "img_type";
		
		public final static String source_name = "source_name";

		public final static String source_url = "source_url";

		public final static String img = "img";
		
		public final static String type = "type";
		
		public final static String media_url = "media_url";
		
		public final static String category = "category";

		public final static String category_2 = "category_2";

		public final static String level = "level";

		public final static String content_type = "content_type";

		public final static String lrc_url = "lrc_url";

	}
	
	public static class Category {
		
		public final static String Category = "ReadingCategory";
		
		public final static String isvalid = "isvalid";
		
		public final static String category_id = "category_id";
		
		public final static String name = "name";
		
		public final static String order = "order";
		
		public final static String composition = "composition";
		
		public final static String jokes = "jokes";
		
		public final static String shuangyu_reading = "shuangyu_reading";
		
		public final static String spoken_english = "spoken_english";
		
		public final static String listening = "listening";
		
		public final static String examination = "examination";
		
		public final static String symbol = "symbol";

		public final static String broadcast = "broadcast";

		public final static String business = "business";

		public final static String grammar = "grammar";

		public final static String word = "word";

		public final static String story = "story";
		
	}
	
	public static class ExaminationType {
		
		public final static String ExaminationType = "ExaminationType";
		
		public final static String type_id = "type_id";
		
		public final static String type_name = "type_name";
		
		public final static String order = "order";
		
		public final static String is_valid = "is_valid";
		
	}
	
	public static class InvestList {
		
		public final static String InvestList = "InvestList";
		
		public final static String name = "name";
		
		public final static String des = "des";
		
		public final static String createdAt = "createdAt";
		
	}

	public static class DailySentence {

		public final static String DailySentence = "DailySentence";

		public final static String content = "content";

		public final static String note = "note";

		public final static String tts = "tts";

		public final static String picture2 = "picture2";

		public final static String dateline = "dateline";

	}

	public static class Joke {
//		category 101 搞笑; 102 段子; 103 美女;
//		type 1 img; 2 none; 3 gif; 4 video; 5 text; 6 url；
		public final static String Joke = "Joke";

		public final static String category = "category";

		public final static String type = "type";

		public final static String text = "text";

		public final static String ratio = "ratio";

		public final static String video_url = "video_url";

		public final static String img = "img";

		public final static String createdAt = "createdAt";

		public final static String source_url = "source_url";

	}

	public static class SubjectList{
		public final static String SubjectList = "SubjectList";
		public final static String category = "category";
		public final static String name = "name";
		public final static String level = "level";
		public final static String code = "code";
		public final static String order = "order";
	}

	public static class SubjectType{
		public final static String SubjectType = "SubjectType";
		public final static String type_code = "type_code";
		public final static String name = "name";
		public final static String code = "code";
		public final static String order = "order";
	}

	public static class SearchHot{
		public final static String SearchHot = "SearchHot";
		public final static String click_time = "click_time";
		public final static String name = "name";
		public final static String type = "type";
		public final static String createdAt = "createdAt";
	}

	public static class XmlySearchHot{
		public final static String XmlySearchHot = "XmlySearchHot";
		public final static String click_time = "click_time";
		public final static String name = "name";
		public final static String type = "type";
		public final static String createdAt = "createdAt";
	}

	public static class CaricatureSearchHot{
		public final static String CaricatureSearchHot = "CaricatureSearchHot";
		public final static String click_time = "click_time";
		public final static String name = "name";
		public final static String type = "type";
		public final static String createdAt = "createdAt";
	}

	public static class Location{
		public final static String Location = "Location";
		public final static String network = "network";
		public final static String screen = "screen";
		public final static String address = "address";
		public final static String city = "city";
		public final static String longitude = "longitude";
		public final static String latitude = "latitude";
		public final static String uuid = "uuid";
		public final static String province = "province";
		public final static String country = "country";
		public final static String adcode = "adcode";
		public final static String district = "district";
		public final static String sdk = "sdk";
		public final static String version = "version";
		public final static String model = "model";
		public final static String brand = "brand";
	}

	public static class AdCategory{
		public final static String AdCategory = "AdCategory";
		//type 根据客户端tab分类，tab 1 为 01，2 为 02。。。
		public final static String type = "type";
		//subject name: english, math, handsoap, sound
		public final static String subject = "subject";
		public final static String name = "name";
		public final static String createdAt = "createdAt";
	}

	public static class AdList{
		public final static String AdList = "AdList";
		public final static String title = "title";
		public final static String sub_title = "sub_title";
		public final static String type = "type";
		public final static String app = "app";
		public final static String des = "des";
		public final static String subject = "subject";
		public final static String level = "level";
		public final static String isValid = "isValid";
		public final static String click_time = "click_time";
		public final static String url = "url";
		public final static String img = "img";
		public final static String imgs = "imgs";
		public final static String createdAt = "createdAt";
	}

	public static class XhDicList{
		public final static String XhDicList = "XinhuaDicList";
		public final static String name = "name";
		public final static String code = "code";
		public final static String type = "type";
		public final static String createdAt = "createdAt";
	}

	public static class XhDicSList{
		public final static String XhDicSList = "XinhuaDicSlist";
		public final static String name = "name";
		public final static String content = "content";
		public final static String code = "code";
		public final static String bs = "bs";
		public final static String createdAt = "createdAt";
	}

	public static class AdFilter{
		public final static String AdFilter = "AdFilter";
		public final static String name = "name";
		public final static String filter = "filter";
		public final static String isShowAd = "isShowAd";
	}

	public static class CantoneseCategory {

		public final static String CantoneseCategory = "CantoneseCategory";

		public final static String ECCode = "ECCode";

		public final static String ETCode = "ETCode";

		public final static String ECName = "ECName";

		public final static String ECIsValid = "ECIsValid";

		public final static String ECOrder = "ECOrder";

		public final static String ECImgUrl = "ECImgUrl";

		public final static String ECType = "ECType";

		public final static String ECSign = "ECSign";
	}

	public static class CantoneseEvaluationDetail {

		public final static String CantoneseEvaluationDetail = "CantoneseEvaluationDetail";

		public final static String EDCode = "EDCode";

		public final static String EDContent = "EDContent";

		public final static String ECCode = "ECCode";

		public final static String EDType = "EDType";

		public final static String mp3url = "mp3url";

		public final static String EDIsValid = "EDIsValid";

	}

	public static class Caricature {
		public final static String Caricature = "Caricature";
		public final static String name = "name";
		public final static String author = "author";
		public final static String book_img_url = "book_img_url";
		public final static String category = "category";
		public final static String createdAt = "createdAt";
		public final static String des = "des";
		public final static String isFree = "isFree";
		public final static String isValid = "isValid";
		public final static String order = "order";
		public final static String read_url = "read_url";
		public final static String source_name = "source_name";
		public final static String tag = "tag";
		public final static String type = "type";
		public final static String update = "update";
		public final static String url = "url";
		public final static String views = "views";
		//local
		public final static String history = "history";
		public final static String collected = "collected";
		public final static String lastUrl = "lastUrl";
		public final static String lastPosition = "lastPosition";
	}


}
