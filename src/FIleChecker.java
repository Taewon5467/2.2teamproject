import java.io.File;

public class FIleChecker {

    public static void main(String[] args) {
        
        // 확인할 파일 이름 (기본 - 프로젝트 루트 폴더에 있을 경우)
        String filePath = "C:\\Temp\\Paintext.txt";


        File f = new File(filePath);
        System.out.println("--- [파일 경로 디버깅] ---");
        System.out.println("1. 프로그램이 실행된 위치(Working Directory): " + new File("").getAbsolutePath());
        System.out.println("2. 프로그램이 찾으려는 파일의 전체 경로: " + f.getAbsolutePath());
        System.out.println("3. 파일이 실제로 존재?: " + f.exists());
        System.out.println("4. 파일의 크기 (bytes): " + f.length());
        System.out.println("--- [디버깅 끝] ---");
    }
}