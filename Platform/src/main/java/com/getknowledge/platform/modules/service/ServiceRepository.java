package com.getknowledge.platform.modules.service;

import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.annotations.Task;
import com.getknowledge.platform.base.repositories.TransientRepository;
import com.getknowledge.platform.initializer.InitApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Repository("ServiceRepository")
public class ServiceRepository extends TransientRepository<Service> {

    private List<Method> getMethodsAnnotatedWith(final Class<?> type, final Class<? extends Annotation> annotation) {
        final List<Method> methods = new ArrayList<Method>();
        Class<?> klass = type;
        while (klass != Object.class) { // need to iterated thought hierarchy in order to retrieve methods from above the current instance
            // iterate though the list of methods declared in the class represented by klass variable, and add those annotated with the specified annotation
            final List<Method> allMethods = new ArrayList<Method>(Arrays.asList(klass.getDeclaredMethods()));
            for (final Method method : allMethods) {
                if (method.isAnnotationPresent(annotation)) {
                    Annotation annotInstance = method.getAnnotation(annotation);
                    // TODO process annotInstance
                    methods.add(method);
                }
            }
            // move to the upper class in the hierarchy in search for more methods
            klass = klass.getSuperclass();
        }
        return methods;
    }

    @Override
    protected Class<Service> getClassEntity() {
        return Service.class;
    }

    @Autowired
    InitApplication application;

    @PostConstruct
    public void ServiceRepository(){
        for (Map.Entry<String,Object> entry : application.getServices().entrySet()) {
            Service service = new Service();
            service.setName(entry.getKey());
            Object value = entry.getValue();

            for (Method method : getMethodsAnnotatedWith(value.getClass(), Action.class) ) {
                Action action = method.getAnnotation(Action.class);
                Service.ActionInfo actionInfo = new Service.ActionInfo();
                actionInfo.setType("Action");
                actionInfo.setName(action.name());
                actionInfo.setMandatoryFields(Arrays.asList(action.mandatoryFields()));
                service.getActionInfos().add(actionInfo);
            }
            for (Method method : getMethodsAnnotatedWith(value.getClass(), ActionWithFile.class) ) {
                ActionWithFile action = method.getAnnotation(ActionWithFile.class);
                Service.ActionInfo actionInfo = new Service.ActionInfo();
                actionInfo.setType("Action with file");
                actionInfo.setName(action.name());
                actionInfo.setMandatoryFields(Arrays.asList(action.mandatoryFields()));
                service.getActionInfos().add(actionInfo);
            }

            for (Method method : getMethodsAnnotatedWith(value.getClass(), Task.class) ) {
                Task task = method.getAnnotation(Task.class);
                Service.ActionInfo actionInfo = new Service.ActionInfo();
                actionInfo.setType("Task");
                actionInfo.setName(task.name());
                service.getActionInfos().add(actionInfo);
            }

            super.list.add(service);
        }
    }
}
