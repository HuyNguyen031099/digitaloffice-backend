/*******************************************************************************
 * (C) Copyright Global CyberSoft (GCS) 2019. All rights reserved. Proprietary
 * and confidential.
 ******************************************************************************/
package com.datn.doffice.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

/**
 * The Interface Api.
 * Extend from RestController and RequestMapping of Spring
 * framework.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@RestController
@RequestMapping
@CrossOrigin(value = "*")
public @interface Api {

  @AliasFor(attribute = "path", annotation = RequestMapping.class)
  String path();

}
