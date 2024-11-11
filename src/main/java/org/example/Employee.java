package org.example;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Employee {
    private String name;
    private String ssn;
    private Date birthDate;
    private String address;
    private String sex;
    private double salary;
    private String supervisor;
    private String department;
    private int dno;
    private Date lastModified;


    public Employee(String name, String ssn, Date birthDate, String address,
                    String sex, double salary, String supervisor, String department, int dno, Date lastModified) {
        this.name = name;
        this.ssn = ssn;
        this.birthDate = birthDate;
        this.address = address;
        this.sex = sex;
        this.salary = salary;
        this.supervisor = supervisor;
        this.department = department;
        this.dno = dno;
        this.lastModified = lastModified;
    }

    // getter 메소드
    public String getName() { return name; }
    public String getSsn() { return ssn; }
    public String getBirthDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(birthDate); // Date 객체를 String으로 변환
    }
    public String getAddress() { return address; }
    public String getSex() { return sex; }
    public double getSalary() { return salary; }
    public String getSupervisor() { return supervisor; }
    public String getDepartment() { return department; }
    public int getDno() { return dno; }

    public String getLastModified() {
        if (lastModified == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(lastModified);
    }
}