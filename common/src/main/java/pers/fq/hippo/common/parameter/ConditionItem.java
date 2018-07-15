package pers.fq.hippo.common.parameter;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/11/17
 */
public class ConditionItem {

    public String name;
    public String leftColumn;
    public String rightValue;
    public int leftType;
    public int operatorType;

    /**
     *  为了使用kryo序列化
     */
    public ConditionItem(){}

    public ConditionItem(String name, String leftColumn, String rightValue, int leftType, int operatorType) {
        this.name = name;
        this.leftColumn = leftColumn;
        this.rightValue = rightValue;
        this.leftType = leftType;
        this.operatorType = operatorType;
    }
}
