package com.isapp_test.is_backend_test.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"user\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"")
    private Integer id;

    @Column(name = "\"user_name\"", nullable = false)
    private String userName;

    @Column(name = "\"companyid\"", nullable = false)
    private Integer companyId;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public Integer getCompanyId() { return companyId; }
    public void setCompanyId(Integer companyId) { this.companyId = companyId; }
}
