package org.example;

public class DependentEmployee {

    private String essn;
    private String empName;
    private String depName;
    private String sex;
    private String birthDate;
    private String relationship;

    public DependentEmployee(String essn, String empName, String depName, String sex, String birthDate, String relationship) {
        this.essn = essn;
        this.empName = empName;
        this.depName = depName;
        this.sex = sex;
        this.birthDate = birthDate;
        this.relationship = relationship;
    }

    //Getter
    public String getEssn() {return essn;}
    public String getEmpName() {return empName;}
    public String getDepName() {return depName;}
    public String getSex(){return sex;}
    public String getBirthDate(){return birthDate;}
    public String getRelationship(){return relationship;}

}
