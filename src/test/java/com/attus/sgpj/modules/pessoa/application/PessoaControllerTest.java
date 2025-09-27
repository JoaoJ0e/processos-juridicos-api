// PessoaControllerWebMvcTest.java
package com.attus.sgpj.modules.pessoa.application;

import com.attus.sgpj.modules.pessoa.domain.dto.PessoaRequestDTO;
import com.attus.sgpj.modules.pessoa.domain.dto.PessoaResponseDTO;
import com.attus.sgpj.modules.pessoa.exception.PessoaAlreadyExistsException;
import com.attus.sgpj.modules.pessoa.exception.PessoaNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PessoaController.class)
class PessoaControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    PessoaService pessoaService;

    @Test
    void create_returnsCreated() throws Exception {
        PessoaRequestDTO req = new PessoaRequestDTO("João Silva","12345678901","joao.silva@email.com","11987654321");
        PessoaResponseDTO res = new PessoaResponseDTO(UUID.randomUUID(),"João Silva","12345678901","joao.silva@email.com","11987654321");

        Mockito.when(pessoaService.create(any())).thenReturn(res);

        mockMvc.perform(post("/pessoa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomeCompleto").value("João Silva"))
                .andExpect(jsonPath("$.cpfCnpj").value("12345678901"));
    }

    @Test
    void findById_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        PessoaResponseDTO res = new PessoaResponseDTO(id,"Maria Oliveira","98765432100","maria.oliveira@email.com","21991234567");
        Mockito.when(pessoaService.findById(id)).thenReturn(res);

        mockMvc.perform(get("/pessoa/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.cpfCnpj").value("98765432100"));
    }

    @Test
    void update_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        PessoaRequestDTO req = new PessoaRequestDTO("Carlos Souza","11122233344","carlos.souza@email.com","31987654321");
        PessoaResponseDTO res = new PessoaResponseDTO(id,"Carlos Souza","11122233344","carlos.souza@email.com","31987654321");

        Mockito.when(pessoaService.update(eq(id), any())).thenReturn(res);

        mockMvc.perform(put("/pessoa/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeCompleto").value("Carlos Souza"))
                .andExpect(jsonPath("$.cpfCnpj").value("11122233344"));
    }

    @Test
    void findPaged_returnsOk() throws Exception {
        Mockito.when(pessoaService.findPaged(0,20,null,null))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/pessoa?page=0&size=20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void findByNome_returnsOk() throws Exception {
        PessoaResponseDTO dto = new PessoaResponseDTO(
                UUID.randomUUID(),
                "João Silva",
                "12345678901",
                "joao.silva@email.com",
                "11999999999"
        );

        // Mock with the actual default values your controller uses
        Mockito.when(pessoaService.findByNomeContaining("joao", 0, 10))  // Changed to 10 if that's your default
                .thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/pessoa/search")
                        .param("nome", "joao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].nomeCompleto").value("João Silva"))
                .andExpect(jsonPath("$.content[0].cpfCnpj").value("12345678901"));
    }


    @Test
    void findByCpfCnpj_returnsOk() throws Exception {
        PessoaResponseDTO res = new PessoaResponseDTO(UUID.randomUUID(),"Ana Lima","55566677788","ana.lima@email.com","41999998888");
        Mockito.when(pessoaService.findByCpfCnpj("55566677788")).thenReturn(res);

        mockMvc.perform(get("/pessoa/cpf/{cpfCnpj}", "55566677788"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpfCnpj").value("55566677788"));
    }

    @Test
    void findById_returnsNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(pessoaService.findById(id))
                .thenThrow(new PessoaNotFoundException("Pessoa não encontrada com ID: " + id));

        mockMvc.perform(get("/pessoa/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PESSOA_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Pessoa não encontrada com ID: " + id));
    }

    @Test
    void create_returnsConflict_whenAlreadyExists() throws Exception {
        PessoaRequestDTO req = new PessoaRequestDTO("João Silva","12345678901","joao.silva@email.com","11987654321");

        Mockito.when(pessoaService.create(any()))
                .thenThrow(new PessoaAlreadyExistsException("CPF/CNPJ já está cadastrado: " + req.cpfCnpj()));

        mockMvc.perform(post("/pessoa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("PESSOA_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("CPF/CNPJ já está cadastrado: " + req.cpfCnpj()));
    }

}
