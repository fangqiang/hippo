package pers.fq.hippo.common.parameter.resp;

import java.util.HashSet;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/11/29
 */
public class SetResponse {

    HashSet<String> sets;

    public SetResponse() {

    }

    public SetResponse(HashSet<String> sets) {
        this.sets = sets;
    }

    public void setSets(HashSet<String> sets) {
        this.sets = sets;
    }

    public HashSet<String> getSets() {
        return sets;
    }
}
