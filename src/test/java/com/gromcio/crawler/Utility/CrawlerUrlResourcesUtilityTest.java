package com.gromcio.crawler.Utility;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class CrawlerUrlResourcesUtilityTest {

    @Test
    void shouldReturnProperUrl() {
        String uri = "https://google.pl";

        URL url = CrawlerUrlResourcesUtility.getUrlFromString(uri);

        Assert.isTrue(url != null && url.toString().equals(uri), "Didn't parse correct URL");
    }

    @Test()
    void shouldReturnNullForMalformedUrl() {
        String uri = "qweqwe";

        URL url = CrawlerUrlResourcesUtility.getUrlFromString(uri);

        Assert.isTrue(url == null, "Didn't properly catch exceptions and map value to null");
    }
}