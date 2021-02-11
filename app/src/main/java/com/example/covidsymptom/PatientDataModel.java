package com.example.covidsymptom;

public class PatientDataModel {
    private String sign;
    private float value;

    public PatientDataModel(String sign, float value) {
        this.sign = sign;
        this.value = value;
    }
    public PatientDataModel() {

    }

    @Override
    public String toString() {
        return  sign +  ": " + value + "/5";
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public float getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}

