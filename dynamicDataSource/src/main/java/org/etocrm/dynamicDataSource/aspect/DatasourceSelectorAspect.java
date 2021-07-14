package org.etocrm.dynamicDataSource.aspect;

import org.aspectj.lang.JoinPoint;

/**
 * @Author chengrong.yang
 * @date 2020/8/29 11:57
 */
//@Aspect
//@Component
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DatasourceSelectorAspect {

//    @Pointcut("execution(public * org.etocrm.*.service.*.*(..))")
    public void datasourcePointcut() {
    }

    /**
     * 前置操作，拦截具体请求，获取header里的数据源id，设置线程变量里，用于后续切换数据源
     */
//    @Before("datasourcePointcut()")
    public void doBefore(JoinPoint joinPoint) {
//        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//        if (requestAttributes != null) {
//            ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
//            HttpServletRequest request = attributes.getRequest();
//            String configIdInHeader = request.getHeader("DatasourceID");
//            if (StringUtils.hasText(configIdInHeader)) {
//                long configId = Long.parseLong(configIdInHeader);
//                DynamicDataSource.setDataSource(configId);
//            }
//        }
    }

//    @After("datasourcePointcut()")
    public void doAfter(JoinPoint joinPoint) {
//        DynamicDataSource.setDataSource(-1L);
    }

}
