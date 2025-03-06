package con.ivanperez.tarea3.tarea3.service;

import con.ivanperez.tarea3.tarea3.model.User;

public interface UserService {
    User registerNewUser(User user);
    User findUserByEmail(String email);
    User findUserById(Long id);
    boolean isEmailAlreadyInUse(String email);
    User updateUser(User user);
}
