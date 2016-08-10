package com.getknowledge.modules.userInfo.blocker.info;


import com.getknowledge.modules.userInfo.blocker.BlockerTypes;

import java.util.Calendar;

public class BlockerInfo {

    private String ip;
    private Integer count;
    private Calendar startDate;
    private BlockerTypes blockerTypes;

    public BlockerInfo() {
    }

    public BlockerInfo(String ip, Integer count, Calendar startDate, BlockerTypes blockerTypes) {
        this.ip = ip;
        this.count = count;
        this.startDate = startDate;
        this.blockerTypes = blockerTypes;
    }

    public BlockerTypes getBlockerTypes() {
        return blockerTypes;
    }

    public void setBlockerTypes(BlockerTypes blockerTypes) {
        this.blockerTypes = blockerTypes;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }
}
