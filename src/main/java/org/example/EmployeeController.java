package org.example;

import javax.swing.*;
import java.util.List;

public class EmployeeController {
    private EmployeeModel model;
    private MainView view;

    public EmployeeController(EmployeeModel model, MainView view) {
        this.model = model;
        this.view = view;

        this.view.getSearchButton().addActionListener(e -> loadEmployeeData());
        List<String> essnList = model.getHavingDepEmpSsnList();
        view.setDepEmpCategories(essnList);


        // 삭제 이벤트 리스너 추가
        this.view.addDeleteListener(ssnList -> {
            model.deleteEmployees(ssnList);
            loadEmployeeData(); // 테이블 새로고침
        });

        // 추가 이벤트 리스너 추가
        this.view.addEmployeeAddListener((fname, minit, lname, ssn, bdate, address, sex, salary, superSsn, dno) -> {
            model.addEmployee(fname, minit, lname, ssn, bdate, address, sex, salary, superSsn, dno);
            loadEmployeeData(); // 테이블 새로고침
        });

        // 프로젝트 목록 요청 리스너 추가
        this.view.addProjectListListener(() -> {
            List<String> projects = model.getProjectList();
            view.setProjectList(projects);
        });
    }

    private void loadEmployeeData() {
        String selectedCategory = view.getSelectedCategory();

        if ("그룹별 평균".equals(selectedCategory)) {
            String groupBy = view.getSelectedGroupBy();
            List<AverageSalary> averageSalaries = model.getGroupAverageSalary(groupBy);
            view.setAverageSalaryTableData(averageSalaries);
        } else if("직계가족".equals(selectedCategory)){
            String family = view.getSelectedFamily();
            List<DependentEmployee> dependentEmployees =model.getDependentEmployees(family);
            view.setFamilyData(dependentEmployees);
        }else {
            String selectedValue = view.getSelectedValue();
            JCheckBox[] checkBoxes = view.getSearchCheckBoxes();
            List<Employee> employees;

            if ("전체".equals(selectedCategory)) {
                employees = model.getAllEmployees();
            } else if ("프로젝트".equals(selectedCategory)) {
                String selectedProject = view.getSelectedProject();
                employees = model.getEmployeesByProject(selectedProject);
            } else {
                employees = model.searchEmployees(selectedCategory, selectedValue);
            }

            boolean[] selectedFields = new boolean[checkBoxes.length];
            for (int i = 0; i < checkBoxes.length; i++) {
                selectedFields[i] = checkBoxes[i].isSelected();
            }
            view.setTableData(employees, selectedFields);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainView view = new MainView();
            EmployeeModel model = new EmployeeModel();
            EmployeeController controller = new EmployeeController(model, view);
            view.setVisible(true);
        });
    }
}