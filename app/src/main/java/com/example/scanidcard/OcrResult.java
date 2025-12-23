package com.example.scanidcard;

public class OcrResult {

    public String name;
    public String sex;
    public String nation;
    public String birth;
    public String address;
    public String idNum;
    public String authority;
    public String validDate;

    @Override
    public String toString() {
        return
                "姓名：" + name + "\n" +
                        "性别：" + sex + "\n" +
                        "民族：" + nation + "\n" +
                        "出生日期：" + birth + "\n" +
                        "身份证号：" + idNum + "\n" +
                        "住址：" + address + "\n" +
                        "签发机关：" + authority + "\n" +
                        "有效期：" + validDate;
    }
}
