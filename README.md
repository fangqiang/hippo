# 介绍
- 定义：这是一个基于java的分布式缓存，用于缓存**最近**的数据集（如最近1000条数据，最近10分钟的数据），并在服务端完成基本的**过滤、计算**。常用于风控领域。
   - 常见需求：计算某个账号最近1小时登入失败的次数？
   - 常见需求：计算某个账号最近10次登入关联多少个不同的手机号？

# 使用场景
本项目的诞生是为了解决典型的风控场景问题，风控场景及问题
- 依赖“最近的数据集”
   - 面临的挑战：目前没有成熟的缓存组件可以很好的支持 “最近1小时”、“最近1000条数” 等数据集。如果使用普通kv缓存，更新时需要**写前读**，这对应性能的消耗是非常大的
- 为了保证风控指标配置的灵活性，需要预存很多字段，但每次计算指标时只依赖其中部分数据
   - 面临的挑战：每次取回客户端的数据，很多都是不需要的。假如总数据量有1000行、50列。但实际上客户端只需要前100行和其中的10列；对于常见计算类型（次数，关联个数等）甚至可以直接在服务端完成计算，以减小网络传输性能损耗。

本项目如何解决上面问题
- 服务端支持按**时间范围、数据条数、数据size** 3个维度进行淘汰。直接在服务端完成更新，无需**写前读**
- 服务端支持数据过滤，**按行过滤，按列过滤，简单类型计算**。保证没有无效数据返回给客户端
- 数据存储结构： SortMap<Time, Map<String,String>>
![image](https://raw.githubusercontent.com/fangqiang/hippo/master/doc/store_struct.jpg)

# 部署方式
每台服务器完全对等，启动时将信息上报zk。客户端通过zk获取集群信息

# 项目结构
##### store模块：缓存实现，参考memcached的slab分级内存管理，数据放在堆外，索引放在堆内
##### transporter模块：rpc调用层实现，底层使用netty4.x
##### hippo-server模块：服务端实现
##### hippo-client模块：客户端实现
##### common模块：客户端，服务端公共库

# 分布式 
### 分布式方案
一致性哈希 + 虚拟节点
### 副本
无副本
# 例子

hippo-server(服务端), hippo-client（客户端），的代码可直接运行

##### append操作

```java
AppendRequest appendRequest = AppendRequest.getBuilder()
        .setKey("key")
        .addAllActivity(activities)     // append 的数据
        .setLimit(1000)                 // 最多保存1000条数据
        .setOldestTime(1511234512345L)  // 小于该时间的数据都自动过期
        .onlyIfExist(true)             // 当key存在时才append，否则放弃append
        .build();
```

##### remove操作

```java
RemoveRequest removeRequest = RemoveRequest.getBuilter()
        .setKey("aaa")
        .build();
```

##### query操作

```java
ConditionItem a = new ConditionItem("a", "name", "张三", LeftType.STRING, StringOper.EQ);
ConditionItem b = new ConditionItem("b", "age", "18", LeftType.STRING, StringOper.EQ);
ConditionItem c = new ConditionItem("c", "classId", "2", LeftType.STRING, StringOper.EQ);


QueryRequest queryRequest = QueryRequest.getBuilder()
        .setKey("aaa")
        .setLimit(1000)                         // 最多返回1000条
        .setTimeRange(111111111L, 2222222222L)  // 返回的数据时间必须在这个范围内
        .setFilter("a||(b)||c", new ArrayList<>(Arrays.asList(a,b,c)))    // 过滤数据
        .setColumns(new HashSet<>(Arrays.asList("ipAddress","accountId")))          // 指定返回列
        .setCompress(true)                      // 返回时是否支持压缩
        .build();
```

##### 查询并求次数

```java
ConditionItem a = new ConditionItem("a", "name", "张三", LeftType.STRING, StringOper.EQ);
ConditionItem b = new ConditionItem("b", "age", "18", LeftType.STRING, StringOper.EQ);
ConditionItem c = new ConditionItem("c", "classId", "2", LeftType.STRING, StringOper.EQ);


CountRequest countRequest = CountRequest.getBuilder()
        .setKey(key)
        .setLimit(1000) // 最多查询1000条数据
        .setTimeRange(111111111L, 2222222222L)  // 查询的数据时间必须在这个范围内
        .setFilter("a||(b)||c", new ArrayList<>(Arrays.asList(a,b,c)))    // 过滤数据
        .build();
```

##### 查询并求关联个数

```java
ConditionItem a = new ConditionItem("a", "name", "张三", LeftType.STRING, StringOper.EQ);
ConditionItem b = new ConditionItem("b", "age", "18", LeftType.STRING, StringOper.EQ);
ConditionItem c = new ConditionItem("c", "classId", "2", LeftType.STRING, StringOper.EQ);


SetRequest setRequest = SetRequest.getBuilder()
        .setKey(key)
        .setLimit(1000) // 最多查询1000条数据
        .setTimeRange(111111111L, 2222222222L)  // 返回的数据时间必须在这个范围内
        .setFilter("a||(b)||c", new ArrayList<>(Arrays.asList(a,b,c)))    // 过滤数据
        .setCalColumn("eventId") // 计算的字段
        .build();
```



# 性能测试
## 场景1：网络测试（没有缓存操作）

#### 单台客户端
- 单台服务器（虚拟机）：4c8g, 1000Mb/s网卡
- 单台客户端（虚拟机）：4c8g，1000Mb/s网卡，48个线程
- 请求数据大小：250字节
- 返回数据大小：1000个字节
- 服务端cpu使用：70%
- 客户端cpu使用：200%
- 网络：峰值450Mb

- 服务端tps
- ![image](https://raw.githubusercontent.com/fangqiang/hippo/master/doc/server_tps_1.png)


#### 多台客户端
- 单台服务器（虚拟机）：4c8g, 1000Mb/s网卡
- 2台客户端（虚拟机）：4c8g，1000Mb/s网卡，48个线程
- 请求数据大小：250字节
- 返回数据大小：1000个字节
- 服务端cpu使用：150%
- 客户端cpu使用：200%
- 网络：峰值850Mb （网络到达瓶颈）

- 服务端tps
- ![image](https://raw.githubusercontent.com/fangqiang/hippo/master/doc/server_tps_2.png)
- 客户端rt
- ![image](https://raw.githubusercontent.com/fangqiang/hippo/master/doc/client_rt.png)



## 场景2：网络+缓存测试 (读写混合1：1)
- 单台服务器（物理机）：40c，128g，1000Mb/s网卡
- 2台客户端（虚拟机）：4c，8g，1000Mb/s网卡，48个线程
- append数据大小：1000字节
- get数据大小：1000个字节
- 服务端cpu使用：400%
- 客户端cpu使用：200%
- 网络：峰值850Mb（网络到达瓶颈）

- 客户端append操作tps
- ![image](https://raw.githubusercontent.com/fangqiang/hippo/master/doc/client_append_tps.png)
- 客户端get操作tps
- ![image](https://raw.githubusercontent.com/fangqiang/hippo/master/doc/client_get_tps.png)
- 客户端append操作rt
- ![image](https://raw.githubusercontent.com/fangqiang/hippo/master/doc/client_append_rt.png)
- 客户端get操作rt
- ![image](https://raw.githubusercontent.com/fangqiang/hippo/master/doc/client_get_rt.png)
- 服务端append操作rt
- ![image](https://raw.githubusercontent.com/fangqiang/hippo/master/doc/server_append_rt.png)
- 服务端get操作rt
- ![image](https://raw.githubusercontent.com/fangqiang/hippo/master/doc/server_get_rt.png)
