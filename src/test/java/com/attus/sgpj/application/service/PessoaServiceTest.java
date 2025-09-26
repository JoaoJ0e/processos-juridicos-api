package com.attus.sgpj.application.service;

import com.attus.sgpj.domain.dto.PessoaRequestDTO;
import com.attus.sgpj.domain.dto.PessoaResponseDTO;
import com.attus.sgpj.domain.exception.PessoaAlreadyExistsException;
import com.attus.sgpj.domain.exception.PessoaNotFoundException;
import com.attus.sgpj.domain.model.pessoa.Pessoa;
import com.attus.sgpj.domain.repository.PessoaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PessoaServiceTest {

    @Mock
    PessoaRepository pessoaRepository;

    @InjectMocks
    PessoaService pessoaService;

    @Test
    void create_savesAndReturnsResponse() {
        PessoaRequestDTO req = new PessoaRequestDTO(
                "João Silva",
                "12345678901",
                "joao.silva@email.com",
                "11987654321"
        );

        Pessoa savedPessoa = Pessoa.create(
                req.nomeCompleto(),
                req.cpfCnpj(),
                req.email(),
                req.telefone()
        );

        when(pessoaRepository.existsByCpfCnpj(req.cpfCnpj())).thenReturn(false);
        when(pessoaRepository.save(any(Pessoa.class))).thenReturn(savedPessoa);

        PessoaResponseDTO res = pessoaService.create(req);

        assertEquals(req.nomeCompleto(), res.nomeCompleto());
        assertEquals(req.cpfCnpj(), res.cpfCnpj());
        assertEquals(req.email(), res.email());
        assertEquals(req.telefone(), res.telefone());
    }


    @Test
    void create_throwsWhenCpfExists() {
        PessoaRequestDTO req = new PessoaRequestDTO("João Silva","12345678901","joao.silva@email.com","11987654321");
        when(pessoaRepository.existsByCpfCnpj(req.cpfCnpj())).thenReturn(true);

        assertThrows(PessoaAlreadyExistsException.class, () -> pessoaService.create(req));
    }

    @Test
    void update_updatesAndReturnsResponse() {
        UUID id = UUID.randomUUID();
        PessoaRequestDTO req = new PessoaRequestDTO("Maria Souza","98765432100","maria.souza@email.com","21987654321");
        Pessoa pessoa = Pessoa.create("Antigo","11122233344","antigo@email.com","11999998888");

        when(pessoaRepository.findById(id)).thenReturn(Optional.of(pessoa));
        when(pessoaRepository.existsByCpfCnpj(req.cpfCnpj())).thenReturn(false);
        when(pessoaRepository.save(any(Pessoa.class))).thenReturn(pessoa);

        PessoaResponseDTO res = pessoaService.update(id, req);

        assertEquals(req.nomeCompleto(), res.nomeCompleto());
        assertEquals(req.cpfCnpj(), res.cpfCnpj());
    }

    @Test
    void update_throwsWhenCpfAlreadyExists() {
        UUID id = UUID.randomUUID();
        PessoaRequestDTO req = new PessoaRequestDTO("João Silva","99988877766","joao@email.com","11987654321");
        Pessoa pessoa = Pessoa.create("Antigo","11122233344","antigo@email.com","11999998888");

        when(pessoaRepository.findById(id)).thenReturn(Optional.of(pessoa));
        when(pessoaRepository.existsByCpfCnpj(req.cpfCnpj())).thenReturn(true);

        assertThrows(PessoaAlreadyExistsException.class, () -> pessoaService.update(id, req));
    }

    @Test
    void findById_returnsResponse() {
        UUID id = UUID.randomUUID();
        Pessoa pessoa = Pessoa.create("Carlos Lima","11122233344","carlos@email.com","31987654321");
        when(pessoaRepository.findById(id)).thenReturn(Optional.of(pessoa));

        PessoaResponseDTO res = pessoaService.findById(id);

        assertEquals("Carlos Lima", res.nomeCompleto());
    }

    @Test
    void findById_throwsWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(pessoaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PessoaNotFoundException.class, () -> pessoaService.findById(id));
    }

    @Test
    void findPaged_withoutSort_returnsPage() {
        Pessoa pessoa = Pessoa.create("Ana","12345678901","ana@email.com","41999998888");
        Page<Pessoa> page = new PageImpl<>(List.of(pessoa));
        when(pessoaRepository.findAll(any(Pageable.class))).thenReturn(page);

        var res = pessoaService.findPaged(0,20,null,null);

        assertEquals(1, res.getContent().size());
        assertEquals("Ana", res.getContent().get(0).nomeCompleto());
    }

    @Test
    void findByNomeContaining_returnsPage() {
        Pessoa pessoa = Pessoa.create("Pedro","22233344455","pedro@email.com","21988887777");
        Page<Pessoa> page = new PageImpl<>(List.of(pessoa));
        when(pessoaRepository.findByNomeCompletoContainingIgnoreCase(eq("pedro"), any(Pageable.class)))
                .thenReturn(page);

        var res = pessoaService.findByNomeContaining("pedro",0,20);

        assertEquals(1, res.getContent().size());
        assertEquals("Pedro", res.getContent().get(0).nomeCompleto());
    }

    @Test
    void findByCpfCnpj_returnsResponse() {
        Pessoa pessoa = Pessoa.create("Laura","55566677788","laura@email.com","11911112222");
        when(pessoaRepository.findByCpfCnpj("55566677788")).thenReturn(Optional.of(pessoa));

        var res = pessoaService.findByCpfCnpj("55566677788");

        assertEquals("Laura", res.nomeCompleto());
    }

    @Test
    void findByCpfCnpj_throwsWhenNotFound() {
        when(pessoaRepository.findByCpfCnpj("000")).thenReturn(Optional.empty());

        assertThrows(PessoaNotFoundException.class, () -> pessoaService.findByCpfCnpj("000"));
    }
}
