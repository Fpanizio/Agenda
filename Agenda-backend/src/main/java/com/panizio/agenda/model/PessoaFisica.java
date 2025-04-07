package com.panizio.agenda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.panizio.agenda.utils.PointSerializer;

@Entity
public class PessoaFisica {

  @Id
  @NotBlank(message = "CPF é obrigatório")
  private String cpf;

  @NotBlank(message = "Nome é obrigatório")
  private String nome;

  @NotNull(message = "Data de nascimento é obrigatória")
  private LocalDate dataNascimento;

  @NotBlank(message = "Telefone é obrigatório")
  private String telefone;

  @NotBlank(message = "CEP é obrigatório")
  private String cep;

  @Column(unique = true)
  @NotBlank(message = "E-mail é obrigatório")
  private String email;

  @NotBlank(message = "Numero do endereço é obrigatório")
  private String numeroEndereco;

  @NotBlank(message = "Bairro é obrigatório")
  private String bairro;

  @NotBlank(message = "Logradouro é obrigatório")
  private String logradouro;

  private String complemento;

  @NotBlank(message = "Cidade é obrigatória")
  private String cidade;

  @NotBlank(message = "Estado é obrigatório")
  private String estado;

  @JsonSerialize(using = PointSerializer.class)
  private Point coordenadas;

  public PessoaFisica() {
  }

  public PessoaFisica(
      String cpf,
      String nome,
      LocalDate dataNascimento,
      String telefone,
      String cep,
      String email,
      String logradouro,
      String numeroEndereco,
      String complemento,
      String bairro,
      String cidade,
      String estado,
      Point coordenadas) {
    this.cpf = cpf;
    this.nome = nome;
    this.dataNascimento = dataNascimento;
    this.telefone = telefone;
    this.cep = cep;
    this.email = email;
    this.logradouro = logradouro;
    this.numeroEndereco = numeroEndereco;
    this.complemento = complemento;
    this.bairro = bairro;
    this.cidade = cidade;
    this.estado = estado;
    this.coordenadas = coordenadas;
  }

  public String getCpf() {
    return cpf;
  }

  public void setCpf(String cpf) {
    this.cpf = cpf;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public LocalDate getDataNascimento() {
    return dataNascimento;
  }

  public void setDataNascimento(LocalDate dataNascimento) {
    this.dataNascimento = dataNascimento;
  }

  public String getTelefone() {
    return telefone;
  }

  public void setTelefone(String telefone) {
    this.telefone = telefone;
  }

  public String getCep() {
    return cep;
  }

  public void setCep(String cep) {
    this.cep = cep;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Point getCoordenadas() {
    return coordenadas;
  }

  public void setCoordenadas(Point coordenadas) {
    this.coordenadas = coordenadas;
  }

  public String getLogradouro() {
    return logradouro;
  }

  public void setLogradouro(String logradouro) {
    this.logradouro = logradouro;
  }

  public String getComplemento() {
    return complemento;
  }

  public void setComplemento(String complemento) {
    this.complemento = complemento;
  }

  public String getBairro() {
    return bairro;
  }

  public void setBairro(String bairro) {
    this.bairro = bairro;
  }

  public String getCidade() {
    return cidade;
  }

  public void setCidade(String cidade) {
    this.cidade = cidade;
  }

  public String getEstado() {
    return estado;
  }

  public void setEstado(String estado) {
    this.estado = estado;
  }

  public String getNumeroEndereco() {
    return numeroEndereco;
  }

  public void setNumeroEndereco(String numeroEndereco) {
    this.numeroEndereco = numeroEndereco;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    PessoaFisica that = (PessoaFisica) o;
    return Objects.equals(cpf, that.cpf) &&
        Objects.equals(nome, that.nome) &&
        Objects.equals(dataNascimento, that.dataNascimento);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cpf, nome, dataNascimento);
  }

  @Override
  public String toString() {
    return "PessoaFisica{" +
        "cpf='" + cpf + '\'' +
        ", nome='" + nome + '\'' +
        ", dataNascimento=" + dataNascimento +
        '}';
  }
}