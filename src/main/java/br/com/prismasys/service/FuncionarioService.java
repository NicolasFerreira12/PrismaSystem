package br.com.prismasys.service;

import br.com.prismasys.model.Funcionario;
import br.com.prismasys.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FuncionarioService {

	@Autowired
	private FuncionarioRepository repository;

	public List<Funcionario> listar() {
		return repository.findAll();
	}

	public Funcionario salvar(Funcionario funcionario) {
		return repository.save(funcionario);
	}

	public Optional<Funcionario> buscarPorRe(int re) {
		return repository.findById(re);
	}

	public void excluir(int re) {
		repository.deleteById(re);
	}
}
