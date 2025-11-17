import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet; // 중복 확인용
import java.io.File;
import java.io.FileNotFoundException; //파일 예외처리
import java.util.Scanner; //파일 읽기
import java.nio.charset.StandardCharsets; // UTF-8로 인코딩

class create_database { //db생성
    public void DataBase(){
        String url = "jdbc:mysql://localhost:3306/"; // MySQL 서버 자체에 연결
        String id = "root";
        String pw = "ansxodnjs5467";
        String dbname = "Accounts";

        String sql = "CREATE DATABASE IF NOT EXISTS " + dbname;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            System.out.println("데이터 베이스 연결 중...");

            try (Connection conn = DriverManager.getConnection(url, id, pw);
                 Statement stmt = conn.createStatement()) {
                
                System.out.println("연결 성공");
                stmt.executeUpdate(sql);
                System.out.println("데이터베이스 " + dbname + " 생성 완료 (또는 이미 존재함)");
            } 
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버를 찾을 수 없습니다.");
            e.printStackTrace();
        } catch (SQLException se) {
            System.out.println("SQL 오류가 발생했습니다.");
            se.printStackTrace();
        } catch (Exception e) {
            System.out.println("기타 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }
}

class create_table { //테이블 생성
    public void table(){
        String url = "jdbc:mysql://localhost:3306/Accounts";
        String id = "root";
        String pw = "ansxodnjs5467";
        String tableName = "Users"; // 유저 계정
        String tableName1 = "Solution"; // 솔루션 종류
        String tableName2 = "Routines"; // 루틴
        String tableName3 = "Routine_Items"; // 루틴 순서

        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                 + "ID VARCHAR(20) NOT NULL,"
                 + "PASSWORD VARCHAR(16) NOT NULL,"
                 + "NICKNAME VARCHAR(20) NOT NULL,"
                 + "PHONENUMBER VARCHAR(20) NOT NULL,"
                 + "PRIMARY KEY(ID)"
                 +")";
        String sql1 = "CREATE TABLE IF NOT EXISTS " + tableName1 + " ("
                 + "Solution_num INTEGER(100) NOT NULL,"
                 // [수정] 이름을 저장하기에 20자는 부족할 수 있으므로 100자로 늘림
                 + "Solution_name VARCHAR(100) NOT NULL," 
                 // [수정] 카테고리(손목/손가락)를 위해 10자 -> 20자로 늘림
                 + "Category VARCHAR(20) NOT NULL," 
                 + "Description TEXT," 
                 + "Video_URL VARCHAR(255)," 
                 + "PRIMARY KEY(Solution_num)"
                 +")";

        String sql2 = "CREATE TABLE IF NOT EXISTS " + tableName2 + " ("
                 + "Routine_ID INT NOT NULL AUTO_INCREMENT," 
                 + "ID VARCHAR(20) NOT NULL," 
                 + "Routine_Name VARCHAR(50) NOT NULL," 
                 + "PRIMARY KEY (Routine_ID)," 
                 + "FOREIGN KEY (ID) REFERENCES " + tableName + "(ID)"
                 +")";
        String sql3 = "CREATE TABLE IF NOT EXISTS " + tableName3 + " ("
                 + "Routine_ID INT NOT NULL,"
                 + "Solution_num INT NOT NULL,"
                 + "Sequence INT NOT NULL DEFAULT 1,"
                 + "PRIMARY KEY (Routine_ID, Solution_num)," 
                 + "FOREIGN KEY (Routine_ID) REFERENCES " + tableName2 + "(Routine_ID),"
                 + "FOREIGN KEY (Solution_num) REFERENCES " + tableName1 + "(Solution_num)"
                 + ")";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("데이터 베이스 연결 중...");

            try (Connection conn = DriverManager.getConnection(url, id, pw);
                 Statement stmt = conn.createStatement()) {
                    System.out.println("연결 성공");
                    stmt.executeUpdate(sql);
                    System.out.println("유저 테이블 " + tableName + " 생성 완료 (또는 이미 존재함)");
                    stmt.executeUpdate(sql1);
                    System.out.println("솔루션(재활 종류)테이블 " + tableName1 + " 생성 완료 (또는 이미 존재함)");
                    stmt.executeUpdate(sql2);
                    System.out.println("루틴(선택하여 만든 솔루션)테이블 " + tableName2 + " 생성 완료 (또는 이미 존재함)");
                    stmt.executeUpdate(sql3);
                    System.out.println("루틴순서(선택하여 만든 솔루션)테이블 " + tableName3 + " 생성 완료 (또는 이미 존재함)");
                 }
            } catch(ClassNotFoundException e) {
                System.out.println("JDBC 드라이버를 찾을 수 없습니다.");
                e.printStackTrace();
            } catch(SQLException se) {
                System.out.println("SQL 오류가 발생했습니다.");
                se.printStackTrace();
            } catch(Exception e) {
                System.out.println("기타 오류가 발생했습니다.");
                e.printStackTrace();
            }
    }
}

class insert {
    // [수정] 'Soultion' -> 'Solution' 오타 수정
    private boolean check_duplicate(Connection conn, int Solution_num) throws SQLException { //데이터가 이미 존재하는지 확인
        String checksql = "SELECT COUNT(*) FROM Solution WHERE Solution_num = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(checksql)) {
            pstmt.setInt(1, Solution_num);

            try (ResultSet rs = pstmt.executeQuery()) {
                // [수정] rs.next()를 두 번 호출하던 로직 오류 수정
                if (rs.next()) {
                    return rs.getInt(1) > 0; // 1개 이상이면 true 반환
                }
            }
        }
        return false;
    }

