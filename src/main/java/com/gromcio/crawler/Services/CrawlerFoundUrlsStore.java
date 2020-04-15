package com.gromcio.crawler.Services;

import java.net.URL;
import java.util.Set;

public interface CrawlerFoundUrlsStore {

    public void addFoundUrl(URL url);

    public boolean isUrlAlreadyFound(URL url);

    public void addVisitedPage(VisitedPage page);

    public Set<VisitedPage> getVisitedPages();

    public Integer getVisitedPagesCount();
}
