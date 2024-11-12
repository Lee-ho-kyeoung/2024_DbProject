package org.example;

import javax.swing.*;
import java.awt.*;

public class ProjectInfoDialog extends JDialog {
    private JComboBox<String> projectComboBox;
    private JLabel departmentLabel;
    private JLabel employeeCountLabel;
    private JLabel totalHoursLabel;
    private JLabel averageSalaryLabel;
    private EmployeeModel model;

    public ProjectInfoDialog(Frame owner) {
        super(owner, "프로젝트 정보", true);
        model = new EmployeeModel();
        initComponents();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // 프로젝트 선택 콤보박스
        projectComboBox = new JComboBox<>();
        java.util.List<String> projects = model.getProjectList();
        for (String project : projects) {
            projectComboBox.addItem(project);
        }
        projectComboBox.addActionListener(e -> updateProjectInfo());

        // 정보 표시 패널
        JPanel infoPanel = new JPanel(new GridLayout(4, 2));
        infoPanel.add(new JLabel("관리 부서 이름:"));
        departmentLabel = new JLabel();
        infoPanel.add(departmentLabel);
        infoPanel.add(new JLabel("직원 수:"));
        employeeCountLabel = new JLabel();
        infoPanel.add(employeeCountLabel);
        infoPanel.add(new JLabel("총 작업 시간:"));
        totalHoursLabel = new JLabel();
        infoPanel.add(totalHoursLabel);
        infoPanel.add(new JLabel("평균 급여:"));
        averageSalaryLabel = new JLabel();
        infoPanel.add(averageSalaryLabel);

        add(projectComboBox, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);

        updateProjectInfo();
    }

    private void updateProjectInfo() {
        String selectedProject = (String) projectComboBox.getSelectedItem();
        ProjectInfo info = model.getProjectInfo(selectedProject);
        if (info != null) {
            departmentLabel.setText(info.getDepartmentName());
            employeeCountLabel.setText(String.valueOf(info.getEmployeeCount()));
            totalHoursLabel.setText(String.valueOf(info.getTotalHours()));
            averageSalaryLabel.setText(String.valueOf(info.getProjectAverageSalary()));
        } else {
            departmentLabel.setText("N/A");
            employeeCountLabel.setText("0");
            totalHoursLabel.setText("0.0");
            averageSalaryLabel.setText("0.0");
        }
    }
}

