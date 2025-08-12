package com.isapp_test.is_backend_test.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"company\"")
public class Company {
    @Id
    @Column(name = "\"companyid\"")
    private Integer companyId;

    @Column(name = "\"company_name\"", nullable = false)
    private String companyName;

    @Column(name = "\"company_address\"", nullable = false)
    private String companyAddress;

    public Integer getCompanyId() { return companyId; }
    public void setCompanyId(Integer companyId) { this.companyId = companyId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getCompanyAddress() { return companyAddress; }
    public void setCompanyAddress(String companyAddress) { this.companyAddress = companyAddress; }
}