    public void insert_sql(Connection conn, int Solution_num, String category, String name, String desc) throws SQLException { //데이터 삽입
        String sql = "INSERT INTO Solution (Solution_num, Category, Solution_name, Description) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, Solution_num);
            pstmt.setString(2, category);
            pstmt.setString(3, name);
            pstmt.setString(4, desc);

            pstmt.executeUpdate();
        }
    }

    public void insert_value(){
        String url = "jdbc:mysql://localhost:3306/Accounts"; // DB 연결
        String id = "root";
        String pw = "ansxodnjs5467";

        String filePath = "C:\\Users\\gg935\\source\\Src_file\\team_project\\First\\Paintext.txt";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("JDBC 드라이버 로드 성공.");

            try (Connection conn = DriverManager.getConnection(url, id, pw);
                 Scanner scanner = new Scanner(new File(filePath), StandardCharsets.UTF_8)) {
                
                System.out.println("데이터베이스 연결 성공. 파일 읽는 중...(UTF-8 모드)...");

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine(); //한 줄씩 읽기
                    if (line.trim().isEmpty() || line.startsWith("#")) {
                        continue; // 빈 줄이나 주석은 건너뜀
                    }

                    String[] parts = line.split("\\|"); // | 로 데이터 분리
                    if (parts.length < 4) { // 데이터가 부족할 경우
                        System.out.println("[경고] 형식 오류(건너뜀): " + line);
                        continue;
                    }

                    int Solution_num = Integer.parseInt(parts[0].trim()); // 솔루션 번호
                    String category = parts[1].trim(); // 카테고리
                    String name = parts[2].trim(); // 이름
                    String desc = parts[3].trim().replace("[NL]", "\n"); // [NL] -> 줄바꿈

                    if (!check_duplicate(conn, Solution_num)) {
                        insert_sql(conn, Solution_num, category, name, desc);
                        System.out.println("삽입 완료: " + name);
                    } else {
                        System.out.println("중복 데이터(건너뜀): " + name);
                    }
                }
                System.out.println("파일 읽기 및 삽입 완료");

            } catch (FileNotFoundException e) {
                System.out.println(filePath + " 파일을 찾을 수 없습니다. (프로젝트 폴더에 파일이 있는지 확인하세요)");
                e.printStackTrace();
            } catch (SQLException se) {
                System.out.println("SQL 오류가 발생했습니다.");
                se.printStackTrace();
            } catch (NumberFormatException e) {
                System.out.println("파일에서 숫자를 읽는 중 오류 발생");
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("기타 오류가 발생했습니다.");
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버를 찾을 수 없습니다.");
            e.printStackTrace();
        } 
    }
}

public class DataBase {
    public static void main(String[] args) {
        create_database dbcreate = new create_database();
        dbcreate.DataBase();
        create_table tbcreate = new create_table();
        tbcreate.table();
        insert append = new insert();
        append.insert_value();
        System.out.println("프로그램 종료");
    }
}