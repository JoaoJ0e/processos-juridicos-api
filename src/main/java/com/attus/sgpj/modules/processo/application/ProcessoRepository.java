package com.attus.sgpj.modules.processo.application;

import com.attus.sgpj.modules.processo.domain.Processo;
import com.attus.sgpj.modules.processo.domain.StatusProcessoEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface ProcessoRepository extends JpaRepository<Processo, UUID> {
    boolean existsByNumero(String numero);

    Page<Processo> findByStatusProcesso(StatusProcessoEnum status, Pageable pageable);

    Page<Processo> findByDataAberturaBetween(LocalDate periodoInicio, LocalDate periodoFim, Pageable pageable);

    @Query("""
            select p from Processo p
            join p.parteEnvolvidas pe
            where pe.pessoa.cpfCnpj = :cpfCnpj
            """)
    Page<Processo> findByParteEnvolvidaCpfCnpj(@Param("cpfCnpj") String cpfCnpj, Pageable pageable);

    @Query("""
            select p from Processo p
            join p.parteEnvolvidas pe
            where pe.pessoa.id = :pessoaId
            """)
    Page<Processo> findByPessoaId(@Param("pessoaId") UUID pessoaId, Pageable pageable);
}
