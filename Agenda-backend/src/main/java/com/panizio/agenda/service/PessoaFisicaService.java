package com.panizio.agenda.service;

import com.panizio.agenda.exception.ValidacaoException;
import com.panizio.agenda.model.PessoaFisica;
import com.panizio.agenda.repository.PessoaFisicaRepository;
import com.panizio.agenda.utils.ValidacaoUtils;

import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Service
public class PessoaFisicaService {

  @Autowired
  private PessoaFisicaRepository pessoaFisicaRepository;
  private final EmailService emailService;

  public PessoaFisicaService(
      PessoaFisicaRepository pessoaFisicaRepository,
      EmailService emailService) {
    this.pessoaFisicaRepository = pessoaFisicaRepository;
    this.emailService = emailService;
  }

  public List<PessoaFisica> listarUsuarios() {
    return pessoaFisicaRepository.findAll();
  }

  public PessoaFisica buscarUsuarioPorCpf(String cpf) {
    return pessoaFisicaRepository.findById(limpar(cpf)).orElse(null);
  }

  public List<PessoaFisica> filtrarPorCpf(String prefixo) {
    return pessoaFisicaRepository.findByCpfStartingWith(prefixo);
  }

  public PessoaFisica salvarUsuario(PessoaFisica pessoaFisica) {
    pessoaFisica.setCpf(limpar(pessoaFisica.getCpf()));

    if (!ValidacaoUtils.validarCEP(pessoaFisica.getCep())) {
      throw new ValidacaoException(Map.of("cep", "CEP inválido"));
    }

    buscarCoordenadas(pessoaFisica);

    validarPessoaFisica(pessoaFisica, true);

    PessoaFisica savedPessoa = pessoaFisicaRepository.save(pessoaFisica);

    emailService.enviarEmailConfirmacao(savedPessoa.getNome(), savedPessoa.getEmail());

    return savedPessoa;
  }

  public PessoaFisica atualizarPessoaFisica(String cpf, PessoaFisica novosDados) {
    novosDados.setCpf(limpar(novosDados.getCpf()));
    PessoaFisica pessoaExistente = pessoaFisicaRepository.findById(cpf)
        .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

    if (novosDados.getCep() != null && !novosDados.getCep().equals(pessoaExistente.getCep())) {
      if (!ValidacaoUtils.validarCEP(novosDados.getCep())) {
        throw new ValidacaoException(Map.of("cep", "CEP inválido"));
      }
      buscarCoordenadas(novosDados);
    }

    validarCamposUnicos(novosDados, pessoaExistente);
    validarPessoaFisica(novosDados, false);
    atualizarCampos(pessoaExistente, novosDados);

    return pessoaFisicaRepository.save(pessoaExistente);
  }

  public void excluirUsuario(String cpf) {
    pessoaFisicaRepository.deleteById(cpf);
  }

  private void buscarCoordenadas(PessoaFisica pessoaFisica) {
    try {
      String enderecoCompleto = pessoaFisica.getLogradouro() + ", " +
          pessoaFisica.getNumeroEndereco() + ", " +
          pessoaFisica.getBairro() + ", " +
          pessoaFisica.getCidade() + ", " +
          pessoaFisica.getEstado() + ", " +
          pessoaFisica.getCep();
      Point coordenadas = ValidacaoUtils.buscarCoordenadasPorEndereco(enderecoCompleto);
      if (coordenadas == null) {
        throw new ValidacaoException(Map.of("endereco", "Não foi possível obter coordenadas para o endereço"));
      }
      pessoaFisica.setCoordenadas(coordenadas);
    } catch (Exception e) {
      throw new ValidacaoException(Map.of("endereco", "Erro ao buscar coordenadas: " + e.getMessage()));
    }
  }

  private void validarPessoaFisica(PessoaFisica pessoaFisica, boolean isNovo) {
    Map<String, String> erros = new HashMap<>();

    validarCampo(pessoaFisica.getCpf(), ValidacaoUtils::validarCPF, "cpf", "CPF inválido", erros);
    validarCampo(pessoaFisica.getEmail(), ValidacaoUtils::validarEmail, "email", "E-mail inválido", erros);
    validarCampo(pessoaFisica.getDataNascimento() != null ? pessoaFisica.getDataNascimento().toString() : null,
        ValidacaoUtils::validarDataNascimento, "dataNascimento", "Data inválida", erros);
    validarCampo(pessoaFisica.getCep(), ValidacaoUtils::validarCEP, "cep", "CEP inválido", erros);
    validarCampo(pessoaFisica.getTelefone(), ValidacaoUtils::validarTelefone, "telefone", "Telefone inválido", erros);
    validarCampo(pessoaFisica.getNome(), ValidacaoUtils::validarNome, "nome", "Nome inválido", erros);

    validarCampo(pessoaFisica.getLogradouro(), ValidacaoUtils::validarTexto, "endereco", "Endereço inválido", erros);
    validarCampo(pessoaFisica.getNumeroEndereco(), ValidacaoUtils::validarTexto, "numero", "Número inválido", erros);
    validarCampo(pessoaFisica.getBairro(), ValidacaoUtils::validarTexto, "bairro", "Bairro inválido", erros);
    validarCampo(pessoaFisica.getCidade(), ValidacaoUtils::validarTexto, "cidade", "Cidade inválida", erros);
    validarCampo(pessoaFisica.getEstado(), ValidacaoUtils::validarTexto, "estado", "Estado inválido", erros);

    if (isNovo) {
      validarExistenciaCampo(pessoaFisica.getCpf(), "cpf", "CPF já cadastrado");
      validarExistenciaCampo(pessoaFisica.getEmail(), "email", "E-mail já cadastrado");
    }

    if (!erros.isEmpty()) {
      throw new ValidacaoException(erros);
    }
  }

  private void validarCamposUnicos(PessoaFisica novaPessoa, PessoaFisica pessoaExistente) {
    if (novaPessoa.getCpf() != null && !novaPessoa.getCpf().equals(pessoaExistente.getCpf())) {
      validarExistenciaCampo(novaPessoa.getCpf(), "cpf", "CPF já cadastrado.");
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
      boolean exists = field.equals("cpf")
          ? pessoaFisicaRepository.existsById(value)
          : pessoaFisicaRepository.findByEmail(value).isPresent();

      if (exists) {
        Map<String, String> error = new HashMap<>();
        error.put(field, message);
        throw new ValidacaoException(error);
      }
    }
  }

  private void atualizarCampos(PessoaFisica existente, PessoaFisica novosDados) {
    if (novosDados.getNome() != null) {
      existente.setNome(novosDados.getNome());
    }
    if (novosDados.getDataNascimento() != null) {
      existente.setDataNascimento(novosDados.getDataNascimento());
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
}