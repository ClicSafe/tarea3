package con.ivanperez.tarea3.tarea3.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import con.ivanperez.tarea3.tarea3.model.User;
import con.ivanperez.tarea3.tarea3.service.UserService;

@Controller
@RequestMapping("/profile")
public class UserController {

    private final UserService userService;
    
    // Directorio donde se guardarán las imágenes (configurable en application.properties)
    @Value("${app.upload.dir:${user.home}/uploads}")
    private String uploadDir;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping
    public String showProfile(Model model) {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        
        if (user != null) {
            model.addAttribute("user", user);
            return "profile";
        } else {
            return "redirect:/login";
        }
    }
    
    @GetMapping("/edit")
    public String showEditProfileForm(Model model) {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        
        if (user != null) {
            model.addAttribute("user", user);
            return "edit-profile";
        } else {
            return "redirect:/login";
        }
    }
    
    @PostMapping("/edit")
    public String updateProfile(
            @RequestParam("id") Long id,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            RedirectAttributes redirectAttributes) {
        try {
            // Obtener el usuario actual de la BD
            User currentUser = userService.findUserById(id);
            
            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/login";
            }
            
            // Verificar que el email coincide con el del usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (!currentUser.getEmail().equals(auth.getName())) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para modificar este perfil");
                return "redirect:/profile";
            }
            
            // Actualizar datos del usuario
            currentUser.setFirstName(firstName);
            currentUser.setLastName(lastName);
            
            // Procesar la imagen si se ha subido una nueva
            if (profileImage != null && !profileImage.isEmpty()) {
                try {
                    currentUser.setProfileImage(profileImage.getBytes());
                    currentUser.setProfileImageContentType(profileImage.getContentType());
                } catch (IOException e) {
                    redirectAttributes.addFlashAttribute("error", "Error al procesar la imagen: " + e.getMessage());
                    return "redirect:/profile/edit";
                }
            }
            
            // Guardar cambios
            userService.updateUser(currentUser);
            
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente");
            return "redirect:/profile";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
            return "redirect:/profile/edit";
        }
    }
    
    // Endpoint para servir la imagen de perfil desde la base de datos
    @GetMapping("/image/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getProfileImage(@PathVariable Long id) {
        User user = userService.findUserById(id);
        
        if (user != null && user.getProfileImage() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                    user.getProfileImageContentType() != null ? 
                    user.getProfileImageContentType() : "image/jpeg"));
            
            return new ResponseEntity<>(user.getProfileImage(), headers, HttpStatus.OK);
        }
        
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
