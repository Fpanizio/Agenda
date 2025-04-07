export interface PessoaJuridica {
  cnpj: string;
  razaoSocial: string;
  nomeFantasia: string;
  telefone: string;
  email: string;
  cep: string;
  logradouro: string;
  numeroEndereco: string;
  complemento?: string;
  bairro: string;
  cidade: string;
  estado: string;
  coordenadas?: any; // Pode ser ajustado para um tipo mais específico, como { latitude: number; longitude: number }
}