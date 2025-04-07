package com.panizio.agenda.service;

import com.panizio.agenda.exception.ValidacaoException;
import com.panizio.agenda.model.PessoaJuridica;
import com.panizio.agenda.repository.PessoaJuridicaRepository;
import com.panizio.agenda.utils.ValidacaoUtils;

import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Service
public class PessoaJuridicaService {

  @Autowired
  private PessoaJuridicaRepository pessoaJuridicaRepository;
  private final EmailService emailService;

  public PessoaJuridicaService(
      PessoaJuridicaRepository pessoaJuridicaRepository,
      EmailService emailService) {
    this.pessoaJuridicaRepository = pessoaJuridicaRepository;
    this.emailService = emailService;
  }

  public List<PessoaJuridica> listarPessoasJuridicas() {
    return pessoaJuridicaRepository.findAll();
  }

  public PessoaJuridica buscarPessoaJuridicaPorCnpj(String cnpj) {
    return pessoaJuridicaRepository.findById(limpar(cnpj)).orElse(null);
  }

  public List<PessoaJuridica> filtrarPorCnpj(String prefixo) {
    return pessoaJuridicaRepository.findByCnpjStartingWith(prefixo);
  }

  public PessoaJuridica salvarPessoaJuridica(PessoaJuridica pessoaJuridica) {
    pessoaJuridica.setCnpj(limpar(pessoaJuridica.getCnpj()));

    if (!ValidacaoUtils.validarCEP(pessoaJuridica.getCep())) {
      throw new ValidacaoException(Map.of("cep", "CEP inválido"));
    }

    buscarCoordenadas(pessoaJuridica);

    validarPessoaJuridica(pessoaJuridica, true);

    PessoaJuridica savedPessoa = pessoaJuridicaRepository.save(pessoaJuridica);
    emailService.enviarEmailConfirmacao(savedPessoa.getRazaoSocial(), savedPessoa.getEmail());

    return savedPessoa;
  }

  public PessoaJuridica atualizarPessoaJuridica(String cnpj, PessoaJuridica novosDados) {
    novosDados.setCnpj(limpar(novosDados.getCnpj()));
    PessoaJuridica pessoaExistente = pessoaJuridicaRepository.findById(cnpj)
        .orElseThrow(() -> new IllegalArgumentException("Pessoa jurídica não encontrada"));

    if (novosDados.getCep() != null && !novosDados.getCep().equals(pessoaExistente.getCep())) {
      if (!ValidacaoUtils.validarCEP(novosDados.getCep())) {
        throw new ValidacaoException(Map.of("cep", "CEP inválido"));
      }

      buscarCoordenadas(novosDados);
    }

    validarCamposUnicos(novosDados, pessoaExistente);
    validarPessoaJuridica(novosDados, false);
    atualizarCampos(pessoaExistente, novosDados);

    return pessoaJuridicaRepository.save(pessoaExistente);
  }

  public void excluirUsuario(String cnpj) {
    pessoaJuridicaRepository.deleteById(cnpj);
  }

  private void validarPessoaJuridica(PessoaJuridica pessoaJuridica, boolean isNovo) {
    Map<String, String> erros = new HashMap<>();

    validarCampo(pessoaJuridica.getCnpj(), ValidacaoUtils::validarCNPJ, "cnpj", "CNPJ inválido", erros);
    validarCampo(pessoaJuridica.getRazaoSocial(), ValidacaoUtils::validarNome, "razaoSocial", "Razão Social inválida",
        erros);
    validarCampo(pessoaJuridica.getNomeFantasia(), ValidacaoUtils::validarNome, "nomeFantasia",
        "Nome Fantasia inválido", erros);
    validarCampo(pessoaJuridica.getTelefone(), ValidacaoUtils::validarTelefone, "telefone", "Telefone inválido", erros);
    validarCampo(pessoaJuridica.getEmail(), ValidacaoUtils::validarEmail, "email", "E-mail inválido", erros);
    validarCampo(pessoaJuridica.getCep(), ValidacaoUtils::validarCEP, "cep", "CEP inválido", erros);

    validarCampo(pessoaJuridica.getLogradouro(), ValidacaoUtils::validarTexto, "endereco", "Endereço inválido", erros);
    validarCampo(pessoaJuridica.getNumeroEndereco(), ValidacaoUtils::validarTexto, "numero", "Número inválido", erros);
    validarCampo(pessoaJuridica.getBairro(), ValidacaoUtils::validarTexto, "bairro", "Bairro inválido", erros);
    validarCampo(pessoaJuridica.getCidade(), ValidacaoUtils::validarTexto, "cidade", "Cidade inválida", erros);
    validarCampo(pessoaJuridica.getEstado(), ValidacaoUtils::validarTexto, "estado", "Estado inválido", erros);

    if (isNovo) {
      validarExistenciaCampo(pessoaJuridica.getCnpj(), "cnpj", "CNPJ já cadastrado");
      validarExistenciaCampo(pessoaJuridica.getEmail(), "email", "E-mail já cadastrado");
    }

    if (!erros.isEmpty()) {
      throw new ValidacaoException(erros);
    }
  }

