package com.jiang.annotaiondemo.user;

import java.lang.reflect.Constructor;

public class AnnotationProcessor {

    public static void init(Object obj) {
        if (!(obj instanceof User)) {
            throw new IllegalArgumentException("[" + obj.getClass().getSimpleName() + "] is not type of User");
        }
        Constructor<?>[] constructors = obj.getClass().getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            if (!constructor.isAnnotationPresent(UserMate.class)) {
                continue;
            }
            UserMate annotation = constructor.getAnnotation(UserMate.class);
            int id = annotation.id();
            int age = annotation.age();
            String name = annotation.name();
            User user = (User) obj;
            user.setId(id);
            user.setAge(age);
            user.setName(name);
        }
    }
}
