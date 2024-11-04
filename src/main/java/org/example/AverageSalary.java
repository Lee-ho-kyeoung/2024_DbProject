package org.example;

public class AverageSalary {
    private String groupName;
    private double averageSalary;

    public AverageSalary(String groupName, double averageSalary) {
        this.groupName = groupName;
        this.averageSalary = averageSalary;
    }

    public String getGroupName() { return groupName; }
    public double getAverageSalary() { return averageSalary; }
}