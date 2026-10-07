package br.com.prismasys.service;

import br.com.prismasys.model.ContaPagarReceber;
import br.com.prismasys.model.StatusConta;
import br.com.prismasys.repository.ContaPagarReceberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ContaPagarReceberService {

    @Autowired
    private ContaPagarReceberRepository repository;

    public List<ContaPagarReceber> listar() {
        return repository.findAll();
    }

    public ContaPagarReceber salvar(ContaPagarReceber conta) {
        if (conta.getStatus() == null) {
            conta.setStatus(StatusConta.PENDENTE);
        }
        return repository.save(conta);
    }

    public Optional<ContaPagarReceber> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }

    /** "Gerenciar Contas a Pagar/Receber": dar baixa (marcar como paga/recebida) */
    public void darBaixa(Long id) {
        repository.findById(id).ifPresent(conta -> {
            conta.setStatus(StatusConta.PAGA);
            conta.setDataPagamento(new Date());
            repository.save(conta);
        });
    }
}