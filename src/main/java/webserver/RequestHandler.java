package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import db.DataBase;
import lombok.extern.slf4j.Slf4j;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

@Slf4j
public class RequestHandler extends Thread {
    private Socket connection;

    private String indexFileName = "/index.html";

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            // 요청 읽기 (requestURL, header)
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String readLine = bufferedReader.readLine();
            final String requestURL = readLine;
            log.debug("request:line: {}", requestURL);
            final Map<String, String> requestHeader = new HashMap<>();
            while (!"".equals(readLine) && readLine != null) {
                log.debug("read:line: {}", readLine);
                String[] split = readLine.split(":");
                if (!requestURL.equals(readLine)) {
                    String headerName = split[0];
                    String headerValue = split[1];
                    requestHeader.put(headerName, headerValue);
                }
                readLine = bufferedReader.readLine();
            }
            log.info(requestHeader.toString());
            // requestURL 분석 (https://www.beusable.net/blog/?p=1687)
            final String[] requestSplit = requestURL.split(" ");
            final String method = requestSplit[0];
            final String path = !"/".equals(requestSplit[1]) ? requestSplit[1] : "/index.html";
            final String version = requestSplit[2];

            byte[] body;
            String movePath = path;
            try {
                if ("GET".equals(method)) {
                    final String[] split = path.split("\\?");
                    final String url = split[0];
                    if (split.length > 1) {
                        final String queryString = split[1];
                        final Map<String, String> stringStringMap = HttpRequestUtils.parseQueryString(queryString);
                        if ("/user/create".equals(url)) {
                            DataBase.addUser(new User(stringStringMap));
                        }
                    }
                } else if ("POST".equals(method)) {
                    if ("/user/create".equals(path)) {
                        final String contentLength = requestHeader.get("Content-Length");
                        String requestBody = IOUtils.readData(bufferedReader, Integer.parseInt(contentLength.trim()));
                        final Map<String, String> stringStringMap = HttpRequestUtils.parseQueryString(requestBody);
                        DataBase.addUser(new User(stringStringMap));
                        DataOutputStream dos = new DataOutputStream(out);
                        response302Header(dos, indexFileName);
                        return;
                    }
                }
                body = Files.readAllBytes(new File("./webapp" + movePath).toPath());
                // 출력
                DataOutputStream dos = new DataOutputStream(out);
                response200Header(dos, body.length);
                responseBody(dos, body);
            } catch (IOException e) {
                DataOutputStream dos = new DataOutputStream(out);
                response302Header(dos, indexFileName);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + url + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
