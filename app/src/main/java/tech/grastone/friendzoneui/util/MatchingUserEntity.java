package tech.grastone.friendzoneui.util;

import java.util.Arrays;

public class MatchingUserEntity {
    private int id;
    private String msgType;
    private String msgText;
    private boolean matchingPresense;
    /**
     * 0-M | 1-F | 2-O
     */
    private byte gender;
    /**
     * 0-M | 1-F | 2-O
     */
    private byte intrestedGender;
    private String[] keywords;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isMatchingPresense() {
        return matchingPresense;
    }

    public void setMatchingPresense(boolean matchingPresense) {
        this.matchingPresense = matchingPresense;
    }

    public byte getGender() {
        return gender;
    }

    public void setGender(byte gender) {
        this.gender = gender;
    }


    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public byte getIntrestedGender() {
        return intrestedGender;
    }

    public void setIntrestedGender(byte intrestedGender) {
        this.intrestedGender = intrestedGender;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }

    @Override
    public String toString() {
        return "MatchingUserEntity [id=" + id + ", msgType=" + msgType + ", msgText=" + msgText + ", matchingPresense="
                + matchingPresense + ", gender=" + gender + ", intrestedGender=" + intrestedGender + ", keywords="
                + Arrays.toString(keywords) + "]";
    }

}
