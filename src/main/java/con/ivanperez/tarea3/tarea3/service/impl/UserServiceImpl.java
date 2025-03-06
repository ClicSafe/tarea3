package con.ivanperez.tarea3.tarea3.service.impl;

import con.ivanperez.tarea3.tarea3.model.User;
import con.ivanperez.tarea3.tarea3.repository.UserRepository;
import con.ivanperez.tarea3.tarea3.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerNewUser(User user) {
        // Verificar si el email ya está en uso
        if (isEmailAlreadyInUse(user.getEmail())) {
            throw new RuntimeException("El correo electrónico ya está registrado");
        }

        // Codificar la contraseña antes de guardarla
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Guardar el usuario en la base de datos
        return userRepository.save(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    public User findUserById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        return userOpt.orElse(null);
    }

    @Override
    public boolean isEmailAlreadyInUse(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public User updateUser(User user) {
        // Verificar que el usuario existe
        if (user.getId() == null) {
            throw new RuntimeException("No se puede actualizar un usuario sin ID");
        }
        
        // Guardar los cambios del usuario
        return userRepository.save(user);
    }
}
