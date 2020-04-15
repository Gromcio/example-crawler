package com.gromcio.crawler.Services;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;

public interface JsoupDocumentLoader {
    public Document getDocumentForUrl(URL url) throws IOException;
}
