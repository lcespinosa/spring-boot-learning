package tacos.security;

import org.springframework.security.core.userdetails.UserDetails;
import tacos.exceptions.UsernameNotFoundException;

public interface UserDetailsService {

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
