package webserver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MyHttpServletRequestTest {
    private static String FILE_URL = "C:\\Users\\miridih\\IdeaProjects\\study\\web-application-server-master\\src\\test\\java\\data\\request.txt";
    final Map<String, String> expectRequestHeader = new HashMap<String, String>();

    @Before
    public void setUp() throws Exception {
        expectRequestHeader.clear();
        expectRequestHeader.put("Connection", "keep-alive");
        expectRequestHeader.put("Cache-Control", "max-age=0");
        expectRequestHeader.put("sec-ch-ua", "\"Not A;Brand\";v=\"99\", \"Chromium\";v=\"101\", \"Google Chrome\";v=\"101\"");
        expectRequestHeader.put("sec-ch-ua-mobile", "?0");
        expectRequestHeader.put("sec-ch-ua-platform", "\"Windows\"");
        expectRequestHeader.put("Upgrade-Insecure-Requests", "1");
        expectRequestHeader.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.67 Safari/537.36");
        expectRequestHeader.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        expectRequestHeader.put("Sec-Fetch-Site", "none");
        expectRequestHeader.put("Sec-Fetch-Mode", "navigate");
        expectRequestHeader.put("Sec-Fetch-User", "?1");
        expectRequestHeader.put("Sec-Fetch-Dest", "document");
        expectRequestHeader.put("Accept-Encoding", "gzip, deflate, br");
        expectRequestHeader.put("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
    }

    @Test
    public void test() throws Exception {
        try (FileInputStream fis = new FileInputStream(FILE_URL); BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));) {
            final MyHttpServletRequest servletRequest = new MyHttpServletRequest(bufferedReader);
            Assert.assertEquals("GET / HTTP/1.1", servletRequest.getRequestURL());
            Assert.assertEquals(expectRequestHeader, servletRequest.getRequestHeader());

        }
    }
}