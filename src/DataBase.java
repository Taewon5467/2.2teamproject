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
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

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
                 + "Solution_name VARCHAR(100) NOT NULL," 
                 + "Category VARCHAR(20) NOT NULL," 
                 + "Description TEXT," 
                 + "Video_URL VARCHAR(255)," 
                 + "PRIMARY KEY(Solution_num)"
                 +")";

        String sql2 = "CREATE TABLE IF NOT EXISTS " + tableName2 + " ("
                 + "Routine_ID INT NOT NULL AUTO_INCREMENT," 
                 + "ID VARCHAR(20) NOT NULL," 
                 + "Solution_num INT NOT NULL,"
                 + "Routine_Name VARCHAR(50) NOT NULL," 
                 + "PRIMARY KEY (Routine_ID),"
                 + "FOREIGN KEY (Solution_num) REFERENCES " + tableName1 + "(Solution_num),"
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
    private boolean check_duplicate(Connection conn, int Solution_num) throws SQLException { //데이터가 이미 존재하는지 확인
        String checksql = "SELECT COUNT(*) FROM Solution WHERE Solution_num = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(checksql)) {
            pstmt.setInt(1, Solution_num);

            try (ResultSet rs = pstmt.executeQuery()) {
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
    // Users 테이블에 사용자 정보 삽입 메서드
    public void insert_Users_sql(Connection conn, String ID, String PASSWORD, String NICKNAME, String PHONENUMBER) throws SQLException{
        String sql = "INSERT INTO Users (ID, PASSWORD, NICKNAME, PHONENUMBER) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, sql);
            pstmt.setString(2, PASSWORD);
            pstmt.setString(3, NICKNAME);
            pstmt.setString(4, PHONENUMBER);

            pstmt.executeUpdate();
        }
    }
    // Routines 테이블에 루틴 정보 삽입 메서드
    public void insert_Routine_sql(Connection conn, int Routine_ID, String ID, String Routine_Name) throws SQLException {
        String sql = "INSERT IN RTO Routines (Routine_ID, ID, Routine_Name) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, Routine_ID);
            pstmt.setString(2, ID);
            pstmt.setString(3, Routine_Name);

            pstmt.executeUpdate();
        }
    }
    // Routines 테이블에서 루틴 이름 수정 메서드
    public void update_RoutineName_sql(Connection conn, String ID, String Old_Routine_Name, String New_Routine_Name) throws SQLException { 
        String sql= "UPDATE " + tableName2 + " SET Routine_name = ? WHERE ID = ? AND Routine_Name = ?";
        // ID가 일치하고, 원래 이름이 일치하는 행을 찾아 이름으 새로운 이름으로 변경
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setSting(1, New_Routine_Name); // 바꿀 새 이름
            pstmt.setString(2, ID); // 사용자 ID
            pstmt.setString(3, Old_Routine_Name); // 원래 이름
            //git test
            int result = pstmt.executeUpdate();
            // System.out.println("수정된 루틴 개수: " + result);
        }
    }
    // 솔루션 번호 수정 메서드
    public void update_RoutineSolution_sql(Connection conn, int Routine_ID, int Old_Solution_num, int New_Solution_num) throws SQLException {
        String sql = "UPDATE " + tableName2 + " SET Solution_num = ? WHERE Routine_ID = ? AND Solution_num = ?";
        // 루틴 ID가 일치하고, 원래 솔루션 번호가 일치하는 행을 찾아 솔루션 번호를 새로운 번호로 변경
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, New_Solution_num); // 바꿀 새 솔루션 번호
            pstmt.setInt(2, Routine_ID); // 루틴 ID
            pstmt.setInt(3, Old_Solution_num); // 원래 솔루션 번호

            int result = pstmt.executeUpdate();
            // System.out.println("수정된 루틴 아이템 개수: " + result);
        }
    }

    public void insert_value(){
        String url = "jdbc:mysql://localhost:3306/Accounts"; // DB 연결
        String id = "root";
        String pw = "ansxodnjs5467";
        //파일 경로
        String filePath = "Paintext.txt";

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
//중복 확인
class Checker
{
public boolean isIdDuplicate(String id)
{
    String url = "jdbc:mysql://localhost:3306/Accounts";
    String id1 = "root";
    String pw = "ansxodnjs5467";
    String tableName ="Users";
    String sql ="SELECT COUNT(*) FROM " + tableName + " WHERE ID =? ";

     try (Connection conn = DriverManager.getConnection(url, id1, pw);
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
            System.out.println("데이터 베이스 연결 완료");

           pstmt.setString(1, id);

           try(ResultSet rs = pstmt.executeQuery())
           {
            if(rs.next())
            {
                int count =rs.getInt(1);
                return count > 0;
            }
           }
        }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return false; //에러 나면 중복 아님

           }
}
//로그인
class isLogins
{
public boolean isLogin(String id,String pw)
{
    String url = "jdbc:mysql://localhost:3306/Accounts";
    String id1 = "root";
    String pw1 = "ansxodnjs5467";
    String tableName ="Users";
    String sql ="SELECT * FROM " + tableName + " WHERE ID =? and PASSWORD =? ";

     try (Connection conn = DriverManager.getConnection(url, id1, pw1);
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
            System.out.println("데이터 베이스 연결 완료");

           pstmt.setString(1, id);
           pstmt.setString(2, pw);

           try(ResultSet rs = pstmt.executeQuery())
           {
            if(rs.next())
            {
                return true;
            }
            return false;
           }
        }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return false;

           }
           
}
class insertpw
{
    public void insert_database(MemberDTO dto)
    {
        String url = "jdbc:mysql://localhost:3306/Accounts";
        String id = "root";
        String pw = "ansxodnjs5467";
        String tableName = "Users";
        String sql = "INSERT INTO " + tableName + " (ID, PASSWORD, NICKNAME, PHONENUMBER) VALUES (?, ?, ?, ?)";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("데이터 베이스 연결 중...");

            try (Connection conn = DriverManager.getConnection(url, id, pw);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    System.out.println("데이터 베이스 연결 완료");

                    //? 자리에 MemberDTO 값 매핑
                    pstmt.setString(1, dto.gedid());
                    pstmt.setString(2, dto.gedpwd());
                    pstmt.setString(3, dto.gednickname());
                    pstmt.setString(4, dto.gedphonenum());
                    int row =pstmt.executeUpdate();
                    System.out.println(row + "행이 추가되었습니다");


                    
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
//솔루션 생성 
class SolutionDAO {
        private static String url = "jdbc:mysql://localhost:3306/Accounts";
        private static  String id = "root";
        private static  String pw = "ansxodnjs5467";
        //읽어온 데이터를 보관함
        private Map<String,String[]> exerciseDatabase = new HashMap<>();

        //CreatApp 에서 이걸로 꺼내 쓴다
        public Map<String,String[]> getExerciseDatabase()
        {
            return exerciseDatabase;
        }
    public void Solution()
    {
        String sql = "SELECT Category, Solution_name " +"FROM Solution " +"ORDER BY Category, Solution_num";
        
        //임시로 리스트에 모으고 나중에 String[]로 변경
        Map<String, List<String>> temp = new HashMap<>();
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("JDBC 드라이버 로드 성공");
        } catch(ClassNotFoundException e){
            System.out.println("JDBC 드라이버 로드 실패");
            e.printStackTrace();
        }

        try (Connection conn = DriverManager.getConnection(url, id, pw);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {
            
            System.out.println("DB 연결 및 조회 성공");

            while(rs.next())
            {
                String category =rs.getString("Category");
                String name =rs.getString("Solution_name");
                // 카테고리별로 List에 이름 추가
                temp.computeIfAbsent(category,k -> new ArrayList<>())
                .add(name);
                
                //확인용 출력
                System.out.println(category + "|" +name);
            }

            for (Map.Entry<String, List<String>> entry : temp.entrySet()) {
                exerciseDatabase.put(
                    entry.getKey(),
                    entry.getValue().toArray(new String[0])
                );
            }

            System.out.println("exerciseDatabase 채우기 완료: " + exerciseDatabase.keySet());

            } catch (SQLException e) {
            System.out.println("SQL 오류 발생");
            e.printStackTrace();
        }
    }

}