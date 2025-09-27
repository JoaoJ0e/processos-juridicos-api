package com.attus.sgpj.modules.pessoa.application;

import com.attus.sgpj.modules.pessoa.domain.Pessoa;
import com.attus.sgpj.shared.vo.CpfCnpj;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, UUID> {

    Optional<Pessoa> findByCpfCnpj(CpfCnpj cpfCnpj);

    boolean existsByCpfCnpj(CpfCnpj cpfCnpj);

    Page<Pessoa> findByNomeCompletoContainingIgnoreCase(String nomeCompleto, Pageable pageable);
}
