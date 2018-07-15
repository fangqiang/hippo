package hippo.fq.hippo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import pers.fq.hippo.HippoClient;
import pers.fq.hippo.common.Utils;
import pers.fq.hippo.common.bo.Activity;
import pers.fq.hippo.common.parameter.ConditionItem;
import pers.fq.hippo.common.parameter.req.*;
import pers.fq.hippo.common.parameter.resp.CountResponse;
import pers.fq.hippo.common.parameter.resp.QueryResponse;
import pers.fq.hippo.common.parameter.resp.RemoveResponse;
import pers.fq.hippo.common.parameter.resp.SetResponse;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/7/14
 */
@Controller
@EnableAutoConfiguration
@SpringBootApplication
//@ImportResource(locations = {"classpath:shutter.xml","classpath*:module-metrics.xml"})
//@ImportResource(locations = {"classpath:shutter.xml"})
public class App {

//    @Autowired
//    Registry registry;
//
//    @PostConstruct
//    public void init(){
//        MonitorService.registry = registry;
//    }

    static AtomicLong cnt = new AtomicLong();

    static int thread = 1;
    static RateLimiter rateLimiter = RateLimiter.create(10000);

    public static void main(String[] args) throws Throwable {
        String zk = "10.211.55.12:2181";
        String zkPath = "/hippo";
        int port;

        if(args.length > 0){
            zk = args[0];
        }

//        Monitor.enable();

        SpringApplication.run(App.class, args);

        HippoClient hippoClient = new HippoClient(zk, zkPath,  8191);

        System.out.println("=======");
        for (int i = 0; i < 100; i++) {
            try {
                test(hippoClient, "aaa");
            }catch (Exception e){ }

            Utils.sleep(3000);
        }
    }

    public static void test(HippoClient hippoClient, String key) throws Exception {
        List<Activity> activities = new ArrayList<>(1000);
        for (int i = 0; i < 1000; i++) {
            activities.add(new Activity(System.currentTimeMillis()+i, mapData));
        }

        AppendRequest appendRequest = AppendRequest.getBuilder()
                .setKey(key)
                .addAllActivity(activities)
                .setLimit(1000)
                .setOldestTime(4L)
                .onlyIfExist(false)
                .build();

        hippoClient.send(appendRequest, 1000);

        ConditionItem a = new ConditionItem("a", "eventId", "login", 0, 0);
        ConditionItem b = new ConditionItem("b", "sequenceId", "1514369486931000X116A37378860135", 0, 0);
        ConditionItem c = new ConditionItem("c", "serviceType", "我自横刀香茗月", 0,0);

        // count
        CountRequest countRequest = CountRequest.getBuilder()
                .setKey(key)
                .setLimit(1000)
                .setTimeRange(4L, Long.MAX_VALUE)
                .setFilter("a&&(b)&&c", new ArrayList<>(Arrays.asList(a,b,c)))
                .build();

        CountResponse countResponse = hippoClient.send(countRequest, 1000);
        System.out.println("count: "+countResponse.getCountVal());

        Utils.sleep(500);
        // set
        SetRequest setRequest = SetRequest.getBuilder()
                .setKey(key)
                .setLimit(1000)
                .setTimeRange(4L, Long.MAX_VALUE)
                .setFilter("a&&(b)&&c", new ArrayList<>(Arrays.asList(a, b, c)))
                .setCalColumn("eventId")
                .build();

        SetResponse setResponse = hippoClient.send(setRequest, 1000);
        System.out.println("set: " + setResponse.getSets());

        QueryRequest queryRequest = QueryRequest.getBuilder()
                .setKey("aaa")
                .setLimit(3)
                .setTimeRange(4L, Long.MAX_VALUE)
                .setFilter("a&&(b)&&c", new ArrayList<>(Arrays.asList(a,b,c)))
                .setColumns(new HashSet<>(Arrays.asList("eventIc","eventIe","eventId","sequenceId","serviceType","partnerCode","eventType", "idNumber","secretKey","asscrev17","appName","eventOccurTime","secondIndustryType","simulation_uuid","firstIndustryType")))
                .setCompress(true)
                .build();

        QueryResponse queryResponse = hippoClient.send(queryRequest, 1000);
        System.out.println("query: "+queryResponse.getActivities().size());
        for (Activity activity : queryResponse.getActivities()) {
            System.out.println(activity.map);
        }

        RemoveRequest removeRequest = RemoveRequest.getBuilter()
                .setKey("aaa")
                .build();

        RemoveResponse removeResponse = hippoClient.send(removeRequest, 1000);

        countRequest = CountRequest.getBuilder()
                .setKey(key)
                .setLimit(1000)
                .setTimeRange(4L, Long.MAX_VALUE)
                .setFilter("a&&(b)&&c", new ArrayList<>(Arrays.asList(a,b,c)))
                .build();

        countResponse = hippoClient.send(countRequest, 1000);
        System.out.println("count: "+countResponse.getCountVal());
    }

