package tech.grastone.friendzoneui.util;

import java.util.Arrays;

public class RequestBody {
    private int id;
    private int serviceId;
    private String serviceType;
    private String msgType;
    private String msgText;
    private boolean matchingPresense;
    private int matchedWith;
    private boolean initiator;
    /**
     * 0-M | 1-F | 2-O
     */
    private byte gender;
    /**
     * 0-M | 1-F | 2-O
     */
    private byte intrestedGender;
    private String[] keywords;


    public int getMatchedWith() {
        return matchedWith;
    }

    public void setMatchedWith(int matchedWith) {
        this.matchedWith = matchedWith;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
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

    public boolean isMatchingPresense() {
        return matchingPresense;
    }

    public void setMatchingPresense(boolean matchingPresense) {
        this.matchingPresense = matchingPresense;
    }

    public boolean isInitiator() {
        return initiator;
    }

    public void setInitiator(boolean initiator) {
        this.initiator = initiator;
    }

    public byte getGender() {
        return gender;
    }

    public void setGender(byte gender) {
        this.gender = gender;
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
        final StringBuffer sb = new StringBuffer("RequestBody{");
        sb.append("id=").append(id);
        sb.append(", serviceId=").append(serviceId);
        sb.append(", serviceType='").append(serviceType).append('\'');
        sb.append(", msgType='").append(msgType).append('\'');
        sb.append(", msgText='").append(msgText).append('\'');
        sb.append(", matchingPresense=").append(matchingPresense);
        sb.append(", matchedWith=").append(matchedWith);
        sb.append(", initiator=").append(initiator);
        sb.append(", gender=").append(gender);
        sb.append(", intrestedGender=").append(intrestedGender);
        sb.append(", keywords=").append(keywords == null ? "null" : Arrays.asList(keywords).toString());
        sb.append('}');
        return sb.toString();
    }
}
