package com.gromcio.crawler.Services;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Assert;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;

@ExtendWith(MockitoExtension.class)
class CustomCrawlerTest {

    @Mock
    JsoupDocumentLoader loader;

    @Test
    void shouldReturnProperPageReport() throws Exception {
        String uri = "http://localhost/";
        URL url = new URL(uri);

        File mockDocumentFile = new File(this.getClass().getResource("/example.html").getFile());
        Document mockedDocument = Jsoup.parse(FileUtils.readFileToString(mockDocumentFile, Charset.defaultCharset()));

        Mockito.when(loader.getDocumentForUrl(Mockito.eq(url))).thenReturn(mockedDocument);
        Mockito.when(loader.getDocumentForUrl(AdditionalMatchers.not(Mockito.eq(url)))).thenReturn(new Document(""));

        CustomCrawler crawler = new CustomCrawler(1, loader);
        Report report = crawler.crawl(uri);

        Assert.isTrue(report.getStartingPoint().equals(url), "Report doesn't start at defined uri");
        Assert.isTrue(report.getPages().size() == 3, "Didn't add all visited pages");
        VisitedPage page = report.getPages()
                .stream()
                .filter(p -> p.getUrl().equals(url))
                .findFirst()
                .get();

        Assert.isTrue(page.getUrl().equals(url), "Wrong url for processed report page");
        Assert.isTrue(page.getLocalLinks().size() == 2, "Didnt find all local links");
        Assert.isTrue(page.getExternalLinks().size() == 1, "Didnt find all external links");
        Assert.isTrue(page.getFoundResourcesLinks().size() == 4, "Didnt find all resources");
    }

    @Test
    void shouldThrowExceptionForNullUri() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class, () -> {
                    CustomCrawler crawler = new CustomCrawler(1, loader);
                    crawler.crawl(null);
                }
        );

        Assert.isTrue(exception.getMessage().equals("Please provide proper url as starting point"), "Didn't return correct exception");
    }

    @Test
    void shouldThrowExceptionForMalformedUrl() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class, () -> {
                    CustomCrawler crawler = new CustomCrawler(1, loader);
                    crawler.crawl("qqqq");
                }
        );

        Assert.isTrue(exception.getMessage().contains("Its not a valid url, reason:"), "Didn't return correct exception");
    }
}