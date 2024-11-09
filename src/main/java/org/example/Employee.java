package org.example;

public class Employee {
    private String name;
    private String ssn;
    private String birthDate;
    private String address;
    private String sex;
    private double salary;
    private String supervisor;
    private String department;
    private int dno; // 추가된 필드

    public Employee(String name, String ssn, String birthDate, String address,
                    String sex, double salary, String supervisor, String department, int dno) {
        this.name = name;
        this.ssn = ssn;
        this.birthDate = birthDate;
        this.address = address;
        this.sex = sex;
        this.salary = salary;
        this.supervisor = supervisor;
        this.department = department;
        this.dno = dno;
    }

    // 기존 getter 메소드들...
    public String getName() { return name; }
    public String getSsn() { return ssn; }
    public String getBirthDate() { return birthDate; }
    public String getAddress() { return address; }
    public String getSex() { return sex; }
    public double getSalary() { return salary; }
    public String getSupervisor() { return supervisor; }
    public String getDepartment() { return department; }
    public int getDno() { return dno; } // 새로운 getter
}