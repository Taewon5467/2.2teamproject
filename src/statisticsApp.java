import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*; 

public class statisticsApp {
    private JFrame frame;
    private JButton backButton;
    // 파일 분리를 안 했다면 DataBase.HistoryDAO 로 수정해서 쓰세요
    private HistoryDAO historyDAO = new HistoryDAO(); 
    private String userId;

    public statisticsApp() {
        frame = new JFrame("일주일 운동 통계");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setResizable(false);
        frame.setSize(500, 600); 
        frame.setLocationRelativeTo(null);

        // 1. ID 가져오기
        userId = SessionManager.getCurrentUserID();

        // 2. DB 데이터 가져오기
        Map<String, double[]> rawData = historyDAO.getWeeklyStatistics(userId);
        
        // ★ [핵심] 데이터가 없어도 그래프 축을 그리기 위해 빈 날짜를 채워넣음
        Map<String, double[]> fullData = fillMissingDays(rawData);

        // 3. UI 구성
        JLabel titleLabel = new JLabel("최근 7일간 통증(빨강) 및 만족도(파랑) 변화", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        frame.add(titleLabel, BorderLayout.NORTH);

        // 그래프 패널
        GraphPanel graphPanel = new GraphPanel(fullData);
        frame.add(graphPanel, BorderLayout.CENTER);

        // 하단 버튼
        backButton = new JButton("메인으로 돌아가기");
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.addActionListener(e -> {
            frame.dispose();
            new Mainscreen().setVisible(true); 
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        buttonPanel.add(backButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // ★ 빈 날짜 채우는 함수 (오늘 기준 최근 7일)
    private Map<String, double[]> fillMissingDays(Map<String, double[]> data) {
        Map<String, double[]> sortedMap = new TreeMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        Calendar cal = Calendar.getInstance();
        
        // 그래프가 2025년 데이터라도 오늘 날짜(2024) 기준으로 그려질 수 있으니 주의
        // (테스트를 위해 2025년도로 날짜를 강제하고 싶다면 아래 주석 해제)
        // cal.set(Calendar.YEAR, 2025); 

        cal.add(Calendar.DAY_OF_MONTH, -6); // 6일 전부터 시작

        for (int i = 0; i < 7; i++) {
            String dateKey = sdf.format(cal.getTime());
            
            if (data.containsKey(dateKey)) {
                sortedMap.put(dateKey, data.get(dateKey));
            } else {
                // 데이터 없으면 0.0 처리
                sortedMap.put(dateKey, new double[]{0.0, 0.0});
            }
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        // DB에서 가져온 날짜가 그래프 범위(7일) 밖에 있을 경우를 대비해 원본 데이터도 병합
        for(String key : data.keySet()){
            if(!sortedMap.containsKey(key)){
                sortedMap.put(key, data.get(key));
            }
        }
        return sortedMap;
    }

    class GraphPanel extends JPanel {
        private Map<String, double[]> data;
        private final int PADDING = 50;
        private final int MAX_SCORE = 10; 

        public GraphPanel(Map<String, double[]> data) {
            this.data = data;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // 축 그리기
            g2.drawLine(PADDING, height - PADDING, width - PADDING, height - PADDING);
            g2.drawLine(PADDING, height - PADDING, PADDING, PADDING);

            for (int i = 0; i <= MAX_SCORE; i++) {
                int y = height - PADDING - (i * (height - 2 * PADDING) / MAX_SCORE);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawLine(PADDING, y, width - PADDING, y); 
                g2.setColor(Color.BLACK);
                g2.drawString(String.valueOf(i), PADDING - 25, y + 5); 
            }

            // 데이터 그리기
            int xGap = (width - 2 * PADDING) / (Math.max(data.size(), 1) + 1);
            int currentX = PADDING + xGap;

            int prevX = -1;
            int prevPainY = -1;
            int prevRateY = -1;

            for (Map.Entry<String, double[]> entry : data.entrySet()) {
                String date = entry.getKey();
                double rating = entry.getValue()[0];
                double pain = entry.getValue()[1];

                int painY = height - PADDING - (int) (pain * (height - 2 * PADDING) / MAX_SCORE);
                int rateY = height - PADDING - (int) (rating * (height - 2 * PADDING) / MAX_SCORE);

                g2.setColor(Color.BLACK);
                g2.drawString(date, currentX - 15, height - PADDING + 20);

                // 통증 (빨강)
                g2.setColor(Color.RED);
                g2.fillOval(currentX - 4, painY - 4, 8, 8); 
                if (prevX != -1) {
                    g2.setStroke(new BasicStroke(2));
                    g2.drawLine(prevX, prevPainY, currentX, painY);
                }
                if (pain > 0) g2.drawString(String.format("%.1f", pain), currentX - 10, painY - 10);

                // 만족도 (파랑)
                g2.setColor(Color.BLUE);
                g2.fillOval(currentX - 4, rateY - 4, 8, 8); 
                if (prevX != -1) {
                    g2.setStroke(new BasicStroke(2)); // 점선 말고 실선
                    g2.drawLine(prevX, prevRateY, currentX, rateY);
                }
                if (rating > 0) g2.drawString(String.format("%.1f", rating), currentX - 10, rateY - 10);

                prevX = currentX;
                prevPainY = painY;
                prevRateY = rateY;
                currentX += xGap;
            }
        }
    }
}