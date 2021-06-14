package tech.grastone.friendzoneui.util;

public class MessageBean {

    private String senderAddress;
    private String recieverAddress;
    private RequestBody messageBody;
    private String messageStr;

    public MessageBean(String senderAddress, String recieverAddress, RequestBody messageBody) {
        this.senderAddress = senderAddress;
        this.recieverAddress = recieverAddress;
        this.messageBody = messageBody;
    }

    public MessageBean(String senderAddress, String recieverAddress, String messageStr) {
        this.senderAddress = senderAddress;
        this.recieverAddress = recieverAddress;
        this.messageStr = messageStr;
    }

    public String getMessageStr() {
        return messageStr;
    }

    public void setMessageStr(String messageStr) {
        this.messageStr = messageStr;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getRecieverAddress() {
        return recieverAddress;
    }

    public void setRecieverAddress(String recieverAddress) {
        this.recieverAddress = recieverAddress;
    }

    public RequestBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(RequestBody messageBody) {
        this.messageBody = messageBody;
    }

    @Override
    public String toString() {
        return "MessageBean [senderAddress=" + senderAddress + ", recieverAddress=" + recieverAddress + ", messageBody="
                + messageBody + "]";
    }
}
