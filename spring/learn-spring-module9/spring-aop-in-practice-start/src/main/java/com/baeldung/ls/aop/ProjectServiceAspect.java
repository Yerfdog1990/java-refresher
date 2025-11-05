package com.baeldung.ls.aop;

import com.baeldung.ls.persistence.model.Project;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Aspect
public class ProjectServiceAspect {
    private static final Logger LOG  = LoggerFactory.getLogger(ProjectServiceAspect.class);

    @Before("execution(* com.baeldung.ls.service.impl.ProjectServiceImpl.findById(*))")
    public void before(JoinPoint jointPoint) {
        LOG.info("Searching Project with Id {}", jointPoint.getArgs()[0]);
    }
    @AfterReturning(pointcut = "execution(*..Optional<*..Project>  *..service..findById(*))", returning = "project")
    public void afterReturningProject(Optional<Project> project) {
        LOG.info("project found: {}", project.orElse(null));
    }

    @After("within(com.baeldung.ls.service.impl.ProjectServiceImpl)")
    public void afterAllMethodsOfProjectServiceImpl(JoinPoint joinPoint) {
        LOG.info("After Invoking the method: {} ", joinPoint.getSignature().getName());
    }

    @Around("execution(* com.baeldung.ls.service.impl.ProjectServiceImpl.save(*))")
    public Object aroundSave(ProceedingJoinPoint joinPoint) {
        Object val = joinPoint.getArgs()[0];
        try {
            LOG.info("saving project : {}", val);
            val = joinPoint.proceed();
            LOG.info("project saved successfully !!");
        } catch (Throwable e) {
            LOG.error("error while saving project: ", e);
        }
        return val;
    }

}
