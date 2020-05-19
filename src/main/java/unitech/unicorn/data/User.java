package unitech.unicorn.data;

import unitech.unicorn.schema.annotation.*;

@Table(name = "users", checkExistence = true)
public class User extends AbstractModel {
    @Column(name = "name", nullable = false)
    @ColumnSize(size = 32)
    private String name;

    @Column(name = "surname")
    @ColumnSize(size = 32)
    private String surname;

    @Column(name = "telephone")
    @ColumnSize(size = 30)
    private String telephone;

    @Column(name = "email", nullable = false, unique = true)
    @ColumnSize(size = 64)
    private String email;

    @Column(name = "age")
    @ColumnSize(size = 3)
    private int age;

    @Column(name = "salary", defaultValue = "0")
    @ColumnSize(size = 10, scale = 2)
    private double salary;

    @Column(name = "is_single", nullable = false)
    @CHECK("is_single IN (0,1)")
    private boolean isSingle;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public boolean isSingle() {
        return isSingle;
    }

    public void setSingle(boolean single) {
        isSingle = single;
    }
}
