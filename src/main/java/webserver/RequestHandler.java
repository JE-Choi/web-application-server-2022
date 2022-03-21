package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

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
            String accept = "text/html";
            while (!"".equals(readLine) && readLine != null) {
                log.debug("read:line: {}", readLine);
                String[] split = readLine.split(":");
                if (!requestURL.equals(readLine)) {
                    String headerName = split[0];
                    String headerValue = split[1];
                    if ("Accept".equals(headerName)) {
                        accept = headerValue;
                    }
                }
                readLine = bufferedReader.readLine();
            }
            log.debug("accept: {}", accept);
            // requestURL 분석 (https://www.beusable.net/blog/?p=1687)
            final String[] requestSplit = requestURL.split(" ");
            final String method = requestSplit[0];
            final String path = !"/".equals(requestSplit[1]) ? requestSplit[1] : "/index.html";
            final String version = requestSplit[2];

            // todo: pathname이 유효하지 않아도, 에러발생하지 않음.
            final byte[] body = Files.readAllBytes(new File("./webapp" + path).toPath());

            // 출력
            DataOutputStream dos = new DataOutputStream(out);
            response200Header(dos, body.length);
            responseBody(dos, body);
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
