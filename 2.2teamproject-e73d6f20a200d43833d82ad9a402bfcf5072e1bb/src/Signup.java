import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class Signup extends JFrame {

    JTextField idField;
    JPasswordField pwField,pwCheckField;
    JTextField nickField;
    JTextField phoneField;
    JLabel logincheck,passwordcheck;

    private boolean isIdChecked =false;

    public Signup() {
        setTitle("회원가입");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(350, 420);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.0;

        Dimension idFieldSize = new Dimension(180, 30);
        Dimension fieldSize = new Dimension(220, 30);

        // ID 필드
        idField = new JTextField();
        idField.setPreferredSize(idFieldSize);
        addPlaceholder(idField, "아이디");

        JButton checkIdBtn = new JButton("중복확인");
        checkIdBtn.setPreferredSize(new Dimension(90, 30));

        JPanel idPanel = new JPanel(new BorderLayout(5, 0));
        idPanel.add(idField, BorderLayout.CENTER);
        idPanel.add(checkIdBtn, BorderLayout.EAST);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(idPanel, gbc);

        //로그인 체크 라벨
        gbc.gridwidth = 2;
        gbc.gridy++;
        logincheck = new JLabel(" ");
        logincheck.setHorizontalAlignment(SwingConstants.CENTER);
        add(logincheck,gbc);
        
        // 비밀번호 필드
        gbc.gridwidth = 2;
        gbc.gridy++;
        pwField = new JPasswordField();
        pwField.setPreferredSize(fieldSize);
        addPasswordPlaceholder(pwField, "비밀번호");
        add(pwField, gbc);

        gbc.gridy++;
        pwCheckField = new JPasswordField();
        pwCheckField.setPreferredSize(fieldSize);
        addPasswordPlaceholder(pwCheckField, "비밀번호 확인");
        add(pwCheckField, gbc);

        // 나머지 일반 필드들
        gbc.gridy++;
        nickField= new JTextField();
        nickField.setPreferredSize(fieldSize);
        addPlaceholder(nickField, "닉네임");
        add(nickField, gbc);

        gbc.gridy++;
        phoneField = new JTextField();
        phoneField.setPreferredSize(fieldSize);
        addPlaceholder(phoneField, "전화번호");
        add(phoneField, gbc);

        //비밀번호 중복 필드
        gbc.gridwidth = 2;
        gbc.gridy++;
        passwordcheck = new JLabel(" ");
        passwordcheck.setHorizontalAlignment(SwingConstants.CENTER);
        add(passwordcheck,gbc);

        // 버튼 하단 배치
        JPanel btnPanel = new JPanel();
        JButton backBtn = new JButton("뒤로가기");
        backBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Login();
                setVisible(false);
            }
        }); 
        backBtn.setPreferredSize(new Dimension(100, 30));
        //회원 가입 버튼
        JButton signupBtn = new JButton("회원가입");
        signupBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                String id = idField.getText().trim();

                if(!isIdChecked) {
                    JOptionPane.showMessageDialog(null, "아이디 중복확인을 해주세요.", "중복오류", JOptionPane.WARNING_MESSAGE);
                    idField.requestFocus();
                    return;
                }

                if(id.isEmpty() || id.equals("아이디")) {
                    JOptionPane.showMessageDialog(null, "아이디를 입력해주세요.", "입력오류", JOptionPane.WARNING_MESSAGE);
                    idField.requestFocus();
                    return;
                }

                Checker checker = new Checker();
                if(checker.isIdDuplicate(id)) {
                    JOptionPane.showMessageDialog(null, "이미 존재하는 아이디입니다.\n다른 아이디를 사용하세요.", "중복오류", JOptionPane.WARNING_MESSAGE);
                    idField.requestFocus();
                    return;
                }
                //비밀번호 중복확인
                char[] pwdChars =pwField.getPassword();
                String pw =new String(pwdChars);
                char[] pwcChars =pwCheckField.getPassword();
                String pwc =new String(pwcChars);

                if(pw.equals(pwc))
                {
                    insertpw sInsert =new insertpw();
                    sInsert.insert_database(getViewData());
                    System.out.println("성공적으로 회원가입 하였습니다");
                    JOptionPane.showMessageDialog(null, "회원가입이 완료되었습니다."); // 알림 추가
                    new Login();
                    setVisible(false);

                }
                else
                {
                    passwordcheck.setText("비밀번호가 같은지 확인해주세요");
                }
                
            }
        }); 
         
        signupBtn.setPreferredSize(new Dimension(100, 30));
        btnPanel.add(backBtn);
        btnPanel.add(signupBtn);


        gbc.gridy++;
        add(btnPanel, gbc);

        setVisible(true);
        //로그인 중복 버튼
        checkIdBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Checker checker =new Checker();
                String id =idField.getText().trim();
                boolean isDup = checker.isIdDuplicate(id);
                
                if(!isDup)
                {
                    logincheck.setText("사용 가능한 아이디입니다.");
                    isIdChecked =true;
                }
                else
                {
                    logincheck.setText("이미 사용중인 아이디입니다.");
                    isIdChecked = false;
                }
            }
        });
    }

    
    
    //insert 멤버 
    public MemberDTO getViewData()
    {
        //setter
        MemberDTO dto = new MemberDTO();
        char[] pwdChars =pwField.getPassword();
        String pwd =new String(pwdChars);
        String id = idField.getText();
        String nickname = nickField.getText();
        String phonenum =phoneField.getText();

          //getter
        dto.setid(id);
        dto.setpwd(pwd);
        dto.setnickname(nickname);
        dto.setphonenum(phonenum);

        //반환
        return dto;

    }

    // 일반 텍스트 필드용 placeholder
    private void addPlaceholder(JTextField field, String placeholder) {
        field.setForeground(Color.GRAY);
        field.setText(placeholder);

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
    }

    // 비밀번호 필드 placeholder
    private void addPasswordPlaceholder(JPasswordField field, String placeholder) {
        field.setEchoChar((char) 0);
        field.setForeground(Color.GRAY);
        field.setText(placeholder);

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                String current = new String(field.getPassword());
                if (current.equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    field.setEchoChar('\u2022');
                }
            }

            public void focusLost(FocusEvent e) {
                String current = new String(field.getPassword());
                if (current.isEmpty()) {
                    field.setEchoChar((char) 0);
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
    }


}