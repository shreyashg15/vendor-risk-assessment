@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/api/vendors/**").hasRole("ADMIN")
            .antMatchers("/api/public/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
            .withUser("apoorva").password("{noop}password").roles("ADMIN")
            .and()
            .withUser("user").password("{noop}password").roles("USER");
    }
}
