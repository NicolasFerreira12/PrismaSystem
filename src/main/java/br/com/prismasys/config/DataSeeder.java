package br.com.prismasys.config;

import br.com.prismasys.model.Funcionario;
import br.com.prismasys.model.Perfil;
import br.com.prismasys.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Garante que sempre exista pelo menos um Administrador para logar.
 * Só cria se o login "admin" ainda não existir — não sobrescreve nada.
 */

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (funcionarioRepository.findByLogin("admin").isEmpty()) {
            Funcionario admin = new Funcionario();
            admin.setRe(999);
            admin.setNome("Administrador");
            admin.setDataAdmissao(new Date());
            admin.setSalario(0.0);
            admin.setLogin("admin");
            admin.setSenha(passwordEncoder.encode("admin123"));
            admin.setPerfil(Perfil.ADMINISTRADOR);
            funcionarioRepository.save(admin);
            System.out.println(">>> Usuário admin criado (login: admin / senha: admin123) — TROQUE a senha depois.");
        }
    }
}