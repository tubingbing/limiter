package com.tbb.controller.index;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


/**
 * Created with IntelliJ IDEA.
 * User: fengjian
 * Date: 2015/9/14
 * Time: 15:37
 */
@Controller
@RequestMapping("/")
public class IndexController  {
    
    @RequestMapping(value = "/index")
    public ModelAndView index(ModelAndView view) {
        view.setViewName("index");
        return view;
    }
    
    @RequestMapping(value = "indexContent")
    public ModelAndView content(ModelAndView view) {
        view.setViewName("index_content");
        return view;
    }

}
