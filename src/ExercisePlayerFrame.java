import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class ExercisePlayerFrame extends JFrame{
    
    public  JFrame Mainscreen; // 메인화면
    public  List<SolutionDAO.ExerciseInfo> exercises;
    public int currentIndex = 0; //현재 인덱스
    private int currentRepeat = 1; //현재 몇 번째 반복
    
    public String userId;
    private update updater = new update();
    //UI 컴포넌트
    public JLabel stepLabel; // 운동단계 0/5
    public JLabel titeLabel; // 운동 이름
    public JTextArea descArea; //설명
    public JLabel setsLabel; //세트
    public JLabel repLabel; //반복
    public JLabel timerLabel; //남은 시간
    public JProgressBar timerBar; //시간 진행 바
    public JButton startBtn,backBtn,nextBtn; //시작,반복 다시 시작,다음 
    public JLabel imageLabel;
    //타이머 관련
    public javax.swing.Timer timer;
    public int remainingSec;
    private static final int DEFAULT_TIME =5;
        private static final int DEFAULT_REPEAT_COUNT = 3;
        
    //로직 생성자 
    public ExercisePlayerFrame(JFrame parent,List<SolutionDAO.ExerciseInfo> exercises,String userId) {

        this.Mainscreen = parent;
        this.exercises = exercises;
        this.userId =userId;

    // 진행 상황 불러오기
    update.Progress saved = null;
    if (userId != null) {
        saved = updater.loadExerciseProgress(userId);
    }

    if (saved != null) {
        //저장된거 불러옴
        this.currentIndex  = saved.getExerciseIndex();
        this.currentRepeat = saved.getRepeatCount();
    } else {
        // 없을 경우
        this.currentIndex  = 0;
        this.currentRepeat = 1;
    }
        setTitle("운동 진행");
        setSize(420,720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUi();
        showExercise(currentIndex,false);
        
        setVisible(true);
    }
    // 현재 운동에서 "다음 운동"
private void goToNextExercise() {
    currentIndex++;

    if (currentIndex >= exercises.size()) {
        JOptionPane.showMessageDialog(
                this,
                "오늘의 운동이 모두 끝났습니다!",
                "완료",
                JOptionPane.INFORMATION_MESSAGE
        );
        //모든 운동 완료시, 프로그레스 바 반영
        if (Mainscreen != null && Mainscreen instanceof Mainscreen) {
            ((Mainscreen) Mainscreen).markAllDone();
        }
        
        //모든 운동 완료 시, 진행도 초기화

        if (userId != null) {
        updater.resetExerciseProgress(userId);
}
        dispose(); // 이 창 닫기

        if (Mainscreen != null) {
            Mainscreen.setVisible(true); // 아예 다 끝나면 메인화면으로 
        }
    } else {
        // 다음 운동이동
        showExercise(currentIndex,true);

        //다음 버튼 잠그기
        nextBtn.setEnabled(false);
    }
}
    private void initUi()
    {
        JPanel exercisPanel = new JPanel(new BorderLayout());
        add(exercisPanel);

        //상단 운동단계
        stepLabel = new JLabel(" ",SwingConstants.LEFT);
        stepLabel.setFont(stepLabel.getFont().deriveFont(Font.BOLD, 16f));
        stepLabel.setBorder(BorderFactory.createEmptyBorder(10,16,10,16));
        exercisPanel.add(stepLabel,BorderLayout.NORTH);

        //중앙 이미지 및 설명 
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center,BoxLayout.Y_AXIS));
        
        //이미지 자리
    imageLabel = new JLabel("이미지 영역", SwingConstants.CENTER); 
    imageLabel.setPreferredSize(new Dimension(360,220));
    imageLabel.setOpaque(true); 
    imageLabel.setBackground(new Color(230,230,230));
    imageLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT); //부모 컨테이너 수평 중앙 배치

    center.add(Box.createVerticalStrut(8)); 
    center.add(imageLabel);
    center.add(Box.createVerticalStrut(12)); 

        //운동이름
        titeLabel = new JLabel(" ",SwingConstants.LEFT);
        titeLabel.setFont(titeLabel.getFont().deriveFont(Font.BOLD ,18f));
        titeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(titeLabel);
        center.add(Box.createVerticalStrut(6)); //아래 여백 6

        //설명
        descArea = new JTextArea();
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setFont(descArea.getFont().deriveFont(Font.BOLD ,13f));
        descArea.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(descArea);
        center.add(Box.createVerticalStrut(12));

        exercisPanel.add(center,BorderLayout.CENTER);

        //하단:세트/반복 +타이머+ 버튼
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBorder(BorderFactory.createEmptyBorder(10,16,16,16));
        bottom.setBackground(Color.WHITE);

        //세트 /반복
        
        JPanel setPanel =new JPanel(new GridLayout(1,2,10,0));
        setPanel.setOpaque(false);
        
        setsLabel = new JLabel("3세트", SwingConstants.CENTER); 
        repLabel = new JLabel(DEFAULT_REPEAT_COUNT+"회", SwingConstants.CENTER);
        setsLabel.setBorder((BorderFactory.createTitledBorder("세트")));
        repLabel.setBorder((BorderFactory.createTitledBorder("반복")));

        setPanel.add(setsLabel);
        setPanel.add(repLabel);
        
        bottom.add(setPanel);
        bottom.add(Box.createVerticalStrut(12));

        //프로그레스바+남은시간
        timerBar = new JProgressBar(0,DEFAULT_TIME);
        timerBar.setValue(DEFAULT_TIME);
        timerBar.setStringPainted(true);

        //텍스트 가운데 정렬
        timerBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        timerBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, timerBar.getPreferredSize().height));


        timerLabel = new JLabel("",SwingConstants.CENTER);
        timerLabel.setFont(timerLabel.getFont().deriveFont(Font.BOLD,18f));
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        bottom.add(timerBar);
        bottom.add(Box.createVerticalStrut(4));
        bottom.add(timerLabel);
        bottom.add(Box.createVerticalStrut(8));

        // 반복 라벨
        repLabel = new JLabel("", SwingConstants.CENTER);
        repLabel.setFont(repLabel.getFont().deriveFont(14f));
        bottom.add(repLabel);
        bottom.add(Box.createVerticalStrut(8));
        
        //버튼 시작, 반복,다음
        JPanel btnPanel = new JPanel(new GridLayout(1,3,10,0));
        btnPanel.setOpaque(false);

        backBtn = new JButton("뒤로 가기");

        startBtn =new JButton("시작");
        nextBtn =new JButton("다음");
        nextBtn.setEnabled(false);

        btnPanel.add(backBtn);
        btnPanel.add(startBtn);
        btnPanel.add(nextBtn);
        bottom.add(btnPanel);

        exercisPanel.add(bottom,BorderLayout.SOUTH);

        // 시작 버튼 → 타이머 시작
    startBtn.addActionListener(e -> {
        startTimer();
    });

    backBtn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e)
        { //타이머 돌고 있으면 멈추기
            if (timer != null && timer.isRunning()) 
                {
                timer.stop();
            }
            //현재 운동 안덱스 저장
            if (userId != null) {
            updater.saveExerciseProgress(userId, currentIndex, currentRepeat);
}

            //메인화면에 지금 까지 한 운동 개수 반영
            if (Mainscreen != null && Mainscreen instanceof Mainscreen) {
            ((Mainscreen) Mainscreen).updateProgressFromPlayer(currentIndex);
            }

            dispose();
            if(Mainscreen != null)
            {
                Mainscreen.setVisible(true);
            }
        }
    });

    nextBtn.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {

        // 다음 운동으로 이동
        goToNextExercise();
    }
});
    }

    private void showExercise(int index)
    {
        showExercise(index,true);
    }

    //현재 운동 정보 화면에 표시
    private void showExercise(int index,boolean resetRepeat)
    {
        if (index < 0 || index >= exercises.size()) return;

        SolutionDAO.ExerciseInfo ex =exercises.get(index);
        
        //상단에 몇 번째 운동인지 표시
        int step =index +1;
        int total =exercises.size();
        stepLabel.setText("운동 " + step + " / " + total);
        

        setExerciseImage(ex.getImagePath());


        setsLabel.setText("반복 " + currentRepeat + " / " + DEFAULT_REPEAT_COUNT);
        titeLabel.setText(ex.getExerciseName());

        String desc =ex.getDescription();
        if (desc == null || desc.isEmpty())
        {
            desc ="설명이 등록되어 있지 않습니다.";
        }
        descArea.setText(desc);
        descArea.setCaretPosition(0);

        //이미지 표시
        setExerciseImage(ex.getImagePath());

        //반복 초기화
        if(resetRepeat)
        {
            currentRepeat =1;
        }
        updateRepeatLabel();

        //마지막 세트 완료후 나갔다 들어오면 다음 버튼 활성화
        if (currentRepeat >= DEFAULT_REPEAT_COUNT) {
            nextBtn.setEnabled(true);
        } else {

            nextBtn.setEnabled(false);
        }
        //타이머 초기화
        remainingSec =DEFAULT_TIME;
        timerBar.setMaximum(DEFAULT_TIME);
        timerBar.setValue(DEFAULT_TIME);
        updateTimerLabel();
    }

    //반복 업데이트
    private void updateRepeatLabel() {
    setsLabel.setText("세트 " + currentRepeat + " / " + DEFAULT_REPEAT_COUNT);
}
    //이미지 
   private void setExerciseImage(String imgPath) {

    System.out.println(">> setExerciseImage imgPath = [" + imgPath + "]");
    if (imgPath == null || imgPath.isEmpty()) {
        imageLabel.setIcon(null);
        imageLabel.setText("이미지가 등록되어 있지 않습니다.");
        return;
    }

  
    try {
        // 1) 먼저 imgPath 그대로 File 객체로 만들기
        File file = new File(imgPath);

        // 2) 만약 절대 경로가 아니라면(= img\고개 기울이기.jpg, 고개 기울이기.jpg 같은 경우)
        //    실행 폴더 기준 ./img/파일명 으로 다시 잡아준다
        if (!file.isAbsolute()) {
            String fileName = file.getName();     // img\고개 기울이기.jpg → 고개 기울이기.jpg
            file = new File("img", fileName);     // ./img/고개 기울이기.jpg
        }

        System.out.println("★ 실제 사용할 이미지 경로 = " + file.getAbsolutePath()
                           + " / exists=" + file.exists());

        if (!file.exists()) {
            imageLabel.setIcon(null);
            imageLabel.setText("이미지 파일을 찾을 수 없습니다.\n" + file.getAbsolutePath());
            return;
        }

        ImageIcon icon = new ImageIcon(imgPath);

        //크기 라벨 맞게 조정
        int w = imageLabel.getPreferredSize().width;
        int h = imageLabel.getPreferredSize().height;
        if (w <= 0) w = 360;
        if (h <= 0) h = 220;

        Image img = icon.getImage();
        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);

        imageLabel.setText(null);
        imageLabel.setIcon(new ImageIcon(scaled));
    } catch (Exception e) {
        e.printStackTrace();
        imageLabel.setIcon(null);
        imageLabel.setText("이미지를 불러오는 중 오류가 발생했습니다.");
    }
}
    
    //타이머 기본 셋팅
    private void updateTimerLabel()
    {
        int m = remainingSec /60;
        int s = remainingSec %60;

        timerLabel.setText(String.format("%02d:%02d",m,s));
        timerBar.setString(remainingSec+"초");
    }
    
    
      private void startTimer() {
        if (timer != null && timer.isRunning()) return; // 이미 동작 중

        timer = new javax.swing.Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                remainingSec--;
                if (remainingSec <= 0) 
                    {
                    remainingSec = 0;
                    updateTimerLabel();
                    timerBar.setValue(0);
                    timer.stop();
                        //반복 세트 처리
                    if(currentRepeat < DEFAULT_REPEAT_COUNT)
                    {
                        //반복 남앙 있을 경우 세트 하나 더 
                        currentRepeat++;
                        updateRepeatLabel();
                    
                                            // 다음 세트 타이머 다시 세팅 후 자동 시작
                        remainingSec = DEFAULT_TIME;
                        timerBar.setMaximum(DEFAULT_TIME);
                        timerBar.setValue(DEFAULT_TIME);
                        updateTimerLabel();

                        JOptionPane.showMessageDialog(
                            ExercisePlayerFrame.this,
                            "이번 세트가 끝났습니다.\n'시작' 버튼을 눌러 다음 세트를 진행하세요",
                            "알림",
                            JOptionPane.INFORMATION_MESSAGE
                    ); 
                    }else{

                        nextBtn.setEnabled(true);
                        JOptionPane.showMessageDialog(
                            ExercisePlayerFrame.this,
                            "이 운동의 반복이 모두 끝났습니다.\n'다음' 버튼을 눌러 다음 운동으로 넘어가세요.",
                            "완료",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                }
            }
            else{
                //아직 시간 남았을 때
                timerBar.setValue(remainingSec);
                updateTimerLabel();
            }
            }
        });
        timer.start();
    }

}
