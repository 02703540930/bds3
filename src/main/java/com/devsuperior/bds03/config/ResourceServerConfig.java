package com.devsuperior.bds03.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter{

	@Autowired
	private Environment env;
	
	@Autowired
	private JwtTokenStore tokenStore;
	
	// endpoint para todos
	// nao acessa as categorias
	private static final String[] PUBLIC = { "/oauth/token" , "/h2-console/**" };
	
	// endpoints operador ou admin
	private static final String[] OPERADOR_GET= { "/departments/**", "/employees/**" };
	
	// endpoints exclusivo admin
	private static final String[] ADMIN = { "/users/**" };
	
		
	//decodificar o token e analisar expiracao e secrets
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenStore(tokenStore);
	}
	
	// Autorizacoes de rota por tipo de user e endpoints
	// catalogo liberado sem login 
	// Cruds produto e categoria login operador 
	// Crud  usuario login admin 
	// Qualquer outra rota precisa apenas autenticacao
	@Override
	public void configure(HttpSecurity http) throws Exception {
		
		//libera H2
		if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
			http.headers().frameOptions().disable();
		}
	
		http.authorizeRequests()
		.antMatchers(PUBLIC).permitAll()
		.antMatchers(HttpMethod.GET, OPERADOR_GET).hasAnyRole("OPERATOR", "ADMIN")
		.anyRequest().hasAnyRole("ADMIN");			
	}
	
}
