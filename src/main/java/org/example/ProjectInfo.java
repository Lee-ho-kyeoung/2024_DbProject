package org.example;

public class ProjectInfo {
    private String projectName;
    private String departmentName;
    private int employeeCount;
    private double totalHours;
    private double projectAverageSalary;

    public ProjectInfo(String projectName, String departmentName, int employeeCount, double totalHours, double projectAverageSalary) {
        this.projectName = projectName;
        this.departmentName = departmentName;
        this.employeeCount = employeeCount;
        this.totalHours = totalHours;
        this.projectAverageSalary = projectAverageSalary;
    }

    // Getter 메서드들
    public String getProjectName() { return projectName; }
    public String getDepartmentName() { return departmentName; }
    public int getEmployeeCount() { return employeeCount; }
    public double getTotalHours() { return totalHours; }
    public double getProjectAverageSalary() { return projectAverageSalary; }
}
