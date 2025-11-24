import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Login extends JFrame  {
    public JTextField idField;
    public JPasswordField pwField;
    public JButton loginBtn,joinBtn;
    
    public Login() {
        setTitle("로그인 화면");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(350, 420);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ID 입력 필드
        idField = new JTextField();
        idField.setPreferredSize(new Dimension(250, 40));
        idField.setBorder(BorderFactory.createTitledBorder("ID"));

        // PW 입력 필드
        pwField = new JPasswordField();
        pwField.setPreferredSize(new Dimension(250, 40));
        pwField.setBorder(BorderFactory.createTitledBorder("Password"));

        // 버튼
        loginBtn = new JButton("로그인");
        joinBtn = new JButton("회원가입");
        
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isLogins islogins = new isLogins();
                String id = idField.getText().trim();
                char[] pwdChars =pwField.getPassword();
                String pw =new String(pwdChars);
                boolean isDup = islogins.isLogin(id,pw);
                
                if(isDup)
                {
                    System.out.println("로그인 성공하였습니다.");
                    SessionManager.login(id); 

                    insert inserts = new insert();
                    inserts.insert_value();

                    new Mainscreen();
                    new Mainscreen().setVisible(true); // 메인 화면 표시
                    dispose();
                    setVisible(false);
                }
                else
                {
                    System.out.println("로그인 실패하였습니다");
                }
            }
        });
        //회원 가입 버튼 클릭시
        joinBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Signup();
                setVisible(false);
            }
        });

        // 컴포넌트 추가
        gbc.gridx = 0; gbc.gridy = 0;
        add(idField, gbc);

        gbc.gridy = 1;
        add(pwField, gbc);

        gbc.gridy = 2;
        add(loginBtn, gbc);

        gbc.gridy = 3;
        add(joinBtn, gbc);

        setVisible(true);
    }

}