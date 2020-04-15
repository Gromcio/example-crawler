package com.gromcio.crawler.Utility;

import java.net.MalformedURLException;
import java.net.URL;

public class CrawlerUrlResourcesUtility {
    public static URL getUrlFromString(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException exception) {
            //If it's not url then for purpose of this project simply return null
            return null;
        }
    }
}
