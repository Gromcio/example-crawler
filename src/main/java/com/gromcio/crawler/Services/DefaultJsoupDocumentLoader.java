package com.gromcio.crawler.Services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;

public class DefaultJsoupDocumentLoader implements JsoupDocumentLoader {
    @Override
    public Document getDocumentForUrl(URL url) throws IOException {
        return Jsoup.parse(url, 8000);
    }
}
