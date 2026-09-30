package br.com.prismasys.service;

import br.com.prismasys.model.Transportadora;
import br.com.prismasys.repository.TransportadoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransportadoraService {

    @Autowired
    private TransportadoraRepository repository;

    public List<Transportadora> listar() {
        return repository.findAll();
    }

    public Transportadora salvar(Transportadora transportadora) {
        return repository.save(transportadora);
    }

    public Optional<Transportadora> buscarPorCnpj(String cnpj) {
        return repository.findByCnpj(cnpj);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}