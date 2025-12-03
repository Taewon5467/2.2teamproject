import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.*;

/**
 * 메인 애플리케이션 클래스
 * - UI 생성 및 레이아웃
 * - '생성' /  모드 관리
 * - 운동 데이터 로드/저장
 * - 부위 클릭 시 다이얼로그 처리
 */
public class CreateApp implements BodyPartClickHandler {
                                 

    private JFrame frame;
    private JToggleButton createButton;
    public JButton backButton;
    
    private HumanBodyPanel bodyPanel;

    // [데이터 모델] 부위별 선택된 운동 목록을 저장
    // 예: {"팔꿈치": ["덤벨 컬", "해머 컬"], "어깨": ["숄더 프레스"]}
    private Map<String, Set<String>> selectedExercises;

    // [임시 DB] 부위별 추천 운동 전체 목록
    private final Map<String, String[]> exerciseDatabase = new HashMap<>();

    // 데이터 저장 파일명
    private String getUserDataFile(){
        String userID = SessionManager.currentUserID;
        if (userID == null) {
            return "default_exercise_data.ser";
        }
        return "_exercise_data.ser";
    }

    public CreateApp() {
        //DB에서 운동 목록 가져오기
        SolutionDAO dao = new SolutionDAO();
        dao.Solution();
        exerciseDatabase.putAll(dao.getExerciseDatabase());

            System.out.println("CreateApp.exerciseDatabase = " + exerciseDatabase.keySet());
        // 데이터 로드
        loadData();

        // GUI 생성
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame("재활 운동 선택기");

        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        //인체 모형
        bodyPanel =new HumanBodyPanel(this);
        frame.add(bodyPanel, BorderLayout.CENTER);

        // 앱 종료 시 데이터 저장
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveData();
            }
        });


        // 2. [SOUTH] 하단 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        //생성 버튼
        createButton = new JToggleButton("생성");
        createButton.setSelected(true);
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                saveData(); //변경된 값 저장

                new Mainscreen();
                frame.setVisible(false);
            }
        });
        //뒤로 가기
        backButton = new JButton("뒤로");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                new Mainscreen();
                frame.setVisible(false);
            }
        });


        buttonPanel.add(createButton);
        buttonPanel.add(backButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // 3. 프레임 표시
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        refreshBodyPartHighlights();
    }

    /**
     * [핵심 컨트롤러] HumanBodyPanel에서 이 메소드를 호출합니다.
     */
    public void handleBodyPartClick(String partName) {
        showExerciseSelectionDialog(partName);
    }

    /**
     * [생성 모드] 전체 운동 목록에서 선택할 수 있는 체크박스 다이얼로그를 띄웁니다.
     */
    private void showExerciseSelectionDialog(String partName) {
        String[] allExercises = exerciseDatabase.getOrDefault(partName, new String[0]);
        if (allExercises.length == 0) {
            JOptionPane.showMessageDialog(frame, "추천 운동이 없습니다.");
            return;
        }

        // 현재 선택된 운동 세트 가져오기
        Set<String> currentSelections = selectedExercises.getOrDefault(partName, new HashSet<>());

        // 체크박스 패널 생성
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JCheckBox[] checkBoxes = new JCheckBox[allExercises.length];

        for (int i = 0; i < allExercises.length; i++) {
            String exercise = allExercises[i];
            checkBoxes[i] = new JCheckBox(exercise);
            // 이미 선택된 항목은 미리 체크
            if (currentSelections.contains(exercise)) {
                checkBoxes[i].setSelected(true);
                checkBoxes[i].setEnabled(false);

            }
            panel.add(checkBoxes[i]);
        }

        int result = JOptionPane.showConfirmDialog(frame, new JScrollPane(panel),
                partName + " - 운동 선택", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // 'OK' 누르면 선택 상태를 맵에 저장
            Set<String> newSelections = new HashSet<>();
            for (JCheckBox cb : checkBoxes) {                 
                if (cb.isSelected()) {
                    newSelections.add(cb.getText());
                }
            }
            selectedExercises.put(partName, newSelections);
            System.out.println("저장됨: " + selectedExercises);
        }
        refreshBodyPartHighlights();
    }

    // --- 데이터 저장 및 로드 ---

    /**
     * 앱 종료 시 Map 데이터를 파일에 저장 (Serialization)
     */
    @SuppressWarnings("unchecked")private void saveData() {
        // 1. 현재 로그인한 ID 가져오기
        String currentID = SessionManager.getCurrentUserID();

        // 2. 로그인이 안 된 상태면 저장 막기
        if (currentID == null || currentID.trim().isEmpty()) {
            System.out.println("오류: 로그인 정보가 없습니다.");
            return;
        }

        // 3. DB에 저장 시도
        try {
            SolutionDAO dao = new SolutionDAO();
            // 파일 저장 코드(ObjectOutputStream)는 다 지우고, 이 줄만 남겨야 합니다.
            dao.saveUserSelections(currentID, selectedExercises);
            
            System.out.println("DB 저장 성공! ID: " + currentID);
            System.out.println("저장된 데이터: " + selectedExercises);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "DB 저장 실패: " + e.getMessage());
        }
    }

    /**
     * 앱 시작 시 파일에서 Map 데이터를 로드 (Deserialization)
     */
    @SuppressWarnings("unchecked")
    private void loadData() {
        String currentID = SessionManager.getCurrentUserID(); // [필수] 로그인 ID 로드
        
        if (currentID == null) {
            // 로그인 정보가 없으면 빈 맵으로 시작
            selectedExercises = new HashMap<>();
            return;
        }
        
        try {
            SolutionDAO dao = new SolutionDAO();
            // [수정] DB에서 유저 ID를 기준으로 데이터를 로드
            Map<String, Set<String>> loadedData = dao.loadUserSelections(currentID);
            
            if (loadedData != null && !loadedData.isEmpty()) {
                selectedExercises = loadedData;
                System.out.println("DB 데이터 로드 완료: " + currentID);
            } else {
                selectedExercises = new HashMap<>();
                System.out.println("DB에 저장된 데이터 없음. 새 데이터로 시작합니다.");
            }
        } catch (Exception e) {
            // 로드 실패 시 초기화
            e.printStackTrace();
            selectedExercises = new HashMap<>();
            JOptionPane.showMessageDialog(null, "데이터 로드 중 오류 발생. 새 데이터로 시작합니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
        
    }
    private void refreshBodyPartHighlights() {
    if (bodyPanel == null) return;

    // HumanBodyPanel 이 내부에 partName -> BodyPartButton 맵을 갖고 있다고 가정
    Map<String, java.util.List<BodyPartButton>> partButtonMap = bodyPanel.getPartButtonMap();
    if (partButtonMap == null) return;

   for (Map.Entry<String, java.util.List<BodyPartButton>> entry : partButtonMap.entrySet()) {
        String partName = entry.getKey();          // "목", "어깨", ...
            java.util.List<BodyPartButton> buttons = entry.getValue();

        // 이 부위에 하나라도 선택된 운동이 있으면 "선택됨"
        Set<String> set = selectedExercises.get(partName);
        boolean hasSelection = (set != null && !set.isEmpty());

        for (BodyPartButton btn : buttons) {
            btn.setSelectedAppearance(hasSelection);
        }
    }
}
}