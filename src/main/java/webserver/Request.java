package webserver;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class Request {
    private final String method;
    private final String path;
    private final String version;
    private final Map<String, String> stringStringMap;
}