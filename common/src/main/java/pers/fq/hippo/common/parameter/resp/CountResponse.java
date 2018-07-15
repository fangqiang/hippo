package pers.fq.hippo.common.parameter.resp;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/11/29
 */
public class CountResponse {

    int countVal;

    public CountResponse() {

    }

    public CountResponse(int countVal) {
        this.countVal = countVal;
    }

    public int getCountVal() {
        return countVal;
    }

    public void setCountVal(int countVal) {
        this.countVal = countVal;
    }
}
