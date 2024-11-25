package hcmute.uni.demo_2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import hcmute.uni.demo_2.model.UserInfo;
import hcmute.uni.demo_2.repository.UserInfoRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Authentication
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.withUsername("trung")
                .password(encoder.encode("123"))
                .roles("ADMIN")
                .build();

        UserDetails user = User.withUsername("user")
                .password(encoder.encode("123"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/customer/**").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults())
                .build();
    }

    @Bean
    public class UserInfoService implements UserDetailsService {

        @Autowired
        UserInfoRepository repository;

        public UserInfoService(UserInfoRepository userInfoRepository) {
            this.repository = userInfoRepository;
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            Optional<UserInfo> userInfo = repository.findByName(username);
            return userInfo.map(UserInfoUserDetails::new)
                    .orElseThrow(() -> new UsernameNotFoundException("user not found: " + username));
        }
    }
}

