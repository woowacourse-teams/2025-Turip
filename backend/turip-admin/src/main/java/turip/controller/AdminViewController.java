package turip.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import turip.account.domain.TuripMember;
import turip.resolver.AuthAdmin;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminViewController {

    @Value("${google.api.key}")
    private String googleApiKey;

    @Value("${youtube.api.key}")
    private String youtubeApiKey;

    @GetMapping
    public String indexPage() {
        return "admin/index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "admin/signup";
    }

    @GetMapping("/home")
    public String homePage(@AuthAdmin TuripMember admin) {
        return "admin/home";
    }

    @GetMapping("/contents")
    public String createContentPage(@AuthAdmin TuripMember admin, Model model) {
        model.addAttribute("googleApiKey", googleApiKey);
        model.addAttribute("youtubeApiKey", youtubeApiKey);
        return "admin/content";
    }
}
