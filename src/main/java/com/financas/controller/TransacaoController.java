// TransacaoController.java
package com.financas.controller;

import com.financas.model.Transacao;
import com.financas.model.Usuario;
import com.financas.repository.TransacaoRepository;
import com.financas.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.financas.model.TipoTransacao;

import java.util.List;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    private final TransacaoRepository transacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public TransacaoController(TransacaoRepository transacaoRepository, UsuarioRepository usuarioRepository) {
        this.transacaoRepository = transacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private Usuario getUsuarioLogado(Authentication auth) {
        return usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @GetMapping
    public List<Transacao> listar(Authentication auth) {
        return transacaoRepository.findByUsuario(getUsuarioLogado(auth));
    }

    @PostMapping
    public Transacao adicionar(@RequestBody Transacao transacao, Authentication auth) {
        transacao.setUsuario(getUsuarioLogado(auth));
        return transacaoRepository.save(transacao);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Long id, Authentication auth) {
        Usuario usuario = getUsuarioLogado(auth);
        Transacao t = transacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));
        if (!t.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Você não tem permissão para excluir esta transação");
        }
        transacaoRepository.deleteById(id);


    }

    @GetMapping("/saldo")
    public Double saldoTotal(Authentication auth) {
        Usuario usuario = getUsuarioLogado(auth);
        Double receitas = transacaoRepository.somaPorTipoEUsuario(usuario, TipoTransacao.RECEITA);
        Double despesas = transacaoRepository.somaPorTipoEUsuario(usuario, TipoTransacao.DESPESA);
        return receitas - despesas;
    }
}
