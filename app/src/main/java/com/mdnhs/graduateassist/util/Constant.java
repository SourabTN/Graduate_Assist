package com.mdnhs.graduateassist.util;

import com.mdnhs.graduateassist.item.GalleryList;
import com.mdnhs.graduateassist.response.AppRP;

import java.util.ArrayList;
import java.util.List;

public class Constant {

    //Change WebView text color light and dark mode
    public static String webViewText = "#8b8b8b;";
    public static String webViewTextDark = "#FFFFFF;";

    //Change WebView link color light and dark mode
    public static String webViewLink = "#0782C1;";
    public static String webViewLinkDark = "#5387ED;";

    public static final String AD_TYPE_ADMOB = "admob";
    public static final String AD_TYPE_FACEBOOK = "facebook";
    public static final String AD_TYPE_STARTAPP = "startapp";
    public static final String AD_TYPE_APPLOVIN = "applovins";
    public static final String AD_TYPE_WORTISE = "wortise";

    public static int AD_COUNT = 0, interstitialAdShow = 0, nativeAdPos = 5;

    public static AppRP appRP;

    public static List<GalleryList> galleryLists = new ArrayList<>();

}
