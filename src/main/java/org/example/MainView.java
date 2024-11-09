package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainView extends JFrame {
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JButton searchButton;
    private JComboBox<String> searchCategoryComboBox;
    private JComboBox<String> searchValueComboBox;
    private JComboBox<String> groupByComboBox;
    private JComboBox<String> depEmpByComboBox;
    private JTextField salaryTextField;
    private JPanel searchValuePanel;
    private JCheckBox[] searchCheckBoxes;
    private JLabel selectedEmployeeLabel;
    private JLabel selectedEmployeeCountLabel;
    private JTable averageSalaryTable;
    private JTable familyTable;
    private DefaultTableModel averageSalaryTableModel;
    private DefaultTableModel familyTableModel;
    private JScrollPane averageSalaryScrollPane;
    private JScrollPane employeeScrollPane;
    private JScrollPane familyScrollPane;
    private CardLayout cardLayout;
    private JPanel tablePanel;
    private List<EmployeeDeleteListener> deleteListeners = new ArrayList<>();
    private List<EmployeeAddListener> addListeners = new ArrayList<>();
    private List<EmployeeEditListener> editListeners = new ArrayList<>();
    private JComboBox<String> projectComboBox;
    private JButton projectInfoButton;
    private JPanel checkBoxPanel;

    public interface EmployeeDeleteListener {
        void onDeleteEmployees(List<String> ssnList);
    }

    public interface EmployeeAddListener {
        void onAddEmployee(String fname, String minit, String lname, String ssn,
                           String bdate, String address, String sex,
                           double salary, String superSsn, int dno);
    }

    public interface EmployeeEditListener {
        void onEditEmployee(String originalSsn, String fname, String minit, String lname,
                            String ssn, String bdate, String address, String sex,
                            double salary, String superSsn, int dno);
    }

    // getDepartmentNumber 메소드를 클래스 레벨로 이동
    private int getDepartmentNumber(String departmentName) {
        switch(departmentName) {
            case "Research": return 5;
            case "Administration": return 4;
            case "Headquarters": return 1;
            default: return 1; // 기본값
        }
    }

    public MainView() {
        setTitle("Information Retrieval System");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 메인 패널
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // 상단 패널 (검색 조건)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchCategoryLabel = new JLabel("검색 범위: ");
        topPanel.add(searchCategoryLabel);

        // 검색 범위 콤보박스
        String[] searchCategories = {"전체", "부서", "성별", "급여", "그룹별 평균", "직계가족", "프로젝트"};
        searchCategoryComboBox = new JComboBox<>(searchCategories);
        topPanel.add(searchCategoryComboBox);

        // 검색 값 패널 생성
        searchValuePanel = new JPanel(new CardLayout());
        searchValueComboBox = new JComboBox<>();
        salaryTextField = new JTextField(10);
        String[] groupCategories = {"그룹 없음", "부서", "성별", "상급자"};
        groupByComboBox = new JComboBox<>(groupCategories);

        depEmpByComboBox = new JComboBox<>();
        projectComboBox = new JComboBox<>();

        // 검색 값 패널에 컴포넌트 추가
        searchValuePanel.add(searchValueComboBox, "COMBO");
        searchValuePanel.add(salaryTextField, "SALARY");
        searchValuePanel.add(groupByComboBox, "GROUP");
        searchValuePanel.add(depEmpByComboBox, "FAMILY");
        searchValuePanel.add(projectComboBox, "PROJECT");
        topPanel.add(searchValuePanel);

        // 검색 버튼
        searchButton = new JButton("검색");
        topPanel.add(searchButton);

        // 체크박스 패널
        checkBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchFieldsLabel = new JLabel("검색 항목: ");
        checkBoxPanel.add(searchFieldsLabel);

        String[] searchFields = {"Name", "Ssn", "Bdate", "Address", "Sex", "Salary", "Supervisor", "Department"};
        searchCheckBoxes = new JCheckBox[searchFields.length];
        for (int i = 0; i < searchFields.length; i++) {
            searchCheckBoxes[i] = new JCheckBox(searchFields[i]);
            searchCheckBoxes[i].setSelected(true);
            checkBoxPanel.add(searchCheckBoxes[i]);
        }

        // 테이블 패널
        tablePanel = new JPanel(new CardLayout());
        cardLayout = (CardLayout) tablePanel.getLayout();

        // 직원 테이블 설정
        String[] employeeColumns = {"선택", "NAME", "SSN", "BDATE", "ADDRESS", "SEX", "SALARY", "SUPERVISOR", "DEPARTMENT"};
        tableModel = new DefaultTableModel(employeeColumns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };
        employeeTable = new JTable(tableModel);
        employeeScrollPane = new JScrollPane(employeeTable);

        // 평균 급여 테이블 설정
        String[] avgColumns = {"Group", "Average Salary"};
        averageSalaryTableModel = new DefaultTableModel(avgColumns, 0);
        averageSalaryTable = new JTable(averageSalaryTableModel);
        averageSalaryScrollPane = new JScrollPane(averageSalaryTable);

        // 직계가족 테이블 설정
        String[] familyColumns = {"ESSN", "NAME", "DEPNAME", "SEX", "BDATE", "RELATION"};
        familyTableModel = new DefaultTableModel(familyColumns, 0);
        familyTable = new JTable(familyTableModel);
        familyScrollPane = new JScrollPane(familyTable);

        // 테이블 패널에 추가
        tablePanel.add(employeeScrollPane, "EMPLOYEE");
        tablePanel.add(averageSalaryScrollPane, "AVERAGE");
        tablePanel.add(familyScrollPane, "FAMILY");

        // 하단 패널
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // 선택 정보 패널
        JPanel selectionInfoPanel = new JPanel(new GridLayout(2, 1));
        selectedEmployeeLabel = new JLabel("선택한 직원: ");
        selectedEmployeeCountLabel = new JLabel("선택한 직원 수: 0");
        selectionInfoPanel.add(selectedEmployeeLabel);
        selectionInfoPanel.add(selectedEmployeeCountLabel);
        bottomPanel.add(selectionInfoPanel, BorderLayout.CENTER);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // 수정 버튼
        JButton editButton = new JButton("선택한 직원 수정");
        editButton.addActionListener(e -> {
            // SSN과 Name 체크박스의 상태 확인
            boolean isSsnChecked = searchCheckBoxes[1].isSelected(); // SSN은 두 번째 체크박스
            boolean isNameChecked = searchCheckBoxes[0].isSelected(); // Name은 첫 번째 체크박스

            if (!isSsnChecked || !isNameChecked) {
                JOptionPane.showMessageDialog(this,
                        "직원 수정을 위해서는 Name과 SSN 항목이 선택되어야 합니다.",
                        "선택 항목 오류",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int selectedCount = 0;
            Employee selectedEmployee = null;
            int selectedRow = -1;

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Boolean isSelected = (Boolean) tableModel.getValueAt(i, 0);
                if (isSelected != null && isSelected) {
                    selectedCount++;
                    selectedRow = i;
                }
            }

            if (selectedCount == 0) {
                JOptionPane.showMessageDialog(this,
                        "수정할 직원을 선택해주세요.",
                        "선택 오류",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (selectedCount > 1) {
                JOptionPane.showMessageDialog(this,
                        "한 번에 한 명의 직원만 수정할 수 있습니다.",
                        "선택 오류",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 선택된 직원의 정보로 Employee 객체 생성
            selectedEmployee = new Employee(
                    (String) tableModel.getValueAt(selectedRow, 1),  // name
                    (String) tableModel.getValueAt(selectedRow, 2),  // ssn
                    (String) tableModel.getValueAt(selectedRow, 3),  // birthDate
                    (String) tableModel.getValueAt(selectedRow, 4),  // address
                    (String) tableModel.getValueAt(selectedRow, 5),  // sex
                    Double.parseDouble((String) tableModel.getValueAt(selectedRow, 6)),  // salary
                    (String) tableModel.getValueAt(selectedRow, 7),  // supervisor
                    (String) tableModel.getValueAt(selectedRow, 8),  // department
                    getDepartmentNumber((String) tableModel.getValueAt(selectedRow, 8))  // department name으로 dno 가져오기
            );

            // 수정 다이얼로그 표시
            EditEmployeeDialog dialog = new EditEmployeeDialog(this, selectedEmployee);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                try {
                    fireEditEmployee(
                            dialog.getOriginalSsn(),
                            dialog.getFname(),
                            dialog.getMinit(),
                            dialog.getLname(),
                            dialog.getSsn(),
                            dialog.getBdate(),
                            dialog.getAddress(),
                            dialog.getSex(),
                            dialog.getSalary(),
                            dialog.getSuperSsn(),
                            dialog.getDno()
                    );
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "직원 수정 중 오류가 발생했습니다: " + ex.getMessage(),
                            "오류",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        buttonPanel.add(editButton);

        // 추가 버튼
        JButton addButton = new JButton("직원 추가");
        addButton.addActionListener(e -> {
            AddEmployeeDialog dialog = new AddEmployeeDialog(this);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                try {
                    fireAddEmployee(
                            dialog.getFname(),
                            dialog.getMinit(),
                            dialog.getLname(),
                            dialog.getSsn(),
                            dialog.getBdate(),
                            dialog.getAddress(),
                            dialog.getSex(),
                            dialog.getSalary(),
                            dialog.getSuperSsn(),
                            dialog.getDno()
                    );
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "직원 추가 중 오류가 발생했습니다: " + ex.getMessage(),
                            "오류",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        buttonPanel.add(addButton);

        // 삭제 버튼
        JButton deleteButton = new JButton("선택한 데이터 삭제");
        deleteButton.addActionListener(e -> {
            // SSN과 Name 체크박스의 상태 확인
            boolean isSsnChecked = searchCheckBoxes[1].isSelected(); // SSN은 두 번째 체크박스
            boolean isNameChecked = searchCheckBoxes[0].isSelected(); // Name은 첫 번째 체크박스

            if (!isSsnChecked || !isNameChecked) {
                JOptionPane.showMessageDialog(this,
                        "직원 삭제를 위해서는 Name과 SSN 항목이 선택되어야 합니다.",
                        "선택 항목 오류",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int selectedCount = 0;
            List<String> selectedSSNs = new ArrayList<>();

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Boolean isSelected = (Boolean) tableModel.getValueAt(i, 0);
                if (isSelected != null && isSelected) {
                    selectedCount++;
                    selectedSSNs.add((String) tableModel.getValueAt(i, 2));
                }
            }

            if (selectedCount == 0) {
                JOptionPane.showMessageDialog(this,
                        "삭제할 직원을 선택해주세요.",
                        "선택 오류",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    selectedCount + "명의 직원을 삭제하시겠습니까?",
                    "삭제 확인",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                fireDeleteEmployees(selectedSSNs);
            }
        });
        buttonPanel.add(deleteButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        // 테이블 선택 리스너
        employeeTable.getModel().addTableModelListener(e -> updateSelectedEmployeeInfo());
        // 카테고리 변경 리스너
        searchCategoryComboBox.addActionListener(e -> {
            String selectedCategory = (String) searchCategoryComboBox.getSelectedItem();
            if ("그룹별 평균".equals(selectedCategory)) {
                cardLayout.show(tablePanel, "AVERAGE");
                CardLayout cl = (CardLayout) (searchValuePanel.getLayout());
                cl.show(searchValuePanel, "GROUP");
                checkBoxPanel.setVisible(false);
            } else if ("직계가족".equals(selectedCategory)) {
                cardLayout.show(tablePanel, "FAMILY");
                CardLayout cl = (CardLayout) (searchValuePanel.getLayout());
                cl.show(searchValuePanel, "FAMILY");
                checkBoxPanel.setVisible(false);
            } else if ("프로젝트".equals(selectedCategory)) {
                cardLayout.show(tablePanel, "EMPLOYEE");
                updateSearchValues(selectedCategory);
                CardLayout cl = (CardLayout) (searchValuePanel.getLayout());
                cl.show(searchValuePanel, "PROJECT");
                checkBoxPanel.setVisible(true);
            } else {
                cardLayout.show(tablePanel, "EMPLOYEE");
                updateSearchValues(selectedCategory);
                checkBoxPanel.setVisible(true);
            }
        });

        // 프로젝트 정보 버튼
        projectInfoButton = new JButton("프로젝트 정보");
        projectInfoButton.addActionListener(e -> {
            ProjectInfoDialog dialog = new ProjectInfoDialog(this);
            dialog.setVisible(true);
        });

        // 상단 패널을 포함하는 새로운 패널 생성
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(topPanel, BorderLayout.CENTER);
        northPanel.add(projectInfoButton, BorderLayout.EAST);

        // 메인 패널에 추가
        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(checkBoxPanel, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        pack();
    }

    public void addDeleteListener(EmployeeDeleteListener listener) {
        deleteListeners.add(listener);
    }

    public void addEmployeeAddListener(EmployeeAddListener listener) {
        addListeners.add(listener);
    }

    public void addEmployeeEditListener(EmployeeEditListener listener) {
        editListeners.add(listener);
    }

    private void fireDeleteEmployees(List<String> ssnList) {
        for (EmployeeDeleteListener listener : deleteListeners) {
            listener.onDeleteEmployees(ssnList);
        }
    }

    private void fireAddEmployee(String fname, String minit, String lname, String ssn,
                                 String bdate, String address, String sex,
                                 double salary, String superSsn, int dno) {
        for (EmployeeAddListener listener : addListeners) {
            listener.onAddEmployee(fname, minit, lname, ssn, bdate, address, sex, salary, superSsn, dno);
        }
    }

    private void fireEditEmployee(String originalSsn, String fname, String minit, String lname,
                                  String ssn, String bdate, String address, String sex,
                                  double salary, String superSsn, int dno) {
        for (EmployeeEditListener listener : editListeners) {
            listener.onEditEmployee(originalSsn, fname, minit, lname, ssn, bdate,
                    address, sex, salary, superSsn, dno);
        }
    }

    public void updateSearchValues(String category) {
        CardLayout cl = (CardLayout) (searchValuePanel.getLayout());

        switch (category) {
            case "부서":
                searchValueComboBox.removeAllItems();
                String[] departments = {"Research", "Administration", "Headquarters"};
                for (String dept : departments) {
                    searchValueComboBox.addItem(dept);
                }
                cl.show(searchValuePanel, "COMBO");
                break;
            case "성별":
                searchValueComboBox.removeAllItems();
                String[] genders = {"M", "F"};
                for (String gender : genders) {
                    searchValueComboBox.addItem(gender);
                }
                cl.show(searchValuePanel, "COMBO");
                break;
            case "급여":
                salaryTextField.setText("");
                cl.show(searchValuePanel, "SALARY");
                break;
            case "전체":
                searchValueComboBox.removeAllItems();
                cl.show(searchValuePanel, "COMBO");
                break;
            case "그룹별 평균":
                cl.show(searchValuePanel, "GROUP");
                break;
            case "직계가족":
                cl.show(searchValuePanel, "FAMILY");
                break;
            case "프로젝트":
                projectComboBox.removeAllItems();
                fireRequestProjectList();
                cl.show(searchValuePanel, "PROJECT");
                break;
        }
    }

    public void updateSelectedEmployeeInfo() {
        StringBuilder selectedNames = new StringBuilder();
        int selectedCount = 0;

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Boolean isSelected = (Boolean) tableModel.getValueAt(i, 0);
            if (isSelected != null && isSelected) {
                selectedNames.append(tableModel.getValueAt(i, 1)).append(", ");
                selectedCount++;
            }
        }

        String names = selectedNames.length() > 0 ?
                selectedNames.substring(0, selectedNames.length() - 2) : "";
        selectedEmployeeLabel.setText("선택한 직원: " + names);
        selectedEmployeeCountLabel.setText("선택한 직원 수: " + selectedCount);
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public String getSelectedCategory() {
        return (String) searchCategoryComboBox.getSelectedItem();
    }

    public String getSelectedValue() {
        String selectedCategory = (String) searchCategoryComboBox.getSelectedItem();
        if ("급여".equals(selectedCategory)) {
            return salaryTextField.getText().trim();
        }
        return (String) searchValueComboBox.getSelectedItem();
    }

    public String getSelectedGroupBy() {
        return (String) groupByComboBox.getSelectedItem();
    }

    public String getSelectedFamily() {
        return (String) depEmpByComboBox.getSelectedItem();
    }

    public JCheckBox[] getSearchCheckBoxes() {
        return searchCheckBoxes;
    }

    public void setTableData(List<Employee> employees, boolean[] selectedFields) {
        tableModel.setRowCount(0);
        for (Employee emp : employees) {
            Object[] row = new Object[9];
            row[0] = false;
            if (selectedFields[0]) row[1] = emp.getName();
            if (selectedFields[1]) row[2] = emp.getSsn();
            if (selectedFields[2]) row[3] = emp.getBirthDate();
            if (selectedFields[3]) row[4] = emp.getAddress();
            if (selectedFields[4]) row[5] = emp.getSex();
            if (selectedFields[5]) row[6] = String.valueOf(emp.getSalary());
            if (selectedFields[6]) row[7] = emp.getSupervisor();
            if (selectedFields[7]) row[8] = emp.getDepartment();
            tableModel.addRow(row);
        }
        employeeTable.repaint();
    }

    public void setAverageSalaryTableData(List<AverageSalary> averageSalaries) {
        averageSalaryTableModel.setRowCount(0);
        for (AverageSalary avg : averageSalaries) {
            Object[] row = {
                    avg.getGroupName(),
                    String.format("%.2f", avg.getAverageSalary())
            };
            averageSalaryTableModel.addRow(row);
        }
    }

    public void setFamilyData(List<DependentEmployee> dependentEmployees) {
        familyTableModel.setRowCount(0);
        for (DependentEmployee depEmp : dependentEmployees) {
            Object[] row = {
                    depEmp.getEssn(),
                    depEmp.getEmpName(),
                    depEmp.getDepName(),
                    depEmp.getSex(),
                    depEmp.getBirthDate(),
                    depEmp.getRelationship()
            };
            familyTableModel.addRow(row);
        }
    }

    public void setDepEmpCategories(List<String> ssnList) {
        depEmpByComboBox.removeAllItems();
        depEmpByComboBox.addItem("전체 조회");
        for (String ssn : ssnList) {
            depEmpByComboBox.addItem(ssn);
        }
    }

    private List<ProjectListListener> projectListListeners = new ArrayList<>();

    public interface ProjectListListener {
        void onRequestProjectList();
    }

    public void addProjectListListener(ProjectListListener listener) {
        projectListListeners.add(listener);
    }

    private void fireRequestProjectList() {
        for (ProjectListListener listener : projectListListeners) {
            listener.onRequestProjectList();
        }
    }

    public void setProjectList(List<String> projects) {
        projectComboBox.removeAllItems();
        for (String project : projects) {
            projectComboBox.addItem(project);
        }
    }

    public String getSelectedProject() {
        return (String) projectComboBox.getSelectedItem();
    }
}