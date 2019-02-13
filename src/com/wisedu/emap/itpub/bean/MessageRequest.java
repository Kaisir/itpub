package com.wisedu.emap.itpub.bean;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by LuQiang on 2016/1/11.
 */
public class MessageRequest extends BaseRequest implements Cloneable {
    
    private static final Logger LOG = LoggerFactory.getLogger(MessageRequest.class);
    
    private String appId;
    private String senderId;
    private String senderName;
    private String mailSender;
    private String mailSenderPw;
    private String subject;
    private String content;
    private String pcUrl;
    private String mobileUrl;
    private String urlDesc;
    private Integer sendType;
    private Boolean sendNow;
    private Long sendTime;
    private Integer tagId;
    private List<Long> attachments;
    private List<Receivers> receivers;
    private String wxSendType;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPcUrl() {
        return pcUrl;
    }

    public void setPcUrl(String pcUrl) {
        this.pcUrl = pcUrl;
    }

    public String getMobileUrl() {
        return mobileUrl;
    }

    public void setMobileUrl(String mobileUrl) {
        this.mobileUrl = mobileUrl;
    }

    public Integer getSendType() {
        return sendType;
    }

    public void setSendType(Integer sendType) {
        this.sendType = sendType;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public boolean isSendNow() {
        return sendNow;
    }

    public void setSendNow(boolean sendNow) {
        this.sendNow = sendNow;
    }

    public String getUrlDesc() {
        return urlDesc;
    }

    public void setUrlDesc(String urlDesc) {
        this.urlDesc = urlDesc;
    }

    public List<Long> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Long> attachments) {
        this.attachments = attachments;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public String getMailSender() {
        return mailSender;
    }

    public void setMailSender(String mailSender) {
        this.mailSender = mailSender;
    }

    public String getMailSenderPw() {
        return mailSenderPw;
    }

    public void setMailSenderPw(String mailSenderPw) {
        this.mailSenderPw = mailSenderPw;
    }

    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }

    public List<Receivers> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<Receivers> receivers) {
        this.receivers = receivers;
    }

    public String getWxSendType() {
        return wxSendType;
    }

    public void setWxSendType(String wxSendType) {
        this.wxSendType = wxSendType;
    }
    
    @Override
    public MessageRequest clone() {
        try {
            return (MessageRequest) super.clone();
        } catch (CloneNotSupportedException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }
}
