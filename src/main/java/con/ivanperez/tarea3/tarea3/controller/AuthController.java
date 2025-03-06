package con.ivanperez.tarea3.tarea3.controller;

import con.ivanperez.tarea3.tarea3.model.User;
import con.ivanperez.tarea3.tarea3.service.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;
    
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Muestra la página de login
     */
    @GetMapping("/login")
    public String showLoginPage() {
        // Si el usuario ya está autenticado, redirigir al dashboard
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    /**
     * Muestra la página de registro
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    /**
     * Procesa el formulario de registro
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, 
                               @ModelAttribute("confirmPassword") String confirmPassword,
                               Model model, 
                               RedirectAttributes redirectAttributes) {
        try {
            // Validar que las contraseñas coincidan
            if (!user.getPassword().equals(confirmPassword)) {
                model.addAttribute("error", "Las contraseñas no coinciden");
                return "register";
            }
            
            // Verificar si el correo ya está registrado
            if (userService.isEmailAlreadyInUse(user.getEmail())) {
                model.addAttribute("error", "Este correo electrónico ya está registrado");
                return "register";
            }
            
            // Registrar el nuevo usuario
            userService.registerNewUser(user);
            
            // Redirigir a login con mensaje de éxito
            redirectAttributes.addFlashAttribute("success", "Registro exitoso. Ahora puedes iniciar sesión.");
            return "redirect:/login";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error en el registro: " + e.getMessage());
            return "register";
        }
    }

    /**
     * Muestra el dashboard del usuario autenticado
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        
        if (user != null) {
            model.addAttribute("user", user);
            return "dashboard";
        } else {
            return "redirect:/login";
        }
    }

    /**
     * Redirecciona la raíz a la página de login
     */
    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    /**
     * Maneja cualquier ruta desconocida y la redirige al login
     */
    @RequestMapping("*")
    public String handleUnknownRequest() {
        // Si el usuario está autenticado, redirige al dashboard
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return "redirect:/dashboard";
        }
        // Si no está autenticado, redirige al login
        return "redirect:/login?unauthorized=true";
    }
}
