package com.gromcio.crawler.Services;

import com.gromcio.crawler.Utility.CrawlerUrlResourcesUtility;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.stream.Collectors;

public class CustomCrawler {

    final private ForkJoinPool forkJoinPool;
    final private JsoupDocumentLoader jsoupDocumentLoader;

    public CustomCrawler(Integer threads, JsoupDocumentLoader jsoupDocumentLoader) {
        this.forkJoinPool = new ForkJoinPool(threads);
        this.jsoupDocumentLoader = jsoupDocumentLoader;
    }

    /**
     * @param rawUri uri which will be used as starting point
     * @return Report of processed url
     */
    public Report crawl(String rawUri) {
        if (rawUri == null) {
            throw new IllegalArgumentException("Please provide proper url as starting point");
        }

        //Validate if it's proper url
        try {
            CrawlerFoundUrlsStore urlsStore = new HasSetCrawlerFoundUrlsStore();
            URL url = new URL(rawUri);
            forkJoinPool.invoke(new CrawlPageAction(url, urlsStore, jsoupDocumentLoader));

            return Report.builder()
                    .startingPoint(url)
                    .pages(urlsStore.getVisitedPages())
                    .build();
        } catch (MalformedURLException exception) {
            throw new IllegalArgumentException(String.format("Its not a valid url, reason: %s", exception.getMessage()));
        }
    }

    private static class HasSetCrawlerFoundUrlsStore implements CrawlerFoundUrlsStore {
        final private Set<VisitedPage> visitedPages = Collections.synchronizedSet(new HashSet<>());
        final private Set<URL> foundUrls = Collections.synchronizedSet(new HashSet<>());

        @Override
        public void addFoundUrl(URL url) {
            foundUrls.add(url);
        }

        @Override
        public boolean isUrlAlreadyFound(URL url) {
            return foundUrls.contains(url);
        }

        @Override
        public void addVisitedPage(VisitedPage page) {
            visitedPages.add(page);
        }

        @Override
        public Integer getVisitedPagesCount() {
            return visitedPages.size();
        }

        @Override
        public Set<VisitedPage> getVisitedPages() {
            return visitedPages;
        }
    }

    private static class CrawlPageAction extends RecursiveAction {

        final private URL url;
        final private CrawlerFoundUrlsStore urlsStore;
        final private JsoupDocumentLoader jsoupDocumentLoader;

        public CrawlPageAction(
                URL url,
                CrawlerFoundUrlsStore urlsStore,
                JsoupDocumentLoader jsoupDocumentLoader
        ) {
            this.url = url;
            this.urlsStore = urlsStore;
            this.jsoupDocumentLoader = jsoupDocumentLoader;
        }

        @Override
        protected void compute() {
            try {
                if (!urlsStore.isUrlAlreadyFound(url)) {
                    urlsStore.addFoundUrl(url);
                    Document doc = jsoupDocumentLoader.getDocumentForUrl(url);

                    VisitedPage page = getReportForPage(doc);

                    List<CrawlPageAction> actions;

                    urlsStore.addVisitedPage(page);

                    actions = page.getLocalLinks()
                            .stream()
                            .filter(link -> !urlsStore.isUrlAlreadyFound(link) && shouldFetchGivenUrl(link))
                            .map(link -> new CrawlPageAction(link, urlsStore, jsoupDocumentLoader))
                            .collect(Collectors.toList());

                    if (!actions.isEmpty()) {
                        invokeAll(actions);
                    }
                }
            } catch (IOException ignored) {
                urlsStore.addVisitedPage(
                        VisitedPage
                                .builder()
                                .hadErrors(true)
                                .build()
                );
            }
        }

        private VisitedPage getReportForPage(Document doc) {

            Map<Boolean, List<URL>> links = doc.select("a[href]")
                    .stream()
                    .map(e -> e.attr("abs:href"))
                    .map(CrawlerUrlResourcesUtility::getUrlFromString)
                    .filter(Objects::nonNull)
                    .filter(this::isWebpageLink)
                    .collect(Collectors.partitioningBy(this::isLocalWebpage));

            Set<URL> localLinks = new HashSet<>(links.get(true));

            Set<URL> resources = doc.select("script[src],link[href],img[src]")
                    .stream()
                    .map(e -> {
                        String url = e.attr("abs:src");
                        if (url.isEmpty()) {
                            url = e.attr("abs:href");
                        }

                        return url;
                    })
                    .map(CrawlerUrlResourcesUtility::getUrlFromString)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            return VisitedPage.builder()
                    .url(url)
                    .localLinks(localLinks)
                    .externalLinks(new HashSet<>(links.get(false)))
                    .foundResourcesLinks(resources)
                    .build();
        }

        private boolean isWebpageLink(URL url) {
            //TODO make something smarter to detect webpage links than just filtering out images
            return !url.toString().endsWith(".jpeg") && !url.toString().endsWith(".jpg") && !url.toString().endsWith(".png") && !url.toString().endsWith(".gif");
        }

        private boolean isLocalWebpage(URL url) {
            return url.getHost().equals(this.url.getHost());
        }

        private boolean shouldFetchGivenUrl(URL url) {
            return isWebpageLink(url) && isLocalWebpage(url);
        }
    }
}
