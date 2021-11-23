package io.meshswap.core.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@ComponentScan(value = {"oio.meshswap.core.*"})
@EnableAutoConfiguration
@EnableGlobalMethodSecurity(securedEnabled=true, prePostEnabled=true)
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {
/*
    @Autowired
    private DataSource dataSource;
    @Autowired
    CustomAuthenticationProvider customAuthProvider;
*/
    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
       // auth.authenticationProvider(customAuthProvider);
/*
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("select email,password,enabled "
                        + "from user "
                        + "where email = ?")
                .authoritiesByUsernameQuery("select user.email, authority.authority "
                        + "from authority, user where user.id=authority.user_id and "
                        + "user.email = ?");
                        */

        // auth.userDetailsService(userDetailsServiceBean());
        auth.inMemoryAuthentication()
                .withUser("user1").password(passwordEncoder().encode("user1Pass")).roles("USER")
                .and()
                .withUser("user11").password("user11").roles("USER")
                .and()
                .withUser("user2").password(passwordEncoder().encode("user2Pass")).roles("USER")
                .and()
                .withUser("admin").password(passwordEncoder().encode("adminPass")).roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
// "/home","/block/**","/tx/**","/address/**","/search/**","/wallet/**","/css/**", "/admin/**"
        http
                .authorizeRequests()
                .antMatchers("/**","/api/**", "/user/registration", "/css/**")
                .permitAll();
                /*
                .antMatchers("/**")
                .authenticated()
                .and()
                .formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/device/all")
                .failureUrl("/login?error=true")
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/perform_logout"))
                //.logoutUrl("/perform_logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll();
                 */
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
