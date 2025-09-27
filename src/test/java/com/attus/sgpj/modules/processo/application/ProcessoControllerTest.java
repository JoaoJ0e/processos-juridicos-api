package com.attus.sgpj.modules.processo.application;

import com.attus.sgpj.modules.acao.domain.TipoAcaoEnum;
import com.attus.sgpj.modules.acao.domain.dto.AcaoRequestDTO;
import com.attus.sgpj.modules.parteenvolvida.domain.TipoParteEnvolvidaEnum;
import com.attus.sgpj.modules.processo.domain.StatusProcessoEnum;
import com.attus.sgpj.modules.processo.domain.dto.ProcessoRequestDTO;
import com.attus.sgpj.modules.processo.domain.dto.ProcessoResponseDTO;
import com.attus.sgpj.modules.processo.exception.ProcessoNotFoundException;
import com.attus.sgpj.modules.parteenvolvida.domain.dto.ParteEnvolvidaRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProcessoController.class)
class ProcessoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ProcessoService processoService;

    @Test
    void create_returnsCreated() throws Exception {
        ProcessoRequestDTO req = new ProcessoRequestDTO("12345678901234567890", "Processo de Teste", LocalDate.now());
        ProcessoResponseDTO res = new ProcessoResponseDTO(
                UUID.randomUUID(),
                "12345678901234567890",
                "Processo de Teste",
                LocalDate.now(),
                StatusProcessoEnum.ATIVO,
                List.of(),
                List.of()
        );

        Mockito.when(processoService.create(any())).thenReturn(res);

        mockMvc.perform(post("/processo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero").value("12345678901234567890"))
                .andExpect(jsonPath("$.descricao").value("Processo de Teste"))
                .andExpect(jsonPath("$.statusProcesso").value("ATIVO"));
    }

    @Test
    void create_withDefaultConstructor_returnsCreated() throws Exception {
        ProcessoRequestDTO req = new ProcessoRequestDTO("12345678901234567890", "Processo de Teste");
        ProcessoResponseDTO res = new ProcessoResponseDTO(
                UUID.randomUUID(),
                "12345678901234567890",
                "Processo de Teste",
                LocalDate.now(),
                StatusProcessoEnum.ATIVO,
                List.of(),
                List.of()
        );

        Mockito.when(processoService.create(any())).thenReturn(res);

        mockMvc.perform(post("/processo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero").value("12345678901234567890"))
                .andExpect(jsonPath("$.descricao").value("Processo de Teste"))
                .andExpect(jsonPath("$.statusProcesso").value("ATIVO"));
    }

    @Test
    void findById_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        ProcessoResponseDTO res = new ProcessoResponseDTO(
                id,
                "09876543210987654321",
                "Processo Consulta",
                LocalDate.now(),
                StatusProcessoEnum.ATIVO,
                List.of(),
                List.of()
        );

        Mockito.when(processoService.findById(id)).thenReturn(res);

        mockMvc.perform(get("/processo/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.numero").value("09876543210987654321"))
                .andExpect(jsonPath("$.descricao").value("Processo Consulta"));
    }

    @Test
    void update_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        ProcessoRequestDTO req = new ProcessoRequestDTO("11111111111111111111", "Processo Atualizado", LocalDate.now());
        ProcessoResponseDTO res = new ProcessoResponseDTO(
                id,
                "11111111111111111111",
                "Processo Atualizado",
                LocalDate.now(),
                StatusProcessoEnum.ATIVO,
                List.of(),
                List.of()
        );

        Mockito.when(processoService.update(eq(id), any())).thenReturn(res);

        mockMvc.perform(put("/processo/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Processo Atualizado"))
                .andExpect(jsonPath("$.numero").value("11111111111111111111"));
    }

    @Test
    void findPaged_returnsOk() throws Exception {
        Mockito.when(processoService.findPaged(0, 10, null, null))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/processo?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void findByStatus_returnsOk() throws Exception {
        ProcessoResponseDTO dto = new ProcessoResponseDTO(
                UUID.randomUUID(),
                "22222222222222222222",
                "Processo Ativo",
                LocalDate.now(),
                StatusProcessoEnum.ATIVO,
                List.of(),
                List.of()
        );

        Mockito.when(processoService.findByStatus(StatusProcessoEnum.ATIVO, 0, 10))
                .thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/processo/status/{status}", "ATIVO")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].statusProcesso").value("ATIVO"))
                .andExpect(jsonPath("$.content[0].descricao").value("Processo Ativo"));
    }

    @Test
    void findByDataAbertura_returnsOk() throws Exception {
        LocalDate dataInicial = LocalDate.of(2024, 1, 1);
        LocalDate dataFinal = LocalDate.of(2024, 12, 31);
        LocalDate dataProcesso = LocalDate.of(2024, 6, 15);

        ProcessoResponseDTO dto = new ProcessoResponseDTO(
                UUID.randomUUID(),
                "33333333333333333333",
                "Processo Por Data",
                dataProcesso,
                StatusProcessoEnum.ATIVO,
                List.of(),
                List.of()
        );

        Mockito.when(processoService.findByDataAbertura(dataInicial, dataFinal, 0, 10))
                .thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/processo/data-abertura")
                        .param("dataInicial", "2024-01-01")
                        .param("dataFinal", "2024-12-31")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].descricao").value("Processo Por Data"))
                .andExpect(jsonPath("$.content[0].dataAbertura").value("2024-06-15"));
    }

    @Test
    void findByPessoaId_returnsOk() throws Exception {
        UUID pessoaId = UUID.randomUUID();
        ProcessoResponseDTO dto = new ProcessoResponseDTO(
                UUID.randomUUID(),
                "44444444444444444444",
                "Processo da Pessoa",
                LocalDate.now(),
                StatusProcessoEnum.ATIVO,
                List.of(),
                List.of()
        );

        Mockito.when(processoService.findByPessoaId(pessoaId, 0, 10))
                .thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/processo/pessoa/id/{pessoaId}", pessoaId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].descricao").value("Processo da Pessoa"));
    }

    @Test
    void findByPessoaCpfCnpj_returnsOk() throws Exception {
        String cpfCnpj = "12345678901";
        ProcessoResponseDTO dto = new ProcessoResponseDTO(
                UUID.randomUUID(),
                "55555555555555555555",
                "Processo por CPF",
                LocalDate.now(),
                StatusProcessoEnum.ATIVO,
                List.of(),
                List.of()
        );

        Mockito.when(processoService.findByPessoaCpfCnpj(cpfCnpj, 0, 10))
                .thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/processo/pessoa/cpf-cnpj/{cpfCnpj}", cpfCnpj)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].descricao").value("Processo por CPF"));
    }

    @Test
    void ativar_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        ProcessoResponseDTO res = new ProcessoResponseDTO(
                id,
                "66666666666666666666",
                "Processo Ativado",
                LocalDate.now(),
                StatusProcessoEnum.ATIVO,
                List.of(),
                List.of()
        );

        Mockito.when(processoService.ativar(id)).thenReturn(res);

        mockMvc.perform(put("/processo/{id}/ativar", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusProcesso").value("ATIVO"))
                .andExpect(jsonPath("$.descricao").value("Processo Ativado"));
    }

    @Test
    void suspender_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        ProcessoResponseDTO res = new ProcessoResponseDTO(
                id,
                "77777777777777777777",
                "Processo Suspenso",
                LocalDate.now(),
                StatusProcessoEnum.SUSPENSO,
                List.of(),
                List.of()
        );

        Mockito.when(processoService.suspender(id)).thenReturn(res);

        mockMvc.perform(put("/processo/{id}/suspender", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusProcesso").value("SUSPENSO"))
                .andExpect(jsonPath("$.descricao").value("Processo Suspenso"));
    }

    @Test
    void arquivar_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        ProcessoResponseDTO res = new ProcessoResponseDTO(
                id,
                "88888888888888888888",
                "Processo Arquivado",
                LocalDate.now(),
                StatusProcessoEnum.ARQUIVADO,
                List.of(),
                List.of()
        );

        Mockito.when(processoService.arquivar(id)).thenReturn(res);

        mockMvc.perform(put("/processo/{id}/arquivar", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusProcesso").value("ARQUIVADO"))
                .andExpect(jsonPath("$.descricao").value("Processo Arquivado"));
    }

    @Test
    void addParteEnvolvida_returnsOk() throws Exception {
        UUID processoId = UUID.randomUUID();
        UUID pessoaId = UUID.randomUUID();
        ParteEnvolvidaRequestDTO req = new ParteEnvolvidaRequestDTO(pessoaId, TipoParteEnvolvidaEnum.AUTOR);
        ProcessoResponseDTO res = new ProcessoResponseDTO(
                processoId,
                "99999999999999999999",
                "Processo com Parte",
                LocalDate.now(),
                StatusProcessoEnum.ATIVO,
                List.of(),
                List.of()
        );

        Mockito.when(processoService.addParteEnvolvida(eq(processoId), any())).thenReturn(res);

        mockMvc.perform(post("/processo/{id}/partes-envolvidas", processoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Processo com Parte"));
    }

    @Test
    void addPartesEnvolvidas_returnsOk() throws Exception {
        UUID processoId = UUID.randomUUID();
        UUID pessoaId1 = UUID.randomUUID();
        UUID pessoaId2 = UUID.randomUUID();
        List<ParteEnvolvidaRequestDTO> reqList = List.of(
                new ParteEnvolvidaRequestDTO(pessoaId1, TipoParteEnvolvidaEnum.ADVOGADO),
                new ParteEnvolvidaRequestDTO(pessoaId2, TipoParteEnvolvidaEnum.REU)
        );
        ProcessoResponseDTO res = new ProcessoResponseDTO(
                processoId,
                "10101010101010101010",
                "Processo com Partes",
                LocalDate.now(),
                StatusProcessoEnum.ATIVO,
                List.of(),
                List.of()
        );

        Mockito.when(processoService.addPartesEnvolvidas(eq(processoId), any())).thenReturn(res);

        mockMvc.perform(post("/processo/{id}/partes-envolvidas/batch", processoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqList)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Processo com Partes"));
    }

    @Test
    void removeParteEnvolvida_returnsOk() throws Exception {
        UUID processoId = UUID.randomUUID();
        UUID parteId = UUID.randomUUID();
        ProcessoResponseDTO res = new ProcessoResponseDTO(
                processoId,
                "20202020202020202020",
                "Processo Sem Parte",
                LocalDate.now(),
                StatusProcessoEnum.ATIVO,
                List.of(),
                List.of()
        );

        Mockito.when(processoService.removeParteEnvolvida(processoId, parteId)).thenReturn(res);

        mockMvc.perform(delete("/processo/{id}/partes-envolvidas/{parteId}", processoId, parteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Processo Sem Parte"));
    }

    @Test
    void addAcaoProcesso_returnsOk() throws Exception {
        UUID processoId = UUID.randomUUID();
        AcaoRequestDTO req = new AcaoRequestDTO(TipoAcaoEnum.PETICAO, "Descrição da petição inicial");
        ProcessoResponseDTO res = new ProcessoResponseDTO(
                processoId,
                "30303030303030303030",
                "Processo com Ação",
                LocalDate.now(),
                StatusProcessoEnum.ATIVO,
                List.of(),
                List.of()
        );

        Mockito.when(processoService.addAcaoProcesso(eq(processoId), any())).thenReturn(res);

        mockMvc.perform(post("/processo/{id}/acoes", processoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Processo com Ação"));
    }

    @Test
    void addAcoesProcesso_returnsOk() throws Exception {
        UUID processoId = UUID.randomUUID();
        List<AcaoRequestDTO> reqList = List.of(
                new AcaoRequestDTO(TipoAcaoEnum.PETICAO, "Descrição da primeira ação"),
                new AcaoRequestDTO(TipoAcaoEnum.AUDIENCIA, "Descrição da segunda ação")
        );
        ProcessoResponseDTO res = new ProcessoResponseDTO(
                processoId,
                "40404040404040404040",
                "Processo com Ações",
                LocalDate.now(),
                StatusProcessoEnum.ATIVO,
                List.of(),
                List.of()
        );

        Mockito.when(processoService.addAcoesProcesso(eq(processoId), any())).thenReturn(res);

        mockMvc.perform(post("/processo/{id}/acoes/batch", processoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqList)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Processo com Ações"));
    }

    @Test
    void removeAcaoProcesso_returnsOk() throws Exception {
        UUID processoId = UUID.randomUUID();
        UUID acaoId = UUID.randomUUID();
        ProcessoResponseDTO res = new ProcessoResponseDTO(
                processoId,
                "50505050505050505050",
                "Processo Sem Ação",
                LocalDate.now(),
                StatusProcessoEnum.ATIVO,
                List.of(),
                List.of()
        );

        Mockito.when(processoService.removeAcaoProcesso(processoId, acaoId)).thenReturn(res);

        mockMvc.perform(delete("/processo/{id}/acoes/{acaoId}", processoId, acaoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Processo Sem Ação"));
    }

    @Test
    void findById_returnsNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(processoService.findById(id))
                .thenThrow(new ProcessoNotFoundException("Processo não encontrado com ID: " + id));

        mockMvc.perform(get("/processo/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PROCESSO_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Processo não encontrado com ID: " + id));
    }

    @Test
    void ativar_returnsNotFound_whenProcessoDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(processoService.ativar(id))
                .thenThrow(new ProcessoNotFoundException("Processo não encontrado com ID: " + id));

        mockMvc.perform(put("/processo/{id}/ativar", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PROCESSO_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Processo não encontrado com ID: " + id));
    }

    @Test
    void suspender_returnsNotFound_whenProcessoDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(processoService.suspender(id))
                .thenThrow(new ProcessoNotFoundException("Processo não encontrado com ID: " + id));

        mockMvc.perform(put("/processo/{id}/suspender", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PROCESSO_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Processo não encontrado com ID: " + id));
    }

    @Test
    void arquivar_returnsNotFound_whenProcessoDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(processoService.arquivar(id))
                .thenThrow(new ProcessoNotFoundException("Processo não encontrado com ID: " + id));

        mockMvc.perform(put("/processo/{id}/arquivar", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PROCESSO_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Processo não encontrado com ID: " + id));
    }
}