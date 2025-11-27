// SessionManager.java (새 파일)
public class SessionManager {
    // 현재 로그인한 사용자 ID를 저장하는 정적 필드
    public static String currentUserID = null;

    /**
     * 로그인 성공 시 사용자 ID를 설정합니다.
     */
    public static void login(String id) {
        currentUserID = id;
    }
    
    /**
     * 현재 로그인된 사용자 ID를 반환합니다.
     */
    public static String getCurrentUserID() {
        return currentUserID;
    }
}