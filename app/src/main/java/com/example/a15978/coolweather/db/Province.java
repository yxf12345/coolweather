package com.example.a15978.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 15978 on 2018/5/29.
 */

public class Province extends DataSupport implements GetName
{
    private int id;
    private String provinceName;
    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
    public String getName()
    {
        return this.provinceName;
    }
}
