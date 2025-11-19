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
 * - '생성' / '삭제' 모드 관리
 * - 운동 데이터 로드/저장
 * - 부위 클릭 시 다이얼로그 처리
 */
public class DeleteApp implements BodyPartClickHandler {

    private JFrame frame;
    private JToggleButton deleteButton;
    public JButton backButton;

    // [데이터 모델] 부위별 선택된 운동 목록을 저장
    // 예: {"팔꿈치": ["덤벨 컬", "해머 컬"], "어깨": ["숄더 프레스"]}
    private Map<String, Set<String>> selectedExercises;

    // [임시 DB] 부위별 추천 운동 전체 목록
    private final Map<String, String[]> exerciseDatabase = new HashMap<>();

    // 데이터 저장 파일명
    private static final String DATA_FILE = "exercise_data.ser";

    public DeleteApp() {
        // 임시 운동 DB 초기화
        exerciseDatabase.put("목", new String[]{"목 스트레칭", "넥 플랭크"});
        exerciseDatabase.put("어깨", new String[]{"숄더 프레스", "래터럴 레이즈", "페이스 풀"});
        exerciseDatabase.put("팔꿈치", new String[]{"덤벨 컬", "해머 컬", "트라이셉스 익스텐션"});
        exerciseDatabase.put("손목", new String[]{"리스트 컬", "리스트 롤러"});
        exerciseDatabase.put("허리", new String[]{"데드리프트", "백 익스텐션", "플랭크"});
        exerciseDatabase.put("고관절", new String[]{"스쿼트", "힙 쓰러스트", "런지"});
        exerciseDatabase.put("무릎", new String[]{"레그 익스텐션", "레그 컬", "카프 레이즈"});
        exerciseDatabase.put("발목", new String[]{"발목 돌리기", "밴드 발목 운동"});

        // 데이터 로드
        loadData();

        // GUI 생성
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame("재활 운동 선택기");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);

        frame.setResizable(false);
        // 앱 종료 시 데이터 저장
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveData();
            }
        });

        // 1. [CENTER] 인체 모형 패널
        HumanBodyPanel bodyPanel = new HumanBodyPanel(this); // 'this' (DeleteApp)를 전달
        frame.add(bodyPanel, BorderLayout.CENTER);

        // 2. [SOUTH] 하단 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        deleteButton = new JToggleButton("삭제");
        deleteButton.setSelected(true);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                new Mainscreen();
                frame.setVisible(false);
            }
        });
        backButton = new JButton("뒤로");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                new Mainscreen();
                frame.setVisible(false);
            }
        });
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        // 3. 프레임 표시
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * [핵심 컨트롤러] HumanBodyPanel에서 이 메소드를 호출합니다.
     */
    public void handleBodyPartClick(String partName) {
        showExerciseDeletionDialog(partName);
    }


    /**
     * [삭제 모드] '선택된' 운동 목록만 보여주고, 클릭하여 삭제합니다.
     */
    private void showExerciseDeletionDialog(String partName) {
        Set<String> currentSelections = selectedExercises.getOrDefault(partName, new HashSet<>());
        if (currentSelections.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "선택된 운동이 없습니다.");
            return;
        }

        // Set을 Array로 변환 (JList에 넣기 위함)
        String[] selectedArray = currentSelections.toArray(new String[0]);

        // JList 생성
        JList<String> list = new JList<>(selectedArray);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        int result = JOptionPane.showConfirmDialog(frame, new JScrollPane(list),
                partName + " - 삭제할 운동 선택", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String exerciseToDelete = list.getSelectedValue();
            if (exerciseToDelete != null) {
                // 선택된 운동을 맵에서 삭제
                currentSelections.remove(exerciseToDelete);
                selectedExercises.put(partName, currentSelections);
                saveData(); // 변경 즉시 저장
                JOptionPane.showMessageDialog(frame, exerciseToDelete + " 삭제 완료.");
                System.out.println("삭제됨: " + selectedExercises);
            }
        }
    }

    // --- 데이터 저장 및 로드 ---

    /**
     * 앱 종료 시 Map 데이터를 파일에 저장 (Serialization)
     */
    @SuppressWarnings("unchecked")
    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(selectedExercises);
            System.out.println("데이터 저장 완료: " + DATA_FILE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "데이터 저장 중 오류 발생", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 앱 시작 시 파일에서 Map 데이터를 로드 (Deserialization)
     */
    @SuppressWarnings("unchecked")
    private void loadData() {
        File dataFile = new File(DATA_FILE);
        if (dataFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile))) {
                selectedExercises = (Map<String, Set<String>>) ois.readObject();
                System.out.println("데이터 로드 완료: " + DATA_FILE);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "데이터 로드 중 오류 발생. 새 데이터로 시작합니다.", "오류", JOptionPane.ERROR_MESSAGE);
                selectedExercises = new HashMap<>();
            }
        } else {
            System.out.println("저장된 데이터 없음. 새 데이터로 시작합니다.");
            selectedExercises = new HashMap<>();
        }
    }
}