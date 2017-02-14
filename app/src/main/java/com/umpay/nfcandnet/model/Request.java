package com.umpay.nfcandnet.model;

import com.umpay.nfcandnet.model.IModel;

/**
 * @author younglee
 * @Description：
 * @DataTime 2017/2/14 19:09
 * @detailsDesc:
 */
public class Request implements IModel {
    private String funCode;

    public String getFunCode() {
        return funCode;
    }

    public void setFunCode(String funCode) {
        this.funCode = funCode;
    }
}
