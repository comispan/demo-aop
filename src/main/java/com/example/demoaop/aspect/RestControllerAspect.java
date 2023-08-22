package com.example.demoaop.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;

@Aspect
@Component
@Slf4j
public class RestControllerAspect {

  @Autowired
  private ObjectMapper mapper;

  @Pointcut("within(com.example.demoaop.controller..*) " +
              "&& @annotation(org.springframework.web.bind.annotation.RequestMapping)")
  public void pointcut() {

  }

  @Before("pointcut()")
  public void logMethod(JoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    RequestMapping mapping = signature.getMethod().getAnnotation(RequestMapping.class);

    Map<String, Object> parameters = getParameters(joinPoint);

    try {
      log.info("==> path(s): {}, method(s): {}, arguments: {} ",
          mapping.path(), mapping.method(), mapper.writeValueAsString(parameters));
    } catch (JsonProcessingException e) {
      log.error("Error while converting", e);
    }
  }

  @AfterReturning(pointcut = "pointcut()", returning = "entity")
  public void logMethodAfter(JoinPoint joinPoint, ResponseEntity<?> entity) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    RequestMapping mapping = signature.getMethod().getAnnotation(RequestMapping.class);

    try {
      log.info("<== path(s): {}, method(s): {}, retuning: {}",
          mapping.path(), mapping.method(), mapper.writeValueAsString(entity));
    } catch (JsonProcessingException e) {
      log.error("Error while converting", e);
    }
  }

  @Around("@annotation(com.example.demoaop.annotation.LogExecutionTime)")
  public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    final StopWatch stopWatch = new StopWatch();

    stopWatch.start();

    Object proceed = joinPoint.proceed();

    stopWatch.stop();

    log.info("\"{}\" executed in {} ms", joinPoint.getSignature(), stopWatch.getTotalTimeMillis());

    return proceed;
  }

  private Map<String, Object> getParameters(JoinPoint joinPoint) {
    CodeSignature signature = (CodeSignature) joinPoint.getSignature();

    HashMap<String, Object> map = new HashMap<>();

    String[] parameterNames = signature.getParameterNames();

    for (int i = 0; i < parameterNames.length; i++) {
      map.put(parameterNames[i], joinPoint.getArgs()[i]);
    }

    return map;
  }

}
