package org.example.aop;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Interceptor
@Idempotent // just the annotation, no params
public class IdempotencyInterceptor {

    @AroundInvoke
    public Object enforceIdempotency(InvocationContext ctx) throws Exception {
        Idempotent idempotent = (Idempotent) ctx.getTarget();
        String level = idempotent.level();
        System.out.println("IdempotencyInterceptor.Enforcing");
        Object result = ctx.proceed();
        System.out.println("IdempotencyInterceptor.Enforced");
        return result;
    }
}
