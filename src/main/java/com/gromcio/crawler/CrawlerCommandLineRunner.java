package com.gromcio.crawler;

import com.gromcio.crawler.Services.CustomCrawler;
import com.gromcio.crawler.Services.Report;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class CrawlerCommandLineRunner implements CommandLineRunner {

    final private CustomCrawler crawler;

    public CrawlerCommandLineRunner(CustomCrawler crawler) {
        this.crawler = crawler;
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
            System.out.println(String.format("Completed for: %s", report.getStartingPoint().toString()));

        } catch (IllegalArgumentException exception) {
            System.err.println(exception.getMessage());
        }
    }
}
