package com.cookies.website;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
        String name = body.get("name");
        String qty = body.get("qty");
        String cc = body.get("cc");

        String entry = String.join(",", name, qty, cc) + "\n";

        String filename = "database.csv";

        // Create file if it does not already exist
        new File(filename).createNewFile();

        Files.write(
                Paths.get(filename),
                entry.getBytes(),
                StandardOpenOption.APPEND);

        response.sendRedirect("/success.html");
    }
}

