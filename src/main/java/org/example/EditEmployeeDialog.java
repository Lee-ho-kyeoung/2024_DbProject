package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class EditEmployeeDialog extends JDialog {
    private JTextField fnameField;
    private JTextField minitField;
    private JTextField lnameField;
    private JTextField ssnField;
    private JTextField bdateField;
    private JTextField addressField;
    private JComboBox<String> sexComboBox;
    private JTextField salaryField;
    private JTextField superSsnField;
    private JTextField dnoField;
    private boolean confirmed = false;
    private String originalSsn;

    public EditEmployeeDialog(Frame owner, Employee employee) {
        super(owner, "직원 정보 수정", true);
        this.originalSsn = employee.getSsn();
        initComponents(employee);
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents(Employee employee) {
        setLayout(new BorderLayout());

        // 입력 필드 패널
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 필드 초기화
        fnameField = new JTextField(15);
        minitField = new JTextField(2);
        lnameField = new JTextField(15);
        ssnField = new JTextField(15);
        bdateField = new JTextField(15);
        addressField = new JTextField(20);
        sexComboBox = new JComboBox<>(new String[]{"M", "F"});
        salaryField = new JTextField(10);
        superSsnField = new JTextField(15);
        dnoField = new JTextField(5);

        // 값 설정
        fnameField.setText(employee.getFname());
        minitField.setText(employee.getMinit());
        lnameField.setText(employee.getLname());
        ssnField.setText(employee.getSsn());
        ssnField.setEditable(false);
        bdateField.setText(employee.getBirthDate());
        addressField.setText(employee.getAddress());
        sexComboBox.setSelectedItem(employee.getSex());
        salaryField.setText(String.valueOf(employee.getSalary()));
        superSsnField.setText(employee.getSupervisor());
        dnoField.setText(String.valueOf(employee.getDno()));

        // Last Modified 라벨 추가
        JLabel lastModifiedLabel = new JLabel("Last Modified: " + employee.getLastModified());
        lastModifiedLabel.setForeground(Color.GRAY);

        // 컴포넌트 배치
        int gridy = 0;

        gbc.gridx = 0; gbc.gridy = gridy++;
        inputPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(fnameField, gbc);

        gbc.gridx = 0; gbc.gridy = gridy++;
        inputPanel.add(new JLabel("Middle Init.:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(minitField, gbc);

        gbc.gridx = 0; gbc.gridy = gridy++;
        inputPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(lnameField, gbc);

        gbc.gridx = 0; gbc.gridy = gridy++;
        inputPanel.add(new JLabel("SSN:* "), gbc);
        gbc.gridx = 1;
        inputPanel.add(ssnField, gbc);

        gbc.gridx = 0; gbc.gridy = gridy++;
        inputPanel.add(new JLabel("Birthdate:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(bdateField, gbc);

        gbc.gridx = 0; gbc.gridy = gridy++;
        inputPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(addressField, gbc);

        gbc.gridx = 0; gbc.gridy = gridy++;
        inputPanel.add(new JLabel("Sex:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(sexComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = gridy++;
        inputPanel.add(new JLabel("Salary:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(salaryField, gbc);

        gbc.gridx = 0; gbc.gridy = gridy++;
        inputPanel.add(new JLabel("Super_ssn:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(superSsnField, gbc);

        gbc.gridx = 0; gbc.gridy = gridy++;
        inputPanel.add(new JLabel("Dno:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(dnoField, gbc);

        // Last Modified 라벨 추가
        gbc.gridx = 0; gbc.gridy = gridy++;
        gbc.gridwidth = 2;
        inputPanel.add(lastModifiedLabel, gbc);
        gbc.gridwidth = 1;

        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        JButton confirmButton = new JButton("수정");
        JButton cancelButton = new JButton("취소");

        confirmButton.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                setVisible(false);
            }
        });

        cancelButton.addActionListener(e -> {
            confirmed = false;
            setVisible(false);
        });

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        // 패널 추가
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private boolean validateInput() {
        // 급여 유효성 검사
        if (!salaryField.getText().trim().isEmpty()) {
            try {
                Double.parseDouble(salaryField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "급여는 숫자로 입력해주세요.",
                        "입력 오류",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        // 부서 번호 유효성 검사
        if (!dnoField.getText().trim().isEmpty()) {
            try {
                Integer.parseInt(dnoField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "부서 번호는 숫자로 입력해주세요.",
                        "입력 오류",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        return true;
    }

    public boolean isConfirmed() { return confirmed; }
    public String getFname() { return fnameField.getText().trim(); }
    public String getMinit() { return minitField.getText().trim(); }
    public String getLname() { return lnameField.getText().trim(); }
    public String getSsn() { return ssnField.getText().trim(); }
    public String getOriginalSsn() { return originalSsn; }
    public String getBdate() { return bdateField.getText().trim(); }
    public String getAddress() { return addressField.getText().trim(); }
    public String getSex() { return (String) sexComboBox.getSelectedItem(); }
    public double getSalary() { return Double.parseDouble(salaryField.getText().trim()); }
    public String getSuperSsn() { return superSsnField.getText().trim(); }
    public int getDno() { return Integer.parseInt(dnoField.getText().trim()); }
}