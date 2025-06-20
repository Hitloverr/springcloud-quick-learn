# 简介

分布式系统一站式解决方案



分布式基础--Nacos--OpenFeign--Sentinel--Gateway--Seata

分布式配套：日志系统 指标监控 链路追踪 消息处理。

# 分布式基础

![image-20250427231954676](.\image\image-20250427231954676.png)

## 单体到集群架构

单体架构：所有功能模块都在一个项目

1、项目打包

优点：开发部署

缺点：无法应对高并发

![image-20250427232229946](.\image\image-20250427232229946.png)



集群架构：副本、解决大并发

问题：1. 应用中的某个模块经常需要升级；2. 需要使用其他语言的项目。

![image-20250427232357024](.\image\image-20250427232357024.png)



## 集群到分布式架构

分布式：一个大型应用被拆分成很多小应用**分布部署**在各个机器；**侧重工作方式**

集群侧重的是**物理形态**。

![image-20250427232646107](.\image\image-20250427232646107.png)

不建议所有副本都在一个服务器上哦，不然有单点故障问题。

![image-20250427232710575](.\image\image-20250427232710575.png)



网关：请求路由，根据请求的URL找注册中心找服务。

微服务都有自己的数据库，分布式要解决分布式事务的问题。

![image-20250427232726478](.\image\image-20250427232726478.png)

## 创建微服务项目

- 创建微服务架构项目
- 引入SpringCloud、SpringCloudAlibaba相关依赖。【注意版本依赖】

版本选择：

- SpringBoot：3.3.4
- SpringCloud：2023.0.3
- SpringCloudAlibaba：2023.0.3.2
- Nacos：2.4.3
- Sentinel：1.8.8
- Seata：2.2.0





![image-20250428200033762](image\note.md)

![image-20250428200103703](image\image-20250428200103703.png)

# Nacos

注册中心，服务注册和服务发现的功能：微服务和节点列表。

**Nacos** /nɑ:kəʊs/ 是 Dynamic Naming and **Co**nfiguration **S**ervice的首字母简称，一个更易于构建云原生应用的动态服务发现、配置管理和服务管理平台。

https://nacos.io/zh-cn/docs/v2/quickstart/quick-start.html

- 下载安装包【2.4.3】
- 启动命令： startup.cmd -m standalone 【单机模式启动】

localhost:8848/nacos

## 服务注册

步骤1 

- 启动微服务 

- SpringBoot 微服务web项目启动

步骤2 

- 引入服务发现依赖 

**spring-cloud-starter-alibaba-nacos-discovery**



步骤3 

- 配置Nacos地址 

**spring.cloud.nacos.server-addr=127.0.0.1:8848**



步骤4 

- 查看注册中心效果 

- 访问 http://localhost:8848/nacos



步骤5 

- 集群模式启动测试 

- 单机情况下通过改变端口模拟微服务集群

## 服务发现

步骤1 开启服务发现功能  **@EnableDiscoveryClient**

步骤2  测试服务发现API  DiscoveryClient 【能获取到服务】

步骤3  测试服务发现API  NacosServiceDiscovery【Nacos特有的】



获取服务 与 实例。

```java
package com.atguigu.product;


import com.alibaba.cloud.nacos.discovery.NacosServiceDiscovery;
import com.alibaba.nacos.api.exception.NacosException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.List;

@SpringBootTest
public class DiscoveryTest {



    @Autowired
    DiscoveryClient discoveryClient;

    @Autowired
    NacosServiceDiscovery nacosServiceDiscovery;


    @Test
    void  nacosServiceDiscoveryTest() throws NacosException {
        for (String service : nacosServiceDiscovery.getServices()) {
            System.out.println("service = " + service);
            List<ServiceInstance> instances = nacosServiceDiscovery.getInstances(service);
            for (ServiceInstance instance : instances) {
                System.out.println("ip："+instance.getHost()+"；"+"port = " + instance.getPort());
            }
        }
    }

    @Test
    void discoveryClientTest(){
        for (String service : discoveryClient.getServices()) {
            System.out.println("service = " + service);
            //获取ip+port
            List<ServiceInstance> instances = discoveryClient.getInstances(service);
            for (ServiceInstance instance : instances) {
                System.out.println("ip："+instance.getHost()+"；"+"port = " + instance.getPort());
            }
        }
    }
}

```