  private void validarCamposUnicos(PessoaJuridica novaPessoa, PessoaJuridica pessoaExistente) {
    if (novaPessoa.getCnpj() != null && !novaPessoa.getCnpj().equals(pessoaExistente.getCnpj())) {
      validarExistenciaCampo(novaPessoa.getCnpj(), "cnpj", "CNPJ já cadastrado.");
    }
    if (novaPessoa.getEmail() != null && !novaPessoa.getEmail().equals(pessoaExistente.getEmail())) {
      validarExistenciaCampo(novaPessoa.getEmail(), "email", "E-mail já cadastrado.");
    }
  }

  private <T> void validarCampo(T value, Predicate<T> validator, String field, String message,
      Map<String, String> errors) {
    if (value != null && !validator.test(value)) {
      errors.put(field, message);
    }
  }

  private void validarExistenciaCampo(String value, String field, String message) {
    if (value != null) {
      boolean exists = field.equals("cnpj")
          ? pessoaJuridicaRepository.existsById(value)
          : pessoaJuridicaRepository.findByEmail(value).isPresent();

      if (exists) {
        Map<String, String> error = new HashMap<>();
        error.put(field, message);
        throw new ValidacaoException(error);
      }
    }
  }

  private void atualizarCampos(PessoaJuridica existente, PessoaJuridica novosDados) {
    if (novosDados.getRazaoSocial() != null) {
      existente.setRazaoSocial(novosDados.getRazaoSocial());
    }
    if (novosDados.getNomeFantasia() != null) {
      existente.setNomeFantasia(novosDados.getNomeFantasia());
    }
    if (novosDados.getTelefone() != null) {
      existente.setTelefone(novosDados.getTelefone());
    }
    if (novosDados.getEmail() != null) {
      existente.setEmail(novosDados.getEmail());
    }
    if (novosDados.getCep() != null) {
      existente.setCep(novosDados.getCep());
    }
    if (novosDados.getLogradouro() != null) {
      existente.setLogradouro(novosDados.getLogradouro());
    }
    if (novosDados.getNumeroEndereco() != null) {
      existente.setNumeroEndereco(novosDados.getNumeroEndereco());
    }
    if (novosDados.getBairro() != null) {
      existente.setBairro(novosDados.getBairro());
    }
    if (novosDados.getComplemento() != null) {
      existente.setComplemento(novosDados.getComplemento());
    }
    if (novosDados.getCidade() != null) {
      existente.setCidade(novosDados.getCidade());
    }
    if (novosDados.getEstado() != null) {
      existente.setEstado(novosDados.getEstado());
    }
    if (novosDados.getCoordenadas() != null) {
      existente.setCoordenadas(novosDados.getCoordenadas());
    }
  }

  private String limpar(String data) {
    return data != null ? data.replaceAll("\\D", "") : null;
  }

  private void buscarCoordenadas(PessoaJuridica pessoaJuridica) {
    try {
      String enderecoCompleto = pessoaJuridica.getLogradouro() + ", " +
          pessoaJuridica.getNumeroEndereco() + ", " +
          pessoaJuridica.getBairro() + ", " +
          pessoaJuridica.getCidade() + ", " +
          pessoaJuridica.getEstado() + ", " +
          pessoaJuridica.getCep();
      Point coordenadas = ValidacaoUtils.buscarCoordenadasPorEndereco(enderecoCompleto);
      if (coordenadas == null) {
        throw new ValidacaoException(Map.of("endereco", "Não foi possível obter coordenadas para o endereço"));
      }
      pessoaJuridica.setCoordenadas(coordenadas);
    } catch (Exception e) {
      throw new ValidacaoException(Map.of("endereco", "Erro ao buscar coordenadas: " + e.getMessage()));
    }
  }
}