package com.panizio.agenda.model;

import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.panizio.agenda.utils.PointSerializer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
public class PessoaJuridica {

  @Id
  @NotBlank(message = "CNPJ é obrigatório")
  private String cnpj;

  @NotBlank(message = "Razão Social é obrigatório")
  private String razaoSocial;

  @NotBlank(message = "Nome Fantasia é obrigatório")
  private String nomeFantasia;

  @NotBlank(message = "Telefone é obrigatório")
  private String telefone;

  @NotBlank(message = "Email é obrigatório")
  @Column(unique = true)
  private String email;

  @NotBlank(message = "CEP é obrigatório")
  private String cep;

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

  public PessoaJuridica() {
  }

  public PessoaJuridica(
      String cnpj, String razaoSocial, String nomeFantasia, String telefone, String email,
      String cep, String numeroEndereco, String bairro, String logradouro, String complemento,
      String cidade, String estado) {
    this.cnpj = cnpj;
    this.razaoSocial = razaoSocial;
    this.nomeFantasia = nomeFantasia;
    this.telefone = telefone;
    this.email = email;
    this.cep = cep;
    this.numeroEndereco = numeroEndereco;
    this.bairro = bairro;
    this.logradouro = logradouro;
    this.complemento = complemento;
    this.cidade = cidade;
    this.estado = estado;
  }

  // Getters e Setters
  public String getCnpj() {
    return cnpj;
  }

  public void setCnpj(String cnpj) {
    this.cnpj = cnpj;
  }

  public String getRazaoSocial() {
    return razaoSocial;
  }

  public void setRazaoSocial(String razaoSocial) {
    this.razaoSocial = razaoSocial;
  }

  public String getNomeFantasia() {
    return nomeFantasia;
  }

  public void setNomeFantasia(String nomeFantasia) {
    this.nomeFantasia = nomeFantasia;
  }

  public String getTelefone() {
    return telefone;
  }

  public void setTelefone(String telefone) {
    this.telefone = telefone;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getCep() {
    return cep;
  }

  public void setCep(String cep) {
    this.cep = cep;
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
}