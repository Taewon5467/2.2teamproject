import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List; 

public class Mainscreen extends JFrame {
    public JMenuBar menuBar;
    public String[] categories = {"전체", "목", "어깨", "팔꿈치", "손목", "허리", "무릎", "발목","고관절"};
    public JButton createButton, deleteButton,startButton,statisticsButton;
    public JLabel progressLabel;
    private int totalGoals = 0; //전체 목표 개수
    private int doneGoals = 0; // 완료한 목표 개수
    private String loginUserId;
    
    private JPanel cardContainer;
    private CardLayout cardLayout; 
    public JProgressBar progressBar;

    private Map<String, Set<String>> userSelections = new HashMap<>();
    //끝난 운동은 매핑하여 초록색으로 변경
private Map<String, java.util.List<JLabel>> exerciseLabelMap = new HashMap<>();
    private update updater = new update();
    public Mainscreen()
    {
       
        setTitle("RehabSolution : 메인화면");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 560);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        loginUserId = SessionManager.getCurrentUserID();
        if(loginUserId == null || loginUserId.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "로그인 정보가 없습니다.\n다시 로그인해 주세요.");
            new Login();  // 로그인 화면 다시 띄우기
            dispose();
            return;
        }

        //DB에서 루틴 불러오기
        loadUserSelectionsDB(loginUserId);

        // --- 1. 위쪽 패널 (NORTH) 구성 ---
        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(450, 130));
        topPanel.setLayout(new GridBagLayout());
        topPanel.setBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY)
        );
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 4, 8);

        // 오늘의 목표 라벨
        gbc.gridy = 0;
        topPanel.add(new JLabel("오늘의 목표"), gbc);
        
        // 진행률 라벨 (0/0)
        gbc.gridy = 1;
        progressLabel = new JLabel("0/0");
        progressLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        topPanel.add(progressLabel, gbc);

        // 프로그레스 바
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 2;
        progressBar = new JProgressBar(0, 100);
        gbc.insets = new Insets(4, 8, 4, 8);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        topPanel.add(progressBar, gbc);
        
        // 생성 & 삭제 버튼 패널
        createButton = new JButton("생성");
        createButton.addActionListener(e -> {
            new CreateApp();
            setVisible(false);
        });
        
        deleteButton = new JButton("삭제");
        deleteButton.addActionListener(e -> {
            new DeleteApp();
            setVisible(false);
        });

        statisticsButton = new JButton("통계");
        statisticsButton.addActionListener(e -> {
            new statisticsApp();
            setVisible(false);
        });
        //시작 버튼 패널
        startButton = new JButton("시작");
        startButton.addActionListener(e -> onStartClicked());

    JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        buttonPanel.add(createButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(startButton);
    buttonPanel.add(statisticsButton);
        gbc.gridy = 3;
        gbc.insets = new Insets(4, 10, 8, 2);
        topPanel.add(buttonPanel, gbc);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        

        menuBar = new JMenuBar(); 

        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        

        for (String category : categories) {
            JMenu menu = new JMenu(category);
            menuBar.add(menu);


            JComponent card =createCategoryCard(category);
            cardContainer.add(card,category);

            menu.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    cardLayout.show(cardContainer, category);
                }
            });
        }
        
        bottomPanel.add(menuBar, BorderLayout.NORTH);
        bottomPanel.add(cardContainer, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.CENTER);

        //DB기반 전체운동 반영
        calTotalGoalsFromSelections();
        //DB저장된 진행상황 bar에 반영
        applySavedProgressFromDB();
        // 프레임 표시
        setVisible(true);
    }
    
    private void updateProgress() {
        progressLabel.setText(doneGoals + "/" + totalGoals);

        int percent = 0;
        if (totalGoals > 0) {
            percent = (int) ((doneGoals * 100.0) / totalGoals);
        }
        progressBar.setValue(percent);
    }
    private JComponent createCategoryCard(String category) {
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // 전체 탭: 모든 부위 
    if ("전체".equals(category)) {
        if (userSelections.isEmpty()) {
            contentPanel.add(new JLabel("저장된 루틴이 없습니다."));
        } else {
            for (Map.Entry<String, Set<String>> entry : userSelections.entrySet()) {
                String partName = entry.getKey();
                Set<String> exercises = entry.getValue();

                // 부위 이름
                JLabel partLabel =(new JLabel("[" + partName + "]"));
                contentPanel.add(partLabel);

                // 해당 부위 운동 목록
                if (exercises != null) {
                    for (String exName : exercises) {
                        JLabel exLabel =(new JLabel("  • " + exName));
                        contentPanel.add(exLabel);
                        //색 바꾸기 위한 map 저장
                        String key = normalizeName(exName);
                        exerciseLabelMap.computeIfAbsent(key, k -> new java.util.ArrayList<>()).add(exLabel);                    }
                }

                // 부위 사이 간격
                contentPanel.add(Box.createVerticalStrut(8)); 
            }
        }
    }
    else {
        Set<String> exercises =userSelections.get(category);

        if(exercises == null || exercises.isEmpty())
        {
            contentPanel.add(new JLabel("저장된 루틴이 없습니다."));
        }else 
        {
            for (String exName :exercises)
            {
                JLabel exLabel =(new JLabel("• " + exName));
                contentPanel.add(exLabel);

                String key = normalizeName(exName);
                exerciseLabelMap.computeIfAbsent(key, k -> new java.util.ArrayList<>()).add(exLabel);    
            }
        }
    }

        JScrollPane scrollPane =new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }
    private void loadUserSelectionsDB(String loginUserId) {
        try {
            SolutionDAO dao = new SolutionDAO();
            userSelections = dao.loadUserSelections(loginUserId);
            if (userSelections == null) {
                userSelections = new HashMap<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            userSelections = new HashMap<>();
        }
    }

    
    private void calTotalGoalsFromSelections()
    {
        totalGoals = 0;

        if (userSelections != null)
        {
            for(Set<String> exercises :userSelections.values())
            {
                if (exercises != null)
                {
                    totalGoals +=exercises.size();
                }
            }
        }
        updateProgress();
    }
    //시작 버튼 동작
    private void onStartClicked() {
    try {
        //DB에서 운동 순서 가져옴
        SolutionDAO dao = new SolutionDAO();
        List<SolutionDAO.ExerciseInfo> exercises =
                dao.loadUserExercisesInOrder(loginUserId);  

        //운동 없을때 경고 메세지
        if (exercises == null || exercises.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "저장된 루틴이 없습니다.\n먼저 루틴을 생성해 주세요.",
                    "알림",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        //운동 시작 화면으로 이동
        this.setVisible(false);
        new ExercisePlayerFrame(this, exercises, loginUserId);
        setRoutineButtonsEnabled(false);


    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(
                this,
                "루틴을 불러오는 중 오류가 발생했습니다.",
                "오류",
                JOptionPane.ERROR_MESSAGE
        );
    }
    }
    //진행도 반영
    public void updateProgressFromPlayer(int currentExerciseIndex)
    {

        doneGoals =Math.min(currentExerciseIndex,totalGoals);
        updateProgress();
    }
    //전부다 운동 완료 했을때 
    public void markAllDone() {
    doneGoals = totalGoals;
    updateProgress();

    setRoutineButtonsEnabled(true);
}
//DB에 저장된 운동 progress바에 반영
    private void applySavedProgressFromDB()
{
    try {
        if (loginUserId == null || loginUserId.isEmpty()) {
            return;
        }

        update.Progress saved = updater.loadExerciseProgress(loginUserId);
        if (saved != null) {
            int idx = saved.getExerciseIndex(); 

            doneGoals = Math.min(idx, totalGoals);

            updateProgress();
            //초록색 칠한 것도 가져온다
            SolutionDAO dao = new SolutionDAO();
            java.util.List<SolutionDAO.ExerciseInfo> exercises =
            dao.loadUserExercisesInOrder(loginUserId);

            for (int i = 0; i < doneGoals && i < exercises.size(); i++) {
            String exName = exercises.get(i).getExerciseName();
            markExerciseDone(exName);
            }
            boolean inProgress =(doneGoals > 0 && doneGoals<totalGoals);
            setRoutineButtonsEnabled(!inProgress);
            }else{
                setRoutineButtonsEnabled(true);
            }
    } catch (Exception e) {
        e.printStackTrace();


    }
}
//특정 운동 완료 처리
    public void markExerciseDone(String exerciseName)
    {
    String key = normalizeName(exerciseName);
    java.util.List<JLabel> labels = exerciseLabelMap.get(key);

        for(JLabel label :labels)
        {
            label.setForeground(Color.GREEN);
        }
    }
    //공백 제거
    private String normalizeName(String name) {
    if (name == null) return "";
    return name.replace(" ", "").trim();
}
public void resetAllProgressColors() {
    doneGoals = 0;
    updateProgress();  


    for (java.util.List<JLabel> labels : exerciseLabelMap.values()) {
        for (JLabel label : labels) {
            label.setForeground(Color.BLACK);
        }
    }
}
private void setRoutineButtonsEnabled(boolean enabled)
{
    if (createButton != null) createButton.setEnabled(enabled);
    if (deleteButton != null) deleteButton.setEnabled(enabled);
}

}
