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

    // [데이터 모델] 부위별 선택된 운동 목록을 저장
    // 예: {"팔꿈치": ["덤벨 컬", "해머 컬"], "어깨": ["숄더 프레스"]}
    private Map<String, Set<String>> selectedExercises;

    // [임시 DB] 부위별 추천 운동 전체 목록
    private final Map<String, String[]> exerciseDatabase = new HashMap<>();

    // 데이터 저장 파일명
    private static final String DATA_FILE = "exercise_data.ser";

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
        HumanBodyPanel bodyPanel = new HumanBodyPanel(this); // 'this' (MainApp)를 전달
        frame.add(bodyPanel, BorderLayout.CENTER);

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
