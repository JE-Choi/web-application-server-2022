package util;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ATest {
    @Test
    public void test(){
        FileInputStream fis = null;
        InputStreamReader isr = null;
        OutputStreamWriter osw = null;
        try{
            // 소스 파일 지정
            fis = new FileInputStream("C:\\Users\\miridih\\IdeaProjects\\study\\web-application-server-master\\src\\test\\java\\data\\request.txt");
            // 파일의 내용을 문자로 읽기 위해서 InputStreamReader 로 생성
            isr = new InputStreamReader (fis);
            // Data의 표시 대상 지정 System.out 에 문자를 출력한다.
            osw = new OutputStreamWriter(System.out);

            char[] buffer= new char[512];
            int cnt = 0;
            while((cnt = isr.read(buffer)) != -1) {
                osw.write(buffer, 0, cnt);
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        } finally {
            try{
                fis.close();
                isr.close();
                osw.close();
            } catch(Exception e) {}
        }
    }
}