## 编写微服务API

![image-20250428211749653](image\image-20250428211749653.png)

![image-20250428211832536](image\image-20250428211832536.png)

远程调用基本实现：

```java
  private Product getProductFromRemote(Long productId){
        //1、获取到商品服务所在的所有机器IP+port
        List<ServiceInstance> instances = discoveryClient.getInstances("service-product");
        ServiceInstance instance = instances.get(0);
        //远程URL
        String url = "http://"+instance.getHost() +":" +instance.getPort() +"/product/"+productId;
        log.info("远程请求：{}",url);
        //2、给远程发送请求
        Product product = restTemplate.getForObject(url, Product.class);
        return product;
    }
```



## 负载均衡

步骤1 引入负载均衡依赖 spring-cloud-starter-loadbalancer

步骤2 测试负载均衡API LoadBalancerClient

步骤3 测试远程调用 RestTemplate

步骤4 测试负载均衡调用 @LoadBalanced

![image-20250429194526758](image\image-20250429194526758.png)

## 配置中心

![image-20250429195040941](image\image-20250429195040941.png)

```java
<dependency>
<groupId>com.alibaba.cloud</groupId>
<artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>
```

```
spring.cloud.nacos.server-addr=127.0.0.1:8848
spring.config.import=nacos:service-order.properties
```

数据集： service-order.properties

```
order.timeout=10min
```

![image-20250429200553959](image\image-20250429200553959.png)

可以再Nacos那里配置。



使用步骤

• @Value(“${xx}”) 获取配置 + @RefreshScope 实现自动刷新

• @ConfigurationProperties 无感自动刷新

• NacosConfigManager 监听配置变化

```java
  // 项目启动就监听配置文件变化。
    @Bean
    ApplicationRunner applicationRunner(NacosConfigManager nacosConfigManager) {
        return args -> {
            ConfigService configService = nacosConfigManager.getConfigService();
            configService.addListener("service-order.properties", "DEFAULT_GROUP", new Listener() {
                @Override
                public Executor getExecutor() {
                    return Executors.newFixedThreadPool(2);
                }

                @Override
                public void receiveConfigInfo(String s) {
                    System.out.println("接收到的配置信息" + s);
                }
            });
        };
    }
```

## 经典面试题

思考： Nacos中的数据集 和 application.properties 有相同的 配置项，哪个生效？

![image-20250501192344666](image\image-20250501192344666.png)

```java
#spring.config.import=nacos:service-order.properties,nacos:common.properties
```

service-order 的比 common优先级高。

## 数据隔离-namespace

• 项目有多套环境：dev，test，prod

• 每个微服务，同一种配置，在每套环境的值都不一样。

如：database.properties，common.properties

• 项目可以通过切换环境，加载本环境的配置



区分环境、微服务、配置，按需加载配置

![image-20250501192846905](image\image-20250501192846905.png)

![image-20250501192916031](image\image-20250501192916031.png)



创建命名空间，同时新建配置的时候选择Group

每个group就是一个微服务。

## 数据隔离-动态切换环境



## 总结

![image-20250501194540335](image\image-20250501194540335.png)

# OpenFeign

**声明式** REST 客户端 vs **编程式** REST 客户端（RestTemplate）

注解驱动

• 指定远程地址：@FeignClient

• 指定请求方式：@GetMapping、@PostMapping、@DeleteMapping ... 

• 指定携带数据：@RequestHeader、@RequestParam、@RequestBody ... 

• 指定结果返回：响应模型

