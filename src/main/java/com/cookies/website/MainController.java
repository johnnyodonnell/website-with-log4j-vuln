package com.cookies.website;

import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class MainController {

    @GetMapping("/")
    public String main() {
        return "index.html";
    }

    @PostMapping("/")
    public void submit(
            HttpServletResponse response,
            @RequestParam Map<String, String> body) throws Exception {
        response.sendRedirect("/success.html");
    }
}

