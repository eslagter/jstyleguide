package org.jstyleguide.mvc.controller;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by kees on 19-11-15.
 */
public abstract class AbstractController {

    @Value("${jstyleguide.templates.dir:jstyleguide}")
    protected String templateDirectory = "jstyleguide";
}