```java
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

![image-20250504143450598](image\image-20250504143450598.png)

自动负载均衡

## 第三方API

![image-20250504144425383](image\image-20250504144425383.png)

- 业务API：直接复制对方Controller签名即可

- 第三方API：根据接口文档确定请求如何发

![image-20250504145259478](D:\code\springcloud-quick-learn\note\image\image-20250504145259478.png)



## 进阶配置：日志

![image-20250504145414696](image\image-20250504145414696.png)

## 超时控制

避免服务雪崩

![image-20250504145736328](image\image-20250504145736328.png)

![image-20250504145806593](image\image-20250504145806593.png)

-  连接超时：第一步连接建立的时间。



## 重试机制

远程调用超时失败后，还可以进行多次尝试，如果某次成功返回ok，如果多次依然失败则结束调用，返回错误

![image-20250504150548882](image\image-20250504150548882.png)



## 拦截器

![image-20250504150839324](image\image-20250504150839324.png)

## Fallback兜底

![image-20250504151353620](image\image-20250504151353620.png)

远程调用失败就返回兜底结果。

## 总结

• 1. 熟练编写 OpenFeign 远程调用客户端

• 2. 熟练配置 OpenFeign 客户端属性

•  连接超时

•  读取超时

• ...... 

• 3. 掌握 拦截器 用法

• 4. 掌握 Fallback 兜底返回机制 及 用法

# Sentinel

服务保护（限流-熔断降级）

- 随着微服务的流行，服务和服务之间的稳定性变得越来越重要。Spring Cloud Alibaba Sentinel 以流量为切入点，从流量控制、流量路由、熔断降级、系统自适应过载保护、热点流量防护等多个维度保护服务的稳定性。

![image-20250504152304755](image\image-20250504152304755.png)

![image-20250504152321497](image\image-20250504152321497.png)

定义资源：

• 主流框架自动适配（Web Servlet、Dubbo、Spring Cloud、gRPC、Spring WebFlux、Reactor）；

所有**Web接口均为资源**

• 编程式：SphU API

• 声明式：@SentinelResource



定义规则：

• 流量控制（FlowRule）

• 熔断降级（DegradeRule）

• 系统保护（SystemRule）

• 来源访问控制（AuthorityRule）

• 热点参数（ParamFlowRule）



## 工作原理

![image-20250504152432284](image\image-20250504152432284.png)

## 整合使用

![image-20250504152550950](image\image-20250504152550950.png)

![image-20250504152958702](image\image-20250504152958702.png)

流控规则，QPS

## 异常处理

![image-20250504153121151](D:\code\springcloud-quick-learn\note\image\image-20250504153121151.png)

SphU.entry 如果触发流控，就会抛出异常。



远程调用添加流控：OpenFeign的兜底fallback，没有的话也是走到全局异常。



SphU硬编码：

```java
SphU.entry("resourceName"), 捕获bloackException
```

## 限流规则

![image-20250504154836110](image\image-20250504154836110.png)

![image-20250504154855905](image\image-20250504154855905.png)

![image-20250504154924622](image\image-20250504154924622.png)

• QPS：

   • 统计每秒请求数

• 并发线程数：

   • 统计并发线程数

![image-20250504155106111](image\image-20250504155106111.png)

链路策略：

![image-20250504155351377](image\image-20250504155351377.png)

关联策略：

- 实现优先写。希望写的量大了之后限制读。当写的阈值到达之后才会限制读

![image-20250504155614033](image\image-20250504155614033.png)

![image-20250504155140471](D:\code\springcloud-quick-learn\note\image\image-20250504155140471.png)

## 流控效果

![image-20250504155902871](image\image-20250504155902871.png)

1. 快速失败：抛出异常。
2. warm up：超高峰流量下，到达峰值qps下，需要period时间
3. 匀速排队：有排队，但是超时也会抛弃异常 IOException，Connection Reset。是漏通算法。

![image-20250504160217094](D:\code\springcloud-quick-learn\note\image\image-20250504160217094.png)

## 熔断降级

![image-20250504160753943](image\image-20250504160753943.png)

![image-20250504160814076](image\image-20250504160814076.png)

规则：（在sentinel的dashboard中配置）

![image-20250504162300041](image\image-20250504162300041.png)

![image-20250504162909796](image\image-20250504162909796.png)

另外还有异常数。



![image-20250504160911589](image\image-20250504160911589.png)

![image-20250504160937855](image\image-20250504160937855.png)

## 热点控制

更精细的限流：

![image-20250504163150068](image\image-20250504163150068.png)

![image-20250504163223205](image\image-20250504163223205.png)





需求一：![image-20250504163516291](image\image-20250504163516291.png)

如果不带这个参数，也是不会被流控的哦



需求二：

![image-20250504163746108](image\image-20250504163746108.png)



需求三：

![image-20250504163832823](image\image-20250504163832823.png)

## 补充，fallback与blockhandler

SentinelResource的blockHandler只能处理流控异常。

fallBack能处理自己的业务异常，fallbacl的函数里面传throwable

## 总结

规则持久化？结合Nacos、MySQL持久化。

# Gateway

![image-20250504202531532](image\image-20250504202531532.png)

![image-20250504202553570](image\image-20250504202553570.png)

1. 客户端发送 /api/order/** 转到 service-order

2. 客户端发送 /api/product/** 转到 service-product

3. 以上转发有负载均衡效果

## 原理

![image-20250504202628790](image\image-20250504202628790.png)



## 断言

![image-20250504204108185](image\image-20250504204108185.png)

只要有一个匹配到了，就不会继续往下走了。规则的顺序很重要。

断言工厂，PathRouterPredicateFactory



## 过滤器

![image-20250504205531394](image\image-20250504205531394.png)

![image-20250504205711074](image\image-20250504205711074.png)

## 总结

![image-20250504210942344](image\image-20250504210942344.png)

默认是不经过网关的，除非你Feign那里写的路径是Gateway的地址。

# Seata分布式事务

![image-20250504211137519](image\image-20250504211137519.png)

## 原理

![image-20250504211228106](image\image-20250504211228106.png)

## 二阶段提交协议

![image-20250504211622701](image\image-20250504211622701.png)

![image-20250504211643727](image\image-20250504211643727.png)

![image-20250504211752782](image\image-20250504211752782.png)

## 代码

```java
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-seata</artifactId>
        </dependency>
       
    // TM最大的方法那里开启全局事务。
    @GlobalTransactional
