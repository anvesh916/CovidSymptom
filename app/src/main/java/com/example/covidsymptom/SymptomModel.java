package com.example.covidsymptom;

public class SymptomModel {
    private int RESP_RATE;
    private int HEART_RATE ;
    private int NAUSEA ;
    private int HEAD_ACHE;
    private int DIARRHEA;
    private int SOAR_THROAT;
    private int FEVER ;
    private int MUSCLE_ACHE ;
    private int NO_SMELL_TASTE ;
    private int COUGH ;
    private int SHORT_BREATH;
    private int FEEL_TIRED;

    public SymptomModel(int RESP_RATE, int HEART_RATE, int NAUSEA, int HEAD_ACHE, int DIARRHEA, int SOAR_THROAT, int FEVER, int MUSCLE_ACHE, int NO_SMELL_TASTE, int COUGH, int SHORT_BREATH, int FEEL_TIRED) {
        this.RESP_RATE = RESP_RATE;
        this.HEART_RATE = HEART_RATE;
        this.NAUSEA = NAUSEA;
        this.HEAD_ACHE = HEAD_ACHE;
        this.DIARRHEA = DIARRHEA;
        this.SOAR_THROAT = SOAR_THROAT;
        this.FEVER = FEVER;
        this.MUSCLE_ACHE = MUSCLE_ACHE;
        this.NO_SMELL_TASTE = NO_SMELL_TASTE;
        this.COUGH = COUGH;
        this.SHORT_BREATH = SHORT_BREATH;
        this.FEEL_TIRED = FEEL_TIRED;
    }


    @Override
    public String toString() {
        return "SymptomModel{" +
                "RESP_RATE=" + RESP_RATE +
                ", HEART_RATE=" + HEART_RATE +
                ", NAUSEA=" + NAUSEA +
                ", HEAD_ACHE=" + HEAD_ACHE +
                ", DIARRHEA=" + DIARRHEA +
                ", SOAR_THROAT=" + SOAR_THROAT +
                ", FEVER=" + FEVER +
                ", MUSCLE_ACHE=" + MUSCLE_ACHE +
                ", NO_SMELL_TASTE=" + NO_SMELL_TASTE +
                ", COUGH=" + COUGH +
                ", SHORT_BREATH=" + SHORT_BREATH +
                ", FEEL_TIRED=" + FEEL_TIRED +
                '}';
    }

    public int getRESP_RATE() {
        return RESP_RATE;
    }

    public void setRESP_RATE(int RESP_RATE) {
        this.RESP_RATE = RESP_RATE;
    }

    public int getHEART_RATE() {
        return HEART_RATE;
    }

    public void setHEART_RATE(int HEART_RATE) {
        this.HEART_RATE = HEART_RATE;
    }

    public int getNAUSEA() {
        return NAUSEA;
    }

    public void setNAUSEA(int NAUSEA) {
        this.NAUSEA = NAUSEA;
    }

    public int getHEAD_ACHE() {
        return HEAD_ACHE;
    }

    public void setHEAD_ACHE(int HEAD_ACHE) {
        this.HEAD_ACHE = HEAD_ACHE;
    }

    public int getDIARRHEA() {
        return DIARRHEA;
    }

    public void setDIARRHEA(int DIARRHEA) {
        this.DIARRHEA = DIARRHEA;
    }

    public int getSOAR_THROAT() {
        return SOAR_THROAT;
    }

    public void setSOAR_THROAT(int SOAR_THROAT) {
        this.SOAR_THROAT = SOAR_THROAT;
    }

    public int getFEVER() {
        return FEVER;
    }

    public void setFEVER(int FEVER) {
        this.FEVER = FEVER;
    }

    public int getMUSCLE_ACHE() {
        return MUSCLE_ACHE;
    }

    public void setMUSCLE_ACHE(int MUSCLE_ACHE) {
        this.MUSCLE_ACHE = MUSCLE_ACHE;
    }

    public int getNO_SMELL_TASTE() {
        return NO_SMELL_TASTE;
    }

    public void setNO_SMELL_TASTE(int NO_SMELL_TASTE) {
        this.NO_SMELL_TASTE = NO_SMELL_TASTE;
    }

    public int getCOUGH() {
        return COUGH;
    }

    public void setCOUGH(int COUGH) {
        this.COUGH = COUGH;
    }

    public int getSHORT_BREATH() {
        return SHORT_BREATH;
    }

    public void setSHORT_BREATH(int SHORT_BREATH) {
        this.SHORT_BREATH = SHORT_BREATH;
    }

    public int getFEEL_TIRED() {
        return FEEL_TIRED;
    }

    public void setFEEL_TIRED(int FEEL_TIRED) {
        this.FEEL_TIRED = FEEL_TIRED;
    }
}