    static String  string="{\n" +
            "  \"abc\": \"18719212121\",\n" +
            "  \"appName\": \"login_and\",\n" +
            "  \"asscrev17\": 11,\n" +
            "  \"bankCardNumber\": \"b0001\",\n" +
            "  \"creditCard\": \"c0001\",\n" +
            "  \"def\": \"1009921221\",\n" +
            "  \"display\": 0,\n" +
            "  \"eventId\": \"login\",\n" +
            "  \"eventOccurTime\": 1514369486931,\n" +
            "  \"eventType\": \"Loan\",\n" +
            "  \"firstIndustryType\": \"Imbank\",\n" +
            "  \"idNumber\": \"i0001\",\n" +
            "  \"idNumberScore\": 0,\n" +
            "  \"partnerCode\": \"jackoa\",\n" +
            "  \"phoneNumber\": \"p0001\",\n" +
            "  \"scoreServiceCallSuccessful\": true,\n" +
            "  \"secondIndustryType\": \"Consumerfinance\",\n" +
            "  \"secretKey\": \"c04e2ad944e047478e003f7dc5f14bae\",\n" +
            "  \"sequenceId\": \"1514369486931000X116A37378860135\",\n" +
            "  \"serviceType\": \"我自横刀香茗月\",\n" +
            "  \"abc1\": \"18719212121\",\n" +
            "  \"appNa1me\": \"login_and\",\n" +
            "  \"bankC1ardNumber\": \"b0001\",\n" +
            "  \"cre1ditCard\": \"c0001\",\n" +
            "  \"def1\": \"1009921221\",\n" +
            "  \"dis1play\": 0,\n" +
            "  \"even1tId\": \"login\",\n" +
            "  \"event1OccurTime\": 1514369486931,\n" +
            "  \"eventT1ype\": \"Loan\",\n" +
            "  \"firstIn1dustryType\": \"Imbank\",\n" +
            "  \"idNumber1\": \"i0001\",\n" +
            "  \"partnerCo1de\": \"jackoa\",\n" +
            "  \"phoneNumbe1r\": \"p0001\",\n" +
            "  \"scoreServic1eCallSuccessful\": true,\n" +
            "  \"secondIndus1tryType\": \"Consumerfinance\",\n" +
            "  \"secretKe1y\": \"c04e2ad944e047478e003f7dc5f14bae\",\n" +
            "  \"sequenc1eId\": \"1514369486931000X116A37378860135\",\n" +
            "  \"serviceTy1pe\": \"我自横刀香茗月\",\n" +
            "  \"simulation_uuid\": \"see\"\n" +
            "}";
    static JSONObject jsonObject = JSON.parseObject(string);

    static HashMap<String, String> mapData = new HashMap<>();
    static {
        jsonObject.forEach((k, v) -> {
            mapData.put(k, v.toString());
        });
    }
}
