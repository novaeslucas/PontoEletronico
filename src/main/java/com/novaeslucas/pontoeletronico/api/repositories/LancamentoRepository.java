package com.novaeslucas.pontoeletronico.api.repositories;

import com.novaeslucas.pontoeletronico.api.entities.Lancamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional(readOnly = true)
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    @Query("SELECT lanc FROM Lancamento lanc WHERE lanc.funcionario.id = ?1")
    Page<Lancamento> findByFuncionarioId(Long funcionarioId, Pageable pageable);

    @Query("SELECT lanc FROM Lancamento lanc WHERE lanc.funcionario.id = ?1")
    List<Lancamento> findByFuncionarioId(Long funcionarioId);

    @Query("SELECT lanc FROM Lancamento lanc WHERE lanc.data between :dataInicial and :dataFinal")
    List<Lancamento> findByData(@Param("dataInicial") Date dataInicial, @Param("dataFinal") Date dataFinal);

    @Query("SELECT lanc FROM Lancamento lanc WHERE lanc.data between :dataInicial and :dataFinal and lanc.funcionario.id = :funcionarioId ORDER BY lanc.data ASC")
    List<Lancamento> findByDatasFuncionarioId(@Param("dataInicial") Date dataInicial, @Param("dataFinal") Date dataFinal, @Param("funcionarioId")Long funcionarioId);

    @Query("SELECT lanc FROM Lancamento lanc WHERE lanc.data = :data and lanc.funcionario.id = :funcionarioId")
    Lancamento findByDataFuncionarioId(@Param("data") Date data, @Param("funcionarioId")Long funcionarioId);
}
