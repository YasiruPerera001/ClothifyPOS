package model;

public class Employee {
    private int employeeId;
    private String name;
    private String phone;
    private String position;

    public Employee(int employeeId, String name, String phone, String position) {
        this.employeeId = employeeId;
        this.name = name;
        this.phone = phone;
        this.position = position;
    }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
}