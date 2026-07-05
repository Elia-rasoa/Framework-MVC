package servlet.framework;

import java.util.Objects;

public class UrlKey {
    private String url;
    private String httpMethod;

    public UrlKey(String url, String httpMethod) {
        this.url = url;
        this.httpMethod = httpMethod.toUpperCase().trim();
    }

    public String getUrl() { return url; }
    public String getHttpMethod() { return httpMethod; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UrlKey urlKey = (UrlKey) o;
        return Objects.equals(url, urlKey.url) && 
               Objects.equals(httpMethod, urlKey.httpMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, httpMethod);
    }

    @Override
    public String toString() {
        return "[" + httpMethod + "] " + url;
    }
}