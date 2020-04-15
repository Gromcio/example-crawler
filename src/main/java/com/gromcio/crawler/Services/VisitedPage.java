package com.gromcio.crawler.Services;

import lombok.*;

import java.net.URL;
import java.util.Objects;
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
    private boolean hadErrors = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisitedPage that = (VisitedPage) o;
        return Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
