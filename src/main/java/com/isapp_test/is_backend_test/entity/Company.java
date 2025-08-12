package com.isapp_test.is_backend_test.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Company\"")
public class Company {
    @Id
    @Column(name = "\"CompanyID\"")
    private Integer companyId;

    @Column(name = "\"CompanyName\"", nullable = false)
    private String companyName;

    @Column(name = "\"CompanyAddress\"", nullable = false)
    private String companyAddress;

    public Integer getCompanyId() { return companyId; }
    public void setCompanyId(Integer companyId) { this.companyId = companyId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getCompanyAddress() { return companyAddress; }
    public void setCompanyAddress(String companyAddress) { this.companyAddress = companyAddress; }
}
