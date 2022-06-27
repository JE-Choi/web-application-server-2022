package webserver;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * request를 파싱하여, 요청 url과 header를 구한다.
 */
@Slf4j
@Getter
public class MyHttpServletRequest {
    private final String requestURL;
    private final Map<String, String> requestHeader;

    public MyHttpServletRequest(final BufferedReader bufferedReader) throws IOException {
        this.requestURL = bufferedReader.readLine();
        log.debug("request:line: {}", requestURL);

        this.requestHeader = this.getRequestHeader(bufferedReader);
        log.info(requestHeader.toString());
    }

    private Map<String, String> getRequestHeader(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> requestHeader = new HashMap<>();
        String readLine = bufferedReader.readLine();
        do {
            final String[] split = readLine.split(":");
            if (!this.requestURL.equals(readLine) && split.length == 2) {
                final String headerName = split[0].trim();
                final String headerValue = split[1].trim();
                requestHeader.put(headerName, headerValue);
            }
            readLine = bufferedReader.readLine();
        } while (!"".equals(readLine) && readLine != null);
        return requestHeader;
    }
}
