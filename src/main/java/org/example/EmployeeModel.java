package org.example;

import io.github.cdimascio.dotenv.Dotenv;

import javax.swing.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmployeeModel {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    private Connection connection;

    public EmployeeModel() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "DB 연결 중 오류가 발생했습니다: " + e.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isSSNExists(String ssn) {
        String query = "SELECT COUNT(*) FROM EMPLOYEE WHERE SSN = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, ssn);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isDeptExists(int Dno) {
        String query = "SELECT EXISTS (SELECT 1 FROM Department WHERE Dnumber = ?) AS DeptExists";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, Dno);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("DeptExists");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateEmployee(String originalSsn, String fname, String minit, String lname,
                               String ssn, String bdate, String address, String sex,
                               double salary, String superSsn, int dno) {
        if (superSsn.equals(ssn)) {
            throw new IllegalArgumentException("자기 자신을 상사의 SSN으로 설정할 수 없습니다.");
        }

        if (!superSsn.isEmpty() && !isSSNExists(superSsn)) {
            throw new IllegalArgumentException("유효하지 않은 SSN입니다. 상사의 SSN을 확인해주세요.");
        }

        Date BDate = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedDate = sdf.parse(bdate);
            BDate = new Date(parsedDate.getTime());
        } catch (Exception e) {
            throw new IllegalArgumentException("잘못된 날짜 형식입니다. yyyy-MM-dd 형식으로 입력해주세요.");
        }

        if(!isDeptExists(dno)){
            throw new IllegalArgumentException("유효하지 않은 부서번호입니다. 부서번호를 확인해주세요.");
        }

        String query = "UPDATE EMPLOYEE SET Fname=?, Minit=?, Lname=?, SSN=?, Bdate=?, " +
                "Address=?, Sex=?, Salary=?, Super_ssn=?, Dno=?, Last_Modified=CURRENT_TIMESTAMP " +
                "WHERE SSN=?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, fname);
            statement.setString(2, minit);
            statement.setString(3, lname);
            statement.setString(4, ssn);
            statement.setDate(5, BDate);
            statement.setString(6, address);
            statement.setString(7, sex);
            statement.setDouble(8, salary);
            if (superSsn != null && !superSsn.trim().isEmpty()) {
                statement.setString(9, superSsn);
            } else {
                statement.setNull(9, Types.VARCHAR);
            }
            statement.setInt(10, dno);
            statement.setString(11, originalSsn);

            statement.executeUpdate();
            JOptionPane.showMessageDialog(null,
                    "직원 정보가 성공적으로 수정되었습니다.",
                    "수정 완료",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "직원 정보 수정 중 오류가 발생했습니다: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT E.Fname, E.Minit, E.Lname, E.SSN, E.Bdate, E.Address, E.Sex, E.Salary, " +
                "E.Super_ssn AS Supervisor, D.Dname, E.Dno, E.Last_Modified " +
                "FROM EMPLOYEE E " +
                "LEFT JOIN EMPLOYEE S ON E.Super_ssn = S.SSN " +
                "JOIN DEPARTMENT D ON E.Dno = D.Dnumber";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String fname = resultSet.getString("Fname");
                String minit = resultSet.getString("Minit");
                String lname = resultSet.getString("Lname");

                // 전체 이름 조합
                String fullName = fname;
                if (minit != null && !minit.trim().isEmpty()) {
                    fullName += " " + minit;
                }
                if (lname != null && !lname.trim().isEmpty()) {
                    fullName += " " + lname;
                }

                Employee employee = new Employee(
                        fullName,
                        resultSet.getString("SSN"),
                        resultSet.getDate("Bdate"),
                        resultSet.getString("Address"),
                        resultSet.getString("Sex"),
                        resultSet.getDouble("Salary"),
                        resultSet.getString("Supervisor"),
                        resultSet.getString("Dname"),
                        resultSet.getInt("Dno"),
                        resultSet.getTimestamp("Last_Modified")
                );
                employees.add(employee);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "데이터베이스 오류: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        return employees;
    }

    public List<Employee> searchEmployees(String category, String value) {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT E.Fname, E.Minit, E.Lname, E.SSN, E.Bdate, E.Address, E.Sex, E.Salary, " +
                "E.Super_ssn AS Supervisor, D.Dname, E.Dno, E.Last_Modified " +
                "FROM EMPLOYEE E " +
                "LEFT JOIN EMPLOYEE S ON E.Super_ssn = S.SSN " +
                "JOIN DEPARTMENT D ON E.Dno = D.Dnumber WHERE ";

        try {
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
                    double salaryValue = Double.parseDouble(value);
                    query += "E.Salary >= ?";
                    statement = connection.prepareStatement(query);
                    statement.setDouble(1, salaryValue);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid category: " + category);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String fname = resultSet.getString("Fname");
                    String minit = resultSet.getString("Minit");
                    String lname = resultSet.getString("Lname");

                    // 전체 이름 조합
                    String fullName = fname;
                    if (minit != null && !minit.trim().isEmpty()) {
                        fullName += " " + minit;
                    }
                    if (lname != null && !lname.trim().isEmpty()) {
                        fullName += " " + lname;
                    }

                    Employee employee = new Employee(
                            fullName,
                            resultSet.getString("SSN"),
                            resultSet.getDate("Bdate"),
                            resultSet.getString("Address"),
                            resultSet.getString("Sex"),
                            resultSet.getDouble("Salary"),
                            resultSet.getString("Supervisor"),
                            resultSet.getString("Dname"),
                            resultSet.getInt("Dno"),
                            resultSet.getTimestamp("Last_Modified")
                    );
                    employees.add(employee);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return employees;
    }

    public List<Employee> getEmployeesByProject(String projectName) {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT E.Fname, E.Minit, E.Lname, E.SSN, E.Bdate, E.Address, E.Sex, E.Salary, " +
                "E.Super_ssn AS Supervisor, D.Dname, E.Dno, E.Last_Modified " +
                "FROM EMPLOYEE E " +
                "LEFT JOIN EMPLOYEE S ON E.Super_ssn = S.SSN " +
                "JOIN DEPARTMENT D ON E.Dno = D.Dnumber " +
                "JOIN WORKS_ON W ON E.SSN = W.Essn " +
                "JOIN PROJECT P ON W.Pno = P.Pnumber " +
                "WHERE P.Pname = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, projectName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String fname = resultSet.getString("Fname");
                    String minit = resultSet.getString("Minit");
                    String lname = resultSet.getString("Lname");

                    // 전체 이름 조합
                    String fullName = fname;
                    if (minit != null && !minit.trim().isEmpty()) {
                        fullName += " " + minit;
                    }
                    if (lname != null && !lname.trim().isEmpty()) {
                        fullName += " " + lname;
                    }

                    Employee employee = new Employee(
                            fullName,
                            resultSet.getString("SSN"),
                            resultSet.getDate("Bdate"),
                            resultSet.getString("Address"),
                            resultSet.getString("Sex"),
                            resultSet.getDouble("Salary"),
                            resultSet.getString("Supervisor"),
                            resultSet.getString("Dname"),
                            resultSet.getInt("Dno"),
                            resultSet.getTimestamp("Last_Modified")
                    );
                    employees.add(employee);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return employees;
    }

    // ... (나머지 메서드들은 동일하게 유지)

    public List<AverageSalary> getGroupAverageSalary(String groupBy) {
        List<AverageSalary> result = new ArrayList<>();
        String query = switch (groupBy) {
            case "그룹 없음" -> "SELECT 'Total' as groupName, AVG(Salary) as avgSalary FROM EMPLOYEE";
            case "부서" -> "SELECT D.Dname as groupName, AVG(E.Salary) as avgSalary " +
                    "FROM EMPLOYEE E JOIN DEPARTMENT D ON E.Dno = D.Dnumber GROUP BY D.Dname";
            case "성별" -> "SELECT Sex as groupName, AVG(Salary) as avgSalary FROM EMPLOYEE GROUP BY Sex";
            case "상급자" -> "SELECT CONCAT(S.Fname, ' ', S.Lname) as groupName, AVG(E.Salary) as avgSalary " +
                    "FROM EMPLOYEE E LEFT JOIN EMPLOYEE S ON E.Super_ssn = S.SSN GROUP BY E.Super_ssn";
            default -> throw new IllegalArgumentException("Invalid groupBy: " + groupBy);
        };

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String groupName = resultSet.getString("groupName");
                double avgSalary = resultSet.getDouble("avgSalary");
                result.add(new AverageSalary(groupName != null ? groupName : "No Supervisor", avgSalary));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return result;
    }

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

    public void addEmployee(String fname, String minit, String lname, String ssn, String bdate,
                            String address, String sex, double salary, String superSsn, int dno) {
        if (ssn == null || ssn.trim().isEmpty()) {
            throw new IllegalArgumentException("SSN은 필수 입력 항목입니다.");
        }

        if (superSsn != null && !superSsn.trim().isEmpty() && superSsn.equals(ssn)) {
            throw new IllegalArgumentException("자기 자신을 상사의 SSN으로 설정할 수 없습니다.");
        }

        if (superSsn != null && !superSsn.trim().isEmpty() && !isSSNExists(superSsn)) {
            throw new IllegalArgumentException("유효하지 않은 SSN입니다. 상사의 SSN을 확인해주세요.");
        }

        if(!isDeptExists(dno)){
            throw new IllegalArgumentException("유효하지 않은 부서번호입니다. 부서번호를 확인해주세요.");
        }

        Date BDate = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedDate = sdf.parse(bdate);
            BDate = new Date(parsedDate.getTime());
        } catch (Exception e) {
            throw new IllegalArgumentException("잘못된 날짜 형식입니다. yyyy-MM-dd 형식으로 입력해주세요.");
        }

        String query = "INSERT INTO EMPLOYEE (Fname, Minit, Lname, SSN, Bdate, Address, Sex, Salary, Super_ssn, Dno) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, fname);
            preparedStatement.setString(2, minit);
            preparedStatement.setString(3, lname);
            preparedStatement.setString(4, ssn);
            preparedStatement.setDate(5, BDate);
            preparedStatement.setString(6, address);
            preparedStatement.setString(7, sex);
            preparedStatement.setDouble(8, salary);
            if (superSsn != null && !superSsn.trim().isEmpty()) {
                preparedStatement.setString(9, superSsn);
            } else {
                preparedStatement.setNull(9, Types.VARCHAR);
            }
            preparedStatement.setInt(10, dno);

            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "직원이 성공적으로 추가되었습니다.",
                    "추가 완료", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(null, "이미 존재하는 SSN입니다.",
                    "추가 실패", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "직원 추가 중 오류가 발생했습니다: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

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

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if (!Ssn.equals("전체 조회")) {
                preparedStatement.setString(1, Ssn);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String empName = resultSet.getString("Fname");
                    String minit = resultSet.getString("Minit");
                    String lname = resultSet.getString("Lname");

                    if (minit != null && !minit.trim().isEmpty()) {
                        empName += " " + minit;
                    }
                    empName += " " + lname;

                    DependentEmployee dependentEmployee = new DependentEmployee(
                            resultSet.getString("Essn"),
                            empName,
                            resultSet.getString("Dependent_name"),
                            resultSet.getString("Sex"),
                            resultSet.getString("Bdate"),
                            resultSet.getString("Relationship")
                    );
                    dependentEmployees.add(dependentEmployee);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "직계가족 검색 중 오류가 발생했습니다: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return dependentEmployees;
    }

    public List<String> getHavingDepEmpSsnList() {
        List<String> EmpSsn = new ArrayList<>();
        String query = "SELECT DISTINCT ESSN FROM DEPENDENT";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String essn = resultSet.getString("Essn");
                EmpSsn.add(essn);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return EmpSsn;
    }

    public List<String> getProjectList() {
        List<String> projectList = new ArrayList<>();
        String query = "SELECT DISTINCT Pname FROM PROJECT";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                projectList.add(resultSet.getString("Pname"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "프로젝트 목록을 가져오는 중 오류 발생: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        return projectList;
    }

    public ProjectInfo getProjectInfo(String projectName) {
        String query = "SELECT P.Pname, D.Dname, COUNT(W.Essn) AS EmployeeCount, SUM(W.Hours) AS TotalHours " +
                "FROM PROJECT P " +
                "JOIN DEPARTMENT D ON P.Dnum = D.Dnumber " +
                "LEFT JOIN WORKS_ON W ON P.Pnumber = W.Pno " +
                "WHERE P.Pname = ? " +
                "GROUP BY P.Pname, D.Dname";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, projectName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String pname = resultSet.getString("Pname");
                    String dname = resultSet.getString("Dname");
                    int employeeCount = resultSet.getInt("EmployeeCount");
                    double totalHours = resultSet.getDouble("TotalHours");

                    return new ProjectInfo(pname, dname, employeeCount, totalHours);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "프로젝트 정보 가져오는 중 오류 발생: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    public List<String> getDeptList(){
        List<String> deptList = new ArrayList<>();
        String query = "SELECT DISTINCT Dname FROM DEPARTMENT";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                deptList.add(resultSet.getString("Dname"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "부서 목록을 가져오는 중 오류 발생: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        return deptList;
    }
}