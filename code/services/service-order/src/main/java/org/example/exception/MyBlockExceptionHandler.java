package org.example.exception;


import com.alibaba.csp.sentinel.adapter.spring.webmvc_v6x.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.common.R;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;

// 处理没处理的Sentinel限流异常。最后一道防线
@Component
public class MyBlockExceptionHandler implements BlockExceptionHandler {
    private ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse response, String resourceName, BlockException e) throws Exception {
        response.setContentType("application/json;charset=utf-8");

        PrintWriter writer = response.getWriter();


        R error = R.error(500, resourceName + " 被Sentinel限制了，原因：" + e.getClass());

        String json = objectMapper.writeValueAsString(error);
        writer.write(json);

        writer.flush();
        writer.close();
    }
}
