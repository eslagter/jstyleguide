package org.jstyleguide.beans;

import java.lang.String;
import java.util.List;

/**
 * Created by kees on 19-11-15.
 */
public class AtomicDesignComponent {

    String name;
    String path;
    String group;
    String type;

    String option;
    List<String> options;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public static AtomicDesignComponent of(String path) {
        AtomicDesignComponent component = new AtomicDesignComponent();
        component.setName(stripExtension(path.substring(path.lastIndexOf("/")+1)));
        component.setPath(stripExtension(path.substring(path.indexOf("/templates"))).replace("/templates/",""));
        component.setType(component.getPath().split("/")[0]);
        component.setGroup(component.getPath().split("/")[1]);
        return component;
    }

    private static String stripExtension(String fileName) {
        return fileName.substring(0, fileName.indexOf("."));
    }
}
