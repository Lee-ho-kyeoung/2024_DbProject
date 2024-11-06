package org.example;

import io.github.cdimascio.dotenv.Dotenv;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmployeeModel {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    // 모든 직원 검색
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(
                         "SELECT E.Fname, E.Lname, E.SSN, E.Bdate, E.Address, E.Sex, E.Salary, " +
                                 "S.Fname AS Supervisor, D.Dname " +
                                 "FROM EMPLOYEE E " +
                                 "LEFT JOIN EMPLOYEE S ON E.Super_ssn = S.SSN " +
                                 "JOIN DEPARTMENT D ON E.Dno = D.Dnumber")) {

                while (resultSet.next()) {
                    Employee employee = new Employee(
                            resultSet.getString("Fname") + " " + resultSet.getString("Lname"),
                            resultSet.getString("SSN"),
                            resultSet.getString("Bdate"),
                            resultSet.getString("Address"),
                            resultSet.getString("Sex"),
                            resultSet.getDouble("Salary"),
                            resultSet.getString("Supervisor"),
                            resultSet.getString("Dname")
                    );
                    employees.add(employee);
                }
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found: " + e.getMessage(),
                    "Driver Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection error: " + e.getMessage(),
                    "SQL Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return employees;
    }

    // 특정 직원 검색
    public List<Employee> searchEmployees(String category, String value) {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT E.Fname, E.Lname, E.SSN, E.Bdate, E.Address, E.Sex, E.Salary, " +
                "S.Fname AS Supervisor, D.Dname " +
                "FROM EMPLOYEE E " +
                "LEFT JOIN EMPLOYEE S ON E.Super_ssn = S.SSN " +
                "JOIN DEPARTMENT D ON E.Dno = D.Dnumber " +
                "WHERE ";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                PreparedStatement statement;

                switch (category) {
                    case "부서":
                        query += "D.Dname = ?";
                        statement = connection.prepareStatement(query);
                        statement.setString(1, value);
                        break;
                    case "성별":
                        query += "E.Sex = ?";
                        statement = connection.prepareStatement(query);
                        statement.setString(1, value);
                        break;
                    case "급여":
                        try {
                            double salaryValue = Double.parseDouble(value);
                            query += "E.Salary >= ?";
                            statement = connection.prepareStatement(query);
                            statement.setDouble(1, salaryValue);
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(null,
                                    "잘못된 급여 형식입니다. 숫자를 입력해주세요.",
                                    "입력 오류",
                                    JOptionPane.ERROR_MESSAGE);
                            return employees;
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid category: " + category);
                }

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Employee employee = new Employee(
                                resultSet.getString("Fname") + " " + resultSet.getString("Lname"),
                                resultSet.getString("SSN"),
                                resultSet.getString("Bdate"),
                                resultSet.getString("Address"),
                                resultSet.getString("Sex"),
                                resultSet.getDouble("Salary"),
                                resultSet.getString("Supervisor"),
                                resultSet.getString("Dname")
                        );
                        employees.add(employee);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found: " + e.getMessage(),
                    "Driver Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection error: " + e.getMessage(),
                    "SQL Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return employees;
    }

    // 그룹의 평균 급여
    public List<AverageSalary> getGroupAverageSalary(String groupBy) {
        List<AverageSalary> result = new ArrayList<>();
        String query = "";

        switch (groupBy) {
            case "그룹 없음":
                query = "SELECT 'Total' as groupName, AVG(Salary) as avgSalary FROM EMPLOYEE";
                break;
            case "부서":
                query = "SELECT D.Dname as groupName, AVG(E.Salary) as avgSalary " +
                        "FROM EMPLOYEE E JOIN DEPARTMENT D ON E.Dno = D.Dnumber " +
                        "GROUP BY D.Dname";
                break;
            case "성별":
                query = "SELECT Sex as groupName, AVG(Salary) as avgSalary " +
                        "FROM EMPLOYEE GROUP BY Sex";
                break;
            case "상급자":
                query = "SELECT CONCAT(S.Fname, ' ', S.Lname) as groupName, AVG(E.Salary) as avgSalary " +
                        "FROM EMPLOYEE E LEFT JOIN EMPLOYEE S ON E.Super_ssn = S.SSN " +
                        "GROUP BY E.Super_ssn";
                break;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                while (resultSet.next()) {
                    String groupName = resultSet.getString("groupName");
                    double avgSalary = resultSet.getDouble("avgSalary");
                    result.add(new AverageSalary(groupName != null ? groupName : "No Supervisor", avgSalary));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return result;
    }

    //직원 삭제
    public void deleteEmployees(List<String> ssnList) {
        String query = "DELETE FROM EMPLOYEE WHERE SSN IN (" +
                String.join(",", Collections.nCopies(ssnList.size(), "?")) + ")";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(query)) {

                for (int i = 0; i < ssnList.size(); i++) {
                    statement.setString(i + 1, ssnList.get(i));
                }

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null,
                            rowsAffected + "명의 직원이 삭제되었습니다.",
                            "삭제 완료",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "직원 삭제 중 오류가 발생했습니다: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // 직원 추가
    public void addEmployee(String fname, String minit, String lname, String ssn, String bdate,
                            String address, String sex, double salary, String superSsn, int dno) {
        if (ssn == null || ssn.trim().isEmpty()) {
            throw new IllegalArgumentException("SSN은 필수 입력 항목입니다.");
        }

        String query = "INSERT INTO EMPLOYEE (Fname, Minit, Lname, SSN, Bdate, Address, Sex, Salary, Super_ssn, Dno) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, fname);
                statement.setString(2, minit);
                statement.setString(3, lname);
                statement.setString(4, ssn);
                statement.setString(5, bdate);
                statement.setString(6, address);
                statement.setString(7, sex);
                statement.setDouble(8, salary);
                if (superSsn != null && !superSsn.trim().isEmpty()) {
                    statement.setString(9, superSsn);
                } else {
                    statement.setNull(9, Types.VARCHAR);
                }
                statement.setInt(10, dno);

                statement.executeUpdate();
                JOptionPane.showMessageDialog(null, "직원이 성공적으로 추가되었습니다.",
                        "추가 완료", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(null, "이미 존재하는 SSN입니다.",
                    "추가 실패", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "직원 추가 중 오류가 발생했습니다: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // 직원 직계가족 검색
    public List<DependentEmployee> getDependentEmployees(String Ssn) {

        List<DependentEmployee> dependentEmployees = new ArrayList<>();
        String query;

        if (Ssn.equals("전체 조회")) {
            query = "SELECT e.Fname, e.Minit, e.Lname, d.Essn, d.Dependent_name, d.Sex, d.Bdate, d.Relationship " +
                    "FROM EMPLOYEE e " +
                    "INNER JOIN DEPENDENT d ON e.Ssn = d.Essn";
        } else {
            query = "SELECT e.Fname, e.Minit, e.Lname, d.Essn, d.Dependent_name, d.Sex, d.Bdate, d.Relationship " +
                    "FROM EMPLOYEE e " +
                    "INNER JOIN DEPENDENT d ON e.Ssn = d.Essn " +
                    "WHERE d.Essn = ?";


        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                if (!Ssn.equals("전체 조회")) {
                    preparedStatement.setString(1, Ssn);
                }

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        DependentEmployee dependentEmployee = new DependentEmployee(
                                resultSet.getString("ESSN"),
                                resultSet.getString("Fname") + " " + resultSet.getString("Minit") + " " + resultSet.getString("Lname"),
                                resultSet.getString("Dependent_name"),
                                resultSet.getString("Sex"),
                                resultSet.getString("Bdate"),
                                resultSet.getString("Relationship")
                        );
                        dependentEmployees.add(dependentEmployee);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return dependentEmployees;

    }

    // 직계가족을 가진 직원의 SSN 리스트
    public List<String> getHavingDepEmpSsnList() {

        List<String> EmpSsn = new ArrayList<>();
        String query = "SELECT DISTINCT ESSN FROM DEPENDENT";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                while (resultSet.next()) {
                    String essn = resultSet.getString("Essn");
                    EmpSsn.add(essn);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return EmpSsn;

    }
}