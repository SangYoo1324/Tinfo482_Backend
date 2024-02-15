package tinfo.project.tinfo482.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@Component
@Aspect
@Slf4j
public class BindingAdvice {


    // 함수 앞 제어 예시 : 특정 인풋이 안들어왔으면 강제로 넣어주고 실행
    // 함수 뒤 제어 예시 : 응답만 관리
    @Around("execution(* tinfo.project.tinfo482.functionalities..*Controller.*(..))")
    public Object validCheck(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
            String type = proceedingJoinPoint.getSignature().getDeclaringTypeName();
            String method = proceedingJoinPoint.getSignature().getName();

            // method execution

       Object[] args =  proceedingJoinPoint.getArgs();
        log.info(String.valueOf(args.length));

       // BindingResult is paired with the @Valid check annotation
       for(Object bindingResult : args){
           if(bindingResult instanceof BindingResult){
                log.info(bindingResult.toString());
               if(((BindingResult) bindingResult).hasErrors()){
                   Map<String,String> erroLogs = new HashMap<>();
                   for(FieldError err: ((BindingResult) bindingResult).getFieldErrors()){
                        erroLogs.put(err.getField(), err.getDefaultMessage());
                   }
               }

           }
        }
        try {
            // this is required or method will not proceed
            System.out.println("type: "+ type);
            Object result = proceedingJoinPoint.proceed();
//            log.debug("method: "+ method);
            return result;
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }

    }

    @Around("execution( * tinfo.project.tinfo482.functionalities.redis.redisTest.*Service.*(..))")
    public Object executionTimerTest(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;

        } finally {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime-startTime;
            log.info("AOP::::: "+ joinPoint.getSignature()+ " executed in "+executionTime);
        }


    }

    @Around("execution(* tinfo.project.tinfo482.service..*Service.*(..))")
    public Object ItemServiceExecutionTimer(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        try {
            Object result = proceedingJoinPoint.proceed();
            return result;
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;

        } finally {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime-startTime;
            log.info("AOP::::: "+ proceedingJoinPoint.getSignature()+ " executed in "+executionTime);
        }
    }




}
