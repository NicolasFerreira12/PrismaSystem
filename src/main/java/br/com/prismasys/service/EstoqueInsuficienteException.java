package br.com.prismasys.service;

import java.math.BigDecimal;

public class EstoqueInsuficienteException extends IllegalArgumentException {

    public EstoqueInsuficienteException(String produto, BigDecimal disponivel, BigDecimal solicitado) {
        super("Estoque insuficiente de \"" + produto + "\": disponível "
                + disponivel.stripTrailingZeros().toPlainString()
                + ", solicitado " + solicitado.stripTrailingZeros().toPlainString() + ".");
    }
}