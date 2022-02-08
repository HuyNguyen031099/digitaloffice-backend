package com.datn.doffice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Rbac {

    /**
     * Feature groups.
     *
     * @return the int[]
     */
    int[] permissionGroups() default {};

    /**
     * User types.
     *
     * @return the int[]
     */
    int[] roleTypes() default {};

    /**
     * Checks if is public.
     *
     * @return true, if is public
     */
    boolean isPublic() default false;

    boolean isPrivate() default false;
}
