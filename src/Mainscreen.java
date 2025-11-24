import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Mainscreen extends JFrame {
    public JMenuBar menuBar;
    public String[] categories = {"전체", "목", "어깨", "팔꿈치", "손목", "허리", "무릎", "발목","고관절"};
    public JButton createButton, deleteButton;
    public JLabel progressLabel;
    private int totalGoals = 0; //전체 목표 개수
    private int doneGoals = 0; // 완료한 목표 개수
    
    // 필드 선언 위치에 있어야 합니다.
    private JPanel cardContainer;
    private CardLayout cardLayout; 
    public JProgressBar progressBar;

    private Map<String, Set<String>> userSelections = new HashMap<>();
    public Mainscreen()
    {
       
        setTitle("RehabSolution : 메인화면");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 560);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        String loginUserId = SessionManager.getCurrentUserID();
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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 45, 0));
        buttonPanel.add(createButton);
        buttonPanel.add(deleteButton);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(4, 10, 8, 2);
        topPanel.add(buttonPanel, gbc);

        // --- 2. 아래쪽 패널 (CENTER) 구성 및 CardLayout 설정 ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        // 1. [수정] menuBar를 생성합니다. (NullPointerException 해결)
        menuBar = new JMenuBar(); 

        // 2. CardLayout과 컨테이너를 설정합니다.
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        
        // --- JMenu 생성 및 리스너 추가 (반복문) ---
        for (String category : categories) {
            JMenu menu = new JMenu(category);
            menuBar.add(menu);

            //카테고리별 카드 생성
            JComponent card =createCategoryCard(category);
            cardContainer.add(card,category);
            // 💡 [필수] 카드 컨테이너에 부위별 빈 패널을 추가합니다.
            // [수정] JMenu 클릭 시 CardLayout을 전환하는 리스너만 남깁니다.
            menu.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    cardLayout.show(cardContainer, category);
                }
            });
        }
        
        // 3. [수정] bottomPanel에 menuBar와 cardContainer를 한 번만 추가합니다.
        bottomPanel.add(menuBar, BorderLayout.NORTH);
        bottomPanel.add(cardContainer, BorderLayout.CENTER);
        
        // --- 3. JFrame에 패널 추가 ---
        // [수정] 이 부분은 반복문 밖에서 한 번만 호출해야 합니다.
        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.CENTER);

        //DB에서 불러온 루틴 기반으로 전체 운동 계수 계산하여 ProgressBar에 반영
        calTotalGoalsFromSelections();

        // 4. 프레임 표시
        setVisible(true);
    }
    
    // ... updateProgress 메소드는 그대로 유지 ...
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

    // 🔹 1) "전체" 탭일 때: 모든 부위 + 운동 출력
    if ("전체".equals(category)) {
        if (userSelections.isEmpty()) {
            contentPanel.add(new JLabel("저장된 루틴이 없습니다."));
        } else {
            for (Map.Entry<String, Set<String>> entry : userSelections.entrySet()) {
                String partName = entry.getKey();
                Set<String> exercises = entry.getValue();

                // 부위 이름
                contentPanel.add(new JLabel("[" + partName + "]"));

                // 해당 부위 운동 목록
                if (exercises != null) {
                    for (String exName : exercises) {
                        contentPanel.add(new JLabel("  • " + exName));
                    }
                }

                // 부위 사이 간격
                contentPanel.add(Box.createVerticalStrut(8));  // ← createVerticalst 오타 수정
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
                contentPanel.add(new JLabel("• " + exName));
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
}