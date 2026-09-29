package br.com.prismasys.service;

import br.com.prismasys.model.Fornecedor;
import br.com.prismasys.repository.FornecedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FornecedorService {

    @Autowired
    private FornecedorRepository repository;

    public List<Fornecedor> listar() {
        return repository.findAll();
    }

    public Fornecedor salvar(Fornecedor fornecedor) {
        return repository.save(fornecedor);
    }

    public Optional<Fornecedor> buscarPorCnpj(String cnpj) {
        return repository.findByCnpj(cnpj);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}