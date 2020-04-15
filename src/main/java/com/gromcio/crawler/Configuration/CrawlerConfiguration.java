package com.gromcio.crawler.Configuration;

import com.gromcio.crawler.Services.DefaultJsoupDocumentLoader;
import com.gromcio.crawler.Services.JsoupDocumentLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CrawlerConfiguration {

    @Bean
    public JsoupDocumentLoader getDocumentLoader() {
        return new DefaultJsoupDocumentLoader();
    }
}
