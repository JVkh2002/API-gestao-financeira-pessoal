// TransacaoRepository.java
package com.financas.repository;

import com.financas.model.Transacao;
import com.financas.model.TipoTransacao;
import com.financas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByUsuario(Usuario usuario);

    @Query("SELECT COALESCE(SUM(t.valor), 0) FROM Transacao t WHERE t.usuario = :usuario AND t.tipo = :tipo")
    Double somaPorTipoEUsuario(@Param("usuario") Usuario usuario, @Param("tipo") TipoTransacao tipo);
}