```

1. 启动Seata服务器。
2. 微服务里面引入Seata客户端，指定Seata的配置

```java
 service {
   #transaction service group mapping
   vgroupMapping.default_tx_group = "default" # 分组
   #only support when registry.type=file, please don't set multiple addresses
   default.grouplist = "127.0.0.1:8091" # Seata协调者的地址
   #degrade, current not support
   enableDegrade = false
   #disable seata
   disableGlobalTransaction = false
 }
```

![image-20250504214323981](image\image-20250504214323981.png)

## 四种事务模式

![image-20250504214614090](image\image-20250504214614090.png)

AT模式：自动模式。

XA模式：数据库的XA二阶段提交。

![image-20250504214532708](image\image-20250504214532708.png)

![image-20250504214558784](image\image-20250504214558784.png)

TCC模式：全手动：prepare commit rollback的代码都要自己写。适合夹杂了非数据库的事务逻辑。

![image-20250504214637641](image\image-20250504214637641.png)

Saga模式：最终一致性，消息。长事务。

- **正向流程**：按顺序执行本地事务。
  *示例*：用户下单→订单服务创建订单（事务1）→库存服务扣减库存（事务2）。
- **逆向补偿**：若某步骤失败，触发反向操作回滚。
  *示例*：库存不足时，订单服务取消订单（补偿事务1）。



- **优点**：无全局锁，提升系统吞吐量；适合跨服务长事务。
- **缺点**：补偿逻辑复杂，需幂等设计；最终一致性延迟可能影响业务。
