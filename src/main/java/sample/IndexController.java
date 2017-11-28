package sample;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Rob Winch
 * @since 5.0
 */
@Controller
public class IndexController {
	@GetMapping("/")
	String index() {
		return "redirect:/inbox";
	}
}
