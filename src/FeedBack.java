import javax.swing.*;
import java.awt.*;

public class FeedBack extends JDialog {
    private int rating = 3; // 기본 평점은 0
    private int painLevel = 0; // 기본 통증 수준은 0
    private String memo = "";
    private boolean isSubmitted = false; // 저장 버튼 눌렀는지 확인 여부

    public FeedBack(JFrame parent, String exerciseName) {
super(parent, "운동 피드백 - " + exerciseName, true); // true = 모달창 (다른거 클릭 불가)
        setSize(400, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // 메인 패널
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. 평점 (Slider)
        JLabel ratingLabel = new JLabel("운동 만족도 (1~5점)");
        ratingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JSlider ratingSlider = new JSlider(1, 5, 3);
        ratingSlider.setMajorTickSpacing(1);
        ratingSlider.setPaintTicks(true);
        ratingSlider.setPaintLabels(true);
        ratingSlider.addChangeListener(e -> rating = ratingSlider.getValue());

        // 2. 통증 강도 (Slider)
        JLabel painLabel = new JLabel("통증 강도 (0:없음 ~ 10:극심함)");
        painLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JSlider painSlider = new JSlider(0, 10, 0);
        painSlider.setMajorTickSpacing(2);
        painSlider.setMinorTickSpacing(1);
        painSlider.setPaintTicks(true);
        painSlider.setPaintLabels(true);
        painSlider.addChangeListener(e -> painLevel = painSlider.getValue());

        // 3. 메모 (TextArea)
        JLabel memoLabel = new JLabel("메모 (특이사항)");
        memoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JTextArea memoArea = new JTextArea(5, 20);
        memoArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(memoArea);

        // 패널에 추가
        panel.add(ratingLabel);
        panel.add(ratingSlider);
        panel.add(Box.createVerticalStrut(20)); // 여백
        panel.add(painLabel);
        panel.add(painSlider);
        panel.add(Box.createVerticalStrut(20));
        panel.add(memoLabel);
        panel.add(scrollPane);

        add(panel, BorderLayout.CENTER);

        // 하단 버튼
        JButton confirmBtn = new JButton("저장 및 다음 운동");
        confirmBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        confirmBtn.setBackground(new Color(100, 200, 100)); // 초록색 느낌
        confirmBtn.addActionListener(e -> {
            this.rating = ratingSlider.getValue();
            this.painLevel = painSlider.getValue();
            this.memo = memoArea.getText();
            this.isSubmitted = true; // 제출됨 표시
            dispose(); // 창 닫기
        });

        add(confirmBtn, BorderLayout.SOUTH);
    }
    public boolean isSubmitted() {
        return isSubmitted;
    }
    public int getRating() {
        return rating;
    }
    public int getPainLevel() {
        return painLevel;
    }
    public String getMemo() {
        return memo;
    }
}