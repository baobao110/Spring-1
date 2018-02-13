package inter;

import user.User;

public interface UserDAO {
	
	int addUser(User user);
	
	User getUser(String username);
}
