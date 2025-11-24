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
import java.util.Set;
import java.util.HashSet;


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
                 + "ID VARCHAR(20) BINARY NOT NULL,"
                 + "PASSWORD VARCHAR(16) BINARY NOT NULL,"
                 + "NICKNAME VARCHAR(20) BINARY NOT NULL,"
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
                 + "ID VARCHAR(20) BINARY NOT NULL," 
                //  + "Solution_num INT NOT NULL,"
                 + "Routine_Name VARCHAR(50) NOT NULL," 
                 + "PRIMARY KEY (Routine_ID),"
                //  + "FOREIGN KEY (Solution_num) REFERENCES " + tableName1 + "(Solution_num),"
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
    private static final String ROUTINES_TABLE = "Routines";
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
            pstmt.setString(1, ID);
            pstmt.setString(2, PASSWORD);
            pstmt.setString(3, NICKNAME);
            pstmt.setString(4, PHONENUMBER);

            pstmt.executeUpdate();
        }
    }
    // Routines 테이블에 루틴 정보 삽입 메서드
    public void insert_Routine_sql(Connection conn, int Routine_ID, String ID, String Routine_Name) throws SQLException {
        // insert.java의 insert_Routine_sql 메소드 (수정된 코드)
        String sql = "INSERT INTO Routines (Routine_ID, ID, Routine_Name) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, Routine_ID);
            pstmt.setString(2, ID);
            pstmt.setString(3, Routine_Name);

            pstmt.executeUpdate();
        }
    }
    // Routines 테이블에서 루틴 이름 수정 메서드
    public void update_RoutineName_sql(Connection conn, String ID, String Old_Routine_Name, String New_Routine_Name) throws SQLException { 
        String sql= "UPDATE " + ROUTINES_TABLE + " SET Routine_name = ? WHERE ID = ? AND Routine_Name = ?";
        // ID가 일치하고, 원래 이름이 일치하는 행을 찾아 이름으 새로운 이름으로 변경
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, New_Routine_Name); // 바꿀 새 이름
            pstmt.setString(2, ID); // 사용자 ID
            pstmt.setString(3, Old_Routine_Name); // 원래 이름
            //git test
            int result = pstmt.executeUpdate();
            // System.out.println("수정된 루틴 개수: " + result);
        }
    }
    // 솔루션 번호 수정 메서드
    public void update_RoutineSolution_sql(Connection conn, int Routine_ID, int Old_Solution_num, int New_Solution_num) throws SQLException {
        String sql = "UPDATE " + ROUTINES_TABLE + " SET Solution_num = ? WHERE Routine_ID = ? AND Solution_num = ?";
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
                 Scanner scanner = new Scanner(new File(filePath), "UTF-8")) {
                
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
    String sql ="SELECT COUNT(*) FROM " + tableName + " WHERE BINARY ID =? ";

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
    String sql ="SELECT * FROM " + tableName + " WHERE BINARY ID =? and BINARY PASSWORD =? ";

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
        // SolutionDAO (또는 RoutineDAO)에 추가
    public void saveUserSelections(String userID, Map<String, Set<String>> selections) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/Accounts";
        String id = "root";
        String pw = "ansxodnjs5467";
        
        // 트랜잭션 관리를 위해 AutoCommit을 끄고 시작합니다.
        try (Connection conn = DriverManager.getConnection(url, id, pw)) {
            conn.setAutoCommit(false);
            try {
                // 1. 기존 루틴 삭제 (새로 저장하기 위해 해당 유저의 모든 루틴 초기화)
                deleteUserRoutines(conn, userID); // 아래 deleteUserRoutines 메소드 필요

                for (Map.Entry<String, Set<String>> entry : selections.entrySet()) {
                    String partName = entry.getKey();
                    Set<String> exercises = entry.getValue();

                    if (exercises == null || exercises.isEmpty()) continue;

                    // 2. 부위(partName)별로 Routines 테이블에 삽입하고 Routine_ID를 얻어옴
                    int routineId = insertNewRoutine(conn, userID, partName); // 아래 insertNewRoutine 메소드 필요

                    int sequence = 1;
                    for (String exerciseName : exercises) {
                        // 3. 운동 이름으로 Solution_num을 조회
                        int solutionNum = getSolutionNumByName(conn, exerciseName); // 아래 getSolutionNumByName 메소드 필요

                        // 4. Routine_Items에 삽입
                        if (solutionNum != -1) {
                            insertRoutineItem(conn, routineId, solutionNum, sequence++); // 아래 insertRoutineItem 메소드 필요
                        }
                    }
                }
                conn.commit(); // 모든 작업 성공 시 커밋
                System.out.println("DB 저장 완료: 사용자 " + userID + "의 루틴이 저장되었습니다.");
            } catch (SQLException e) {
                conn.rollback(); // 오류 발생 시 롤백
                e.printStackTrace();
                throw e;
            } finally {
                conn.setAutoCommit(true); // 원래대로 복구
            }
        }
    }
    // SolutionDAO (또는 RoutineDAO)에 추가
    private void deleteUserRoutines(Connection conn, String userID) throws SQLException {
        // 1. Routine_Items에서 해당 유저의 루틴 아이템 먼저 삭제
        String sqlDeleteItems = "DELETE RI FROM Routine_Items RI JOIN Routines R ON RI.Routine_ID = R.Routine_ID WHERE R.ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteItems)) {
            pstmt.setString(1, userID);
            pstmt.executeUpdate();
        }
        // 2. Routines 테이블에서 해당 유저의 루틴 삭제
        String sqlDeleteRoutines = "DELETE FROM Routines WHERE ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteRoutines)) {
            pstmt.setString(1, userID);
            pstmt.executeUpdate();
        }
    }

    private int insertNewRoutine(Connection conn, String userID, String routineName) throws SQLException {
        String sql = "INSERT INTO Routines (ID, Routine_Name) VALUES (?, ?)";
        //int solutionNumDefault = 1; // Routines 테이블의 FK 제약조건을 맞추기 위한 임시값
        
        // AUTO_INCREMENT 키를 얻기 위해 Statement.RETURN_GENERATED_KEYS 사용
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, userID);
            pstmt.setString(2, routineName);
            // pstmt.setInt(3, solutionNumDefault);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // 새로 생성된 Routine_ID 반환
                } else {
                    throw new SQLException("Routine ID 생성 실패.");
                }
            }
        }
    }
    // SolutionDAO.java 클래스 내부에 추가

    /**
     * 💡 [추가] 사용자 ID를 기준으로 저장된 모든 운동 선택 데이터를 로드합니다.
     * @param userID 현재 로그인한 사용자의 ID
     * @return Map<부위 이름, Set<운동 이름>>
     */
    public Map<String, Set<String>> loadUserSelections(String userID) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        // JDBC 연결 정보 (필드 값 사용)
        String dbUrl = this.url; 
        String dbId = this.id;
        String dbPw = this.pw;
        
        // 결과 맵: Map<부위 이름, Set<운동 이름>>
        Map<String, Set<String>> userSelections = new HashMap<>(); 
        
        // SQL 쿼리: Routines(R)에서 사용자 ID를 기준으로 조인하여 운동 이름(S.Solution_name)과 
        // 부위 이름(R.Routine_Name)을 가져옴. 순서(Sequence)대로 정렬.
        String sql = "SELECT R.Routine_Name, S.Solution_name " +
                    "FROM Routines R " +
                    "JOIN Routine_Items RI ON R.Routine_ID = RI.Routine_ID " +
                    "JOIN Solution S ON RI.Solution_num = S.Solution_num " +
                    "WHERE R.ID = ? " +
                    "ORDER BY R.Routine_ID, RI.Sequence"; 

        try (Connection conn = DriverManager.getConnection(dbUrl, dbId, dbPw);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userID);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String partName = rs.getString("Routine_Name");
                    String exerciseName = rs.getString("Solution_name");
                    
                    // 해당 부위에 대한 Set이 없으면 새로 HashSet을 생성 (putIfAbsent)
                    userSelections.putIfAbsent(partName, new HashSet<>());
                    
                    // Set에 운동 이름 추가
                    userSelections.get(partName).add(exerciseName);
                }
            }
        }
        return userSelections;
    }
    private int getSolutionNumByName(Connection conn, String exerciseName) throws SQLException {
        String sql = "SELECT Solution_num FROM Solution WHERE Solution_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, exerciseName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Solution_num");
                }
            }
        }
        return -1; // 찾지 못한 경우
    }

    private void insertRoutineItem(Connection conn, int routineId, int solutionNum, int sequence) throws SQLException {
        String sql = "INSERT INTO Routine_Items (Routine_ID, Solution_num, Sequence) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, routineId);
            pstmt.setInt(2, solutionNum);
            pstmt.setInt(3, sequence);
            pstmt.executeUpdate();
        }
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

// public class DataBase {
//     public static void main(String[] args) {
//         //데이터베이스 생성
//         create_database dbcreate = new create_database();
//         dbcreate.DataBase();

//         //테이블 생성
//         create_table tablecreate = new create_table();
//         tablecreate.table();

//         //파일 읽어서 값 삽입
//         insert insertdata = new insert();
//         insertdata.insert_value();
//     }
// }