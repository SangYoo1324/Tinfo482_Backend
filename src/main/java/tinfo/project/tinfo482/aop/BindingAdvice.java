package tinfo.project.tinfo482.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
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

       // BindingResult is paired with the @Valid check annotation
       for(Object bindingResult : args){
           if(bindingResult instanceof BindingResult){

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

//    @Around("execution(* *(..))")
//    public void executionTimer(){
//
//    }


}
