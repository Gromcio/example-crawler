package com.gromcio.crawler.Services;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitedPage {
    private URL url;
    private Set<URL> localLinks;
    private Set<URL> externalLinks;
    private Set<URL> foundResourcesLinks;
}
