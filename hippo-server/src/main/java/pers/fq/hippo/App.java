package pers.fq.hippo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import pers.fq.hippo.common.Assert;

/**
 * @Description:
 * @author: fang
 * @date: Created by on 18/7/14
 */

@Controller
@EnableAutoConfiguration
@SpringBootApplication
//@ImportResource(locations = {"classpath:shutter.xml","classpath*:module-metrics.xml"})
public class App {

//    @Autowired
//    Registry registry;
//
//    @PostConstruct
//    public void init(){
//        MonitorService.registry = registry;
//    }

    public static void main(String[] args) throws Throwable {
//        Monitor.enable();

        String port = System.getProperty("port");
        Assert.check(port != null, "port is not set");


        String zk = System.getProperty("zk");
        Assert.check(zk != null, "zk is not set");

        String zkPath = System.getProperty("zkPath");
        Assert.check(zkPath != null, "zkPath is not set");

        SpringApplication.run(App.class, args);

        new HippoServer().run(zk, zkPath, Integer.parseInt(port));
    }
}
