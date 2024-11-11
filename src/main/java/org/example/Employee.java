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
    private String minit;  // minit 필드 추가
    private String fname;  // fname 필드 추가
    private String lname;  // lname 필드 추가

    public Employee(String name, String ssn, Date birthDate, String address,
                    String sex, double salary, String supervisor, String department,
                    int dno, Date lastModified) {
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

        // 이름 파싱
        String[] nameParts = name.split(" ");
        this.fname = nameParts[0];
        if (nameParts.length > 2) {
            this.minit = nameParts[1];
            this.lname = nameParts[2];
        } else if (nameParts.length > 1) {
            this.lname = nameParts[1];
            this.minit = "";
        } else {
            this.minit = "";
            this.lname = "";
        }
    }

    public String getName() { return name; }
    public String getSsn() { return ssn; }
    public String getBirthDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return birthDate != null ? sdf.format(birthDate) : "";
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

    // 새로 추가된 getter 메서드들
    public String getMinit() { return minit; }
    public String getFname() { return fname; }
    public String getLname() { return lname; }
}