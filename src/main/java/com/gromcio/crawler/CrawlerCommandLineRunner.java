package com.gromcio.crawler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gromcio.crawler.Services.CustomCrawler;
import com.gromcio.crawler.Services.JsoupDocumentLoader;
import com.gromcio.crawler.Services.Report;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class CrawlerCommandLineRunner implements CommandLineRunner {

    final private CustomCrawler crawler;

    public CrawlerCommandLineRunner(
            @Value("${crawler.threads}") Integer threads,
            JsoupDocumentLoader jsoupDocumentLoader
    ) {
        this.crawler = new CustomCrawler(threads, jsoupDocumentLoader);
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length < 1) {
            System.err.println("Provide domain string");
            return;
        }

        try {
            Report report = crawler.crawl(args[0]);
            //TODO display report in a nice way
            System.out.println(new ObjectMapper().writeValueAsString(report));

        } catch (IllegalArgumentException exception) {
            System.err.println(exception.getMessage());
        }
    }
}
