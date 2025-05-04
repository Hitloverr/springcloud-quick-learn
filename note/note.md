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
