package turip.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminContentViewController {

    @Value("${google.api.key}")
    private String googleApiKey;

    @GetMapping("/contents")
    public String createContentPage(Model model) {
        model.addAttribute("googleApiKey", googleApiKey);
        return "admin-content";
    }
}
