package br.com.prismasys.service;

import br.com.prismasys.model.Funcionario;
import br.com.prismasys.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FuncionarioService {

	@Autowired
	private FuncionarioRepository repository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<Funcionario> listar() {
		return repository.findAll();
	}

	public Funcionario salvar(Funcionario funcionario) {
		if (funcionario.getSenha() != null && !funcionario.getSenha().isBlank()) {
			funcionario.setSenha(passwordEncoder.encode(funcionario.getSenha()));
		} else {
			// edição sem trocar a senha: mantém a senha que já existe no banco
			repository.findById(funcionario.getRe())
					.ifPresent(existente -> funcionario.setSenha(existente.getSenha()));
		}
		return repository.save(funcionario);
	}

	public Optional<Funcionario> buscarPorLogin(String login) {
		return repository.findByLogin(login);
	}

	public Optional<Funcionario> buscarPorRe(int re) {
		return repository.findById(re);
	}

	public void excluir(int re) {
		repository.deleteById(re);
	}
}