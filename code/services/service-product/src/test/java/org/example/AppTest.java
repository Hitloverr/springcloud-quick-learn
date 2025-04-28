package org.example;

import com.alibaba.cloud.nacos.discovery.NacosDiscoveryClient;
import com.alibaba.cloud.nacos.discovery.NacosServiceDiscovery;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.List;

/**
 * Unit test for simple App.
 */
@SpringBootTest
public class AppTest {
    @Autowired
    DiscoveryClient discoveryClient;

    @Autowired
    NacosServiceDiscovery nacosServiceDiscovery;

    @Test
    public void nacosDiscoveryTest() throws Exception {
        for (String service : nacosServiceDiscovery.getServices()) {
            System.out.println(service);
            for (ServiceInstance instance : nacosServiceDiscovery.getInstances(service)) {
                System.out.println(instance.getHost() + ":" + instance.getPort());
            }
        }
    }


    @Test
    public void discoveryClients() {
        for (String service : discoveryClient.getServices()) {
            System.out.println(service);
            List<ServiceInstance> instances = discoveryClient.getInstances(service);
            for (ServiceInstance instance : instances) {
                System.out.println(instance.getHost() + ":" + instance.getPort());
            }
        }
    }
}
