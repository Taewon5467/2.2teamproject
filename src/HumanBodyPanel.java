import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
/**
 * 인체 모형 조립 및 좌우대칭 호버, 클릭 이벤트 처리 패널
 */

public class HumanBodyPanel extends JPanel {


    private Map<String, java.util.List<BodyPartButton>> partButtonMap = new HashMap<>();

    // 호버 시 적용할 색상 필터
    private static final float[] HOVER_TINT = {2.0f, 2.0f, 0.5f, 1.0f};
    // 메인 앱 참조 (이벤트 처리를 위임하기 위함)
    public BodyPartClickHandler handler;

    public HumanBodyPanel(BodyPartClickHandler handler) {
        this.handler =handler;

        setLayout(null);
        setPreferredSize(new Dimension(510, 700));
        setOpaque(false);

        // 1. 부위 버튼 생성
       BodyPartButton neck      = createButton("목-removebg-preview.png", "목");
        BodyPartButton shoulder  = createButton("어깨-removebg-preview.png", "어깨");
        BodyPartButton waist     = createButton("허리-removebg-preview.png", "허리");
        BodyPartButton hip       = createButton("고관절-removebg-preview.png", "고관절");
        BodyPartButton knee      = createButton("무릎-removebg-preview.png", "무릎");
        BodyPartButton ankle     = createButton("발목-removebg-preview.png", "발목");

        BodyPartButton elbowLeft  = createButton("팔꿈치-1-removebg-preview.png", "팔꿈치");
        BodyPartButton elbowRight = createButton("팔꿈치-2-removebg-preview.png", "팔꿈치");
        BodyPartButton wristLeft  = createButton("손목-1-removebg-preview.png", "손목");
        BodyPartButton wristRight = createButton("손목-2-removebg-preview.png", "손목");

        // 2. [수정] 좌우대칭 호버 리스너 연결
        addSymmetricalHover(elbowLeft, elbowRight);
        addSymmetricalHover(wristLeft, wristRight);

        // 3. 좌표 배치 (사용자 제공)
        neck.setBounds(80, 50, neck.getPreferredSize().width, neck.getPreferredSize().height);             // 100-50
        shoulder.setBounds(80, 153, shoulder.getPreferredSize().width, shoulder.getPreferredSize().height); // 100-50
        waist.setBounds(187, 227, waist.getPreferredSize().width, waist.getPreferredSize().height);         // 207-50, y=253
        hip.setBounds(187, 303, hip.getPreferredSize().width, hip.getPreferredSize().height);              // 107-50
        knee.setBounds(80, 379, knee.getPreferredSize().width, knee.getPreferredSize().height);            // 100-50
        ankle.setBounds(80, 499, ankle.getPreferredSize().width, ankle.getPreferredSize().height);          // 100-50

        elbowLeft.setBounds(80, 227, elbowLeft.getPreferredSize().width, elbowLeft.getPreferredSize().height); // 100-50
        wristLeft.setBounds(80, 303, wristLeft.getPreferredSize().width, wristLeft.getPreferredSize().height); // 100-50

        elbowRight.setBounds(305, 227, elbowRight.getPreferredSize().width, elbowRight.getPreferredSize().height); // 325-50
        wristRight.setBounds(305, 303, wristRight.getPreferredSize().width, wristRight.getPreferredSize().height); // 325-50
        // 4. 패널에 버튼 추가
        add(neck); add(shoulder); add(waist); add(hip); add(knee); add(ankle);
        add(elbowLeft); add(elbowRight); add(wristLeft); add(wristRight);
    }

    // 버튼 생성 유틸리티
    private BodyPartButton createButton(String imagePath, String partName) {
        //클래스 패스 기준으로 리소스 찾기

        BodyPartButton button = new BodyPartButton(imagePath, HOVER_TINT);

        //클릭 했을때 어느 부위를 넘겨주기
        button.setActionCommand(partName);
        button.addActionListener(e -> handler.handleBodyPartClick(partName));

        partButtonMap
        .computeIfAbsent(partName, k -> new java.util.ArrayList<>())
        .add(button);
        return button;
    }

    /**
     * 두 버튼 간의 좌우대칭 호버를 위한 MouseListener를 추가합니다.
     */
    private void addSymmetricalHover(BodyPartButton btn1, BodyPartButton btn2) {
        MouseAdapter symmetricalListener = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // 상대방 버튼의 롤오버 상태를 강제로 활성화
                if (e.getSource() == btn1) {
                    btn2.getModel().setRollover(true);
                } else {
                    btn1.getModel().setRollover(true);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                // 상대방 버튼의 롤오버 상태를 강제로 비활성화
                if (e.getSource() == btn1) {
                    btn2.getModel().setRollover(false);
                } else {
                    btn1.getModel().setRollover(false);
                }
            }
        };
        btn1.addMouseListener(symmetricalListener);
        btn2.addMouseListener(symmetricalListener);
    }
public Map<String, java.util.List<BodyPartButton>> getPartButtonMap() {
        return partButtonMap;
    }
}