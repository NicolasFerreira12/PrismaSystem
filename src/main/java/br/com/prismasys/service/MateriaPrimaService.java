package br.com.prismasys.service;

import br.com.prismasys.model.MateriaPrima;
import br.com.prismasys.repository.MateriaPrimaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MateriaPrimaService {

    @Autowired
    private MateriaPrimaRepository repository;

    public List<MateriaPrima> listar() {
        return repository.findAll();
    }

    public MateriaPrima salvar(MateriaPrima materiaPrima) {
        return repository.save(materiaPrima);
    }

    public Optional<MateriaPrima> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}