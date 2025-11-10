import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


class create_database { //db생성
    public void DataBase(){
        String url = "jdbc:mysql://localhost:3306/"; // MySQL 서버 자체에 연결
        String id = "root";
        String pw = "ansxodnjs5467";
        String dbname = "Accounts";

        // 2. DB가 없으면 생성 (IF NOT EXISTS 추가)
        String sql = "CREATE DATABASE IF NOT EXISTS " + dbname;

        try {
            // 3. JDBC 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            System.out.println("데이터 베이스 연결 중...");

            try (Connection conn = DriverManager.getConnection(url, id, pw);
                Statement stmt = conn.createStatement()) {
                
                System.out.println("연결 성공");
                
                // 5. SQL 실행
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
                    + "Solution_num INTEGER(100) NOT NULL," //솔루션 번호(ex) 무릎 관련 1 ~ 5번, 척추 관련 6 ~ 10번
                    + "Solution_name VARCHAR(20) NOT NULL," //이름
                    + "Category VARCHAR(10) NOT NULL," // 부위 / 종류
                    + "Description TEXT," // 설명
                    + "Video_URL VARCHAR(255)," // 참고 URL
                    + "PRIMARY KEY(Solution_num)" // 중복X
                    +")";

        String sql2 = "CREATE TABLE IF NOT EXISTS " + tableName2 + " ("
                    + "Routine_ID INT NOT NULL AUTO_INCREMENT," // 루틴 고유 번호
                    + "ID VARCHAR(20) NOT NULL," // 만든 유저 (FK)
                    + "Routine_Name VARCHAR(50) NOT NULL," // 루틴 이름
                    + "PRIMARY KEY (Routine_ID)," // Routine_ID를 기본 키로
                    + "FOREIGN KEY (ID) REFERENCES " + tableName + "(ID)" // Users(ID) 참조
                    +")";
        String sql3 = "CREATE TABLE IF NOT EXISTS " + tableName3 + " ("
                    + "Routine_ID INT NOT NULL," // Routines(Routine_ID) 참조
                    + "Solution_num INT NOT NULL," // Solution(Solution_num) 참조
                    + "Sequence INT NOT NULL DEFAULT 1," // (선택사항) 루틴 내 순서
                    // 복합 기본 키: (Routine_ID, Solution_num)
                    // "하나의 루틴에 같은 솔루션이 중복 추가되는 것을 방지"
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

public class DataBase {
    public static void main(String[] args) {
        create_database dbcreate = new create_database();
        dbcreate.DataBase();
        create_table tbcreate = new create_table();
        tbcreate.table();
    }
}
