import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Mainscreen extends JFrame {
    public JMenuBar menuBar;
    public String[] categories = {"전체", "목", "어깨", "팔꿈치", "손목", "허리", "무릎", "발목"};
    public JButton createButton,deleteButton; 
    public JLabel progressLabel;
    private int totalGoals = 0; //전체 목표 개수
    private int doneGoals =0; // 완료한 목표 개수
    public JProgressBar progressBar;
    public Mainscreen()
    {
        setTitle("RehabSolution : 메인화면");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 560);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //위쪽 패널
        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(450,130));
        topPanel.setLayout(new GridBagLayout());
        
        // 패널간 선 표시
        topPanel.setBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY)
        );
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx =1.0;
        gbc.weighty =1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;  // ← 왼쪽 최상단 고정
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;

        gbc.insets = new Insets(8, 8, 4, 8);
        //JLabel1 //오늘의 목표 문자
        JLabel jl1 = new JLabel("오늘의 목표");
        topPanel.add(jl1,gbc);
        
        //
        gbc.gridy =1;
        //progressLabel 0/0
        progressLabel = new JLabel("0/0");
        progressLabel.setHorizontalAlignment(SwingConstants.RIGHT);
         topPanel.add(progressLabel,gbc);


        //프로그레스 바
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 2;
        progressBar = new JProgressBar(0,100);
        gbc.insets = new Insets(4, 8, 4, 8);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        
        topPanel.add(progressBar,gbc);
        //생성 버튼&삭제 버튼
        createButton = new JButton("생성");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                new CreateApp();
                setVisible(false);
            }
        });
        deleteButton =new JButton("삭제");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                new DeleteApp();
                setVisible(false);
            }
        });

        JPanel buttonPanel =new JPanel(new FlowLayout(FlowLayout.LEFT,45,0));
        buttonPanel.add(createButton);
        buttonPanel.add(deleteButton);
        
        gbc.gridy =3;
        gbc.gridx =0;    
        
        gbc.insets = new Insets(4, 10, 8, 2);
        topPanel.add(buttonPanel,gbc);

        
        
        //아래쪽 패널
        JPanel bottomPanel = new JPanel(new BorderLayout());
        

        menuBar = new JMenuBar();

        //반복문으로 JMENU 삽입
        for (String category : categories) {
            JMenu menu = new JMenu(category);
            menuBar.add(menu);


        //마우스 클릭시 그룹 홀더 변경
            menu.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    CardLayout cl = (CardLayout) (bottomPanel.getLayout());
                    cl.show(bottomPanel,category);
                }
            });
            
        bottomPanel.add(menuBar,BorderLayout.NORTH);





        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel,BorderLayout.CENTER);

        setVisible(true);

        }

    }
    private void updateProgress()
    {
        progressLabel.setText(doneGoals +"/"+totalGoals);

        int percent = 0;
        if(totalGoals >0)
        {
            percent =(int) ((doneGoals * 100.0)/totalGoals);
        }
        progressBar.setValue(percent);
    }
}


