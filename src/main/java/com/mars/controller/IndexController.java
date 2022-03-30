package com.mars.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * .
 * Package Name:   com.yyj.controller
 *
 * @author: YYJ
 * Date Time:      2021/5/24 17:27
 */

@Controller
public class IndexController {

    @GetMapping(value = "/toIndex")
    public String toIndexPage() {
        return "index";
    }

    @GetMapping(value = "/")
    public String toIndexPageDirectly() {
        return "index";
    }


}
