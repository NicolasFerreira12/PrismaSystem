package br.com.prismasys.service;

import br.com.prismasys.model.Funcionario;
import br.com.prismasys.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * localiza o funcionário pelo login e monta as credenciais/função
 *  o spring security vai usar para "Verificar senha".
 */

@Service
public class AutenticacaoService implements UserDetailsService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Funcionario funcionario = funcionarioRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Login não encontrado: " + login));

        return User.builder()
                .username(funcionario.getLogin())
                .password(funcionario.getSenha())
                .authorities(new SimpleGrantedAuthority("ROLE_" + funcionario.getPerfil().name()))
                .build();
    }
}