package org.example;

public class ProjectInfo {
    private String projectName;
    private String departmentName;
    private int employeeCount;
    private double totalHours;

    public ProjectInfo(String projectName, String departmentName, int employeeCount, double totalHours) {
        this.projectName = projectName;
        this.departmentName = departmentName;
        this.employeeCount = employeeCount;
        this.totalHours = totalHours;
    }

    // Getter 메서드들
    public String getProjectName() { return projectName; }
    public String getDepartmentName() { return departmentName; }
    public int getEmployeeCount() { return employeeCount; }
    public double getTotalHours() { return totalHours; }
}
