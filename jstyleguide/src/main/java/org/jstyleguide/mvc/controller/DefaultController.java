package org.jstyleguide.mvc.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.jstyleguide.beans.AtomicDesignComponent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;
import java.lang.Object;
import java.lang.String;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kees on 19-11-15.
 */
@Controller
@RequestMapping("/")
public class DefaultController extends AbstractController {

    @RequestMapping(method = RequestMethod.GET)
    public String showDefault(Model model, @RequestParam(required=false) String path, @RequestParam(required = false) String content) throws URISyntaxException, IOException {

        HashMap<String, List<AtomicDesignComponent>> atoms = getComponents("/templates/atoms/");
        model.addAttribute("atoms"    , atoms);
        model.addAttribute("molecules", getComponents("/templates/molecules/"));
        model.addAttribute("organisms", getComponents("/templates/organisms/"));
        model.addAttribute("templates", getComponents("/templates/templates/"));

        if (path == null) {
            path = atoms.values().iterator().next().get(0).getPath();
        }


        model.addAttribute("component", resolveComponent(path, content));

        return templateDirectory + "/index";
    }

    private AtomicDesignComponent resolveComponent(String path, String content) throws URISyntaxException, IOException {

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        for (Resource resource : resolver.getResources("/templates/"+path+".html")) {
            AtomicDesignComponent atomicDesignComponent = AtomicDesignComponent.of(resource.getURI().toString());
            if (content != null && content.length()>1) {
                atomicDesignComponent.setOption(content);
            }
            atomicDesignComponent.setOptions(resolveContent(path));
            return atomicDesignComponent;
        }
        return null;
    }

    private ArrayList<String> resolveContent(String path) throws URISyntaxException, IOException {
        ArrayList<String> content = new ArrayList<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        for (Resource resource : resolver.getResources("/content/"+path+"-*.json")) {
            String filename = resource.getFilename();
            content.add(filename.substring(filename.indexOf("-")+1, filename.indexOf(".")));
        }
        return content;
    }

    private HashMap<String, List<AtomicDesignComponent>> getComponents(String directory) throws URISyntaxException, IOException {
        HashMap<String, List<AtomicDesignComponent>> components = new HashMap<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        for (Resource resource : resolver.getResources(directory + "**/*.html")) {
            InputStream is = resource.getInputStream();
            String path = resource.getURI().toString();
            String dir = path.substring(path.indexOf(directory)+directory.length(), path.indexOf(resource.getFilename())-1);
            if (!components.containsKey(dir)) {
                components.put(dir, new ArrayList<>());
            }
            components.get(dir).add(AtomicDesignComponent.of(path));
        }
        return components;
    }

    @RequestMapping(value = "component", method = RequestMethod.GET)
    public String showComponent(@RequestParam String path, @RequestParam(required = false) String content, Model model) throws UnsupportedEncodingException {
        model.addAllAttributes(getContent(path, content));
        return path;
    }

    private Map<String, Object> getContent(String path, String content) throws UnsupportedEncodingException {
        String contentPath = getContentPath(path, content);
        try {
            InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(contentPath);
            InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream, "UTF-8");
            if (inputStreamReader != null) {
                JsonReader reader = new JsonReader(inputStreamReader);
                return new Gson().fromJson(reader, new TypeToken<HashMap<String, Object>>() {}.getType());
            }
        } catch (Throwable t) {
            System.out.println("ERROR: " +t);
        }

        return new HashMap();
    }

    private String getContentPath(String path, String content) {
        if (content != null && content.length() > 1) {
            return "content/" + path + "-" + content + ".json";
        }
        return "content/" + path + ".json";
    }
}
