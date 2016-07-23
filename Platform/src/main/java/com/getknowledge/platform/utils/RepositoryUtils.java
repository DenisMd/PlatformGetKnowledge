package com.getknowledge.platform.utils;


import com.getknowledge.platform.annotations.ModelView;
import com.getknowledge.platform.annotations.ViewType;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RepositoryUtils {

    public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            fields = getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

    public static void prepareViewFields(List<ViewType> viewTypes  , AbstractEntity entity){
        for (Field field : getAllFields(new LinkedList<>(),entity.getClass())) {
            if (viewTypes != null) {
                boolean viewAvail = false;
                for (ModelView modelView : field.getAnnotationsByType(ModelView.class)) {
                    if (!Collections.disjoint(Arrays.asList(modelView.type()), viewTypes)) {
                        viewAvail = true;
                        break;
                    }
                }
                if (!viewAvail) {
                    try {
                        field.setAccessible(true);
                        field.set(entity, null);
                    } catch (IllegalAccessException | IllegalArgumentException e) {
                        //Невозможно сбросить примитивный тип в null
                    }
                }
            }
        }
    }

}
