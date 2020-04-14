package com.gromcio.crawler.Services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

@Service
public class CustomCrawler {

    final private static Set<URL> visited = Collections.synchronizedSet(new HashSet<>());

    private static ForkJoinPool forkJoinPool;

    public CustomCrawler(@Value("${crawler.threads}") Integer threads) {
        forkJoinPool = new ForkJoinPool(threads);
    }

    /**
     * @param rawUri uri which will be used as starting point
     * @return Report of processed url
     */
    public Report crawl(String rawUri) {
        if (rawUri == null) {
            throw new IllegalArgumentException("Domain cannot be empty");
        }

        //Validate if it's proper url
        try {
            URL url = new URL(rawUri);

            visited.add(url);
            forkJoinPool.invoke(new CrawlingProcessor(url, visited));

            return Report.builder()
                    .startingPoint(url)
                    .build();
        } catch (MalformedURLException exception) {
            throw new IllegalArgumentException(String.format("Its not a valid url, reason: %s", exception.getMessage()));
        }
    }

    private static class CrawlingProcessor extends RecursiveAction {

        final private URL url;
        final private Set<URL> visited;

        public CrawlingProcessor(URL url, Set<URL> visited) {
            this.url = url;
            this.visited = visited;
        }

        @Override
        protected void compute() {
            try {
                System.out.println(String.format("Trying: %s", url.toString()));
                Document doc = Jsoup.parse(url, 8000);

                LinkedList<CrawlingProcessor> actions = new LinkedList<>();
                for (Element link : doc.select("a[href]")) {

                    try {
                        URL url = new URL(link.attr("abs:href"));
                        if (!visited.contains(url) && shouldFetchGivenUrl(url)) {
                            actions.add(new CrawlingProcessor(url, visited));
                            visited.add(url);
                        }
                    } catch (MalformedURLException ignored) {
                    }
                }

                invokeAll(actions);
            } catch (IOException ignored) {
            }
        }

        private boolean shouldFetchGivenUrl(URL url) {
            if (!url.getHost().equals(this.url.getHost())) {
                return false;
            }

            if (url.toString().endsWith(".jpeg") || url.toString().endsWith(".jpg") || url.toString().endsWith(".png") || url.toString().endsWith(".gif")) {
                return false;
            }

            return true;
        }
    }
}
