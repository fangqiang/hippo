package pers.fq.hippo.common.parameter.req;

import org.apache.commons.lang3.StringUtils;
import pers.fq.hippo.common.Assert;
import pers.fq.hippo.common.bo.Activity;
import pers.fq.hippo.common.bo.ActivityHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/11/17
 */
public class RemoveRequest {

    String key;

    public RemoveRequest(){

    }

    public String getKey() {
        return key;
    }

    public static RemoveRequestBuilder getBuilter() {
        return new RemoveRequestBuilder();
    }

    public static final class RemoveRequestBuilder {

        String key;

        private RemoveRequestBuilder() {
        }

        public RemoveRequestBuilder setKey(String key) {
            this.key = key;
            return this;
        }

        public RemoveRequest build() {
            Assert.check(StringUtils.isNotBlank(key), "key is empty");

            RemoveRequest removeRequest = new RemoveRequest();
            removeRequest.key = this.key;
            return removeRequest;
        }
    }
}
