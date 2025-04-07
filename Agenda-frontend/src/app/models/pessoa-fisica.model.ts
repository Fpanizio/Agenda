export interface PessoaFisica {
  cpf: string;
  nome: string;
  dataNascimento: string;
  telefone: string;
  cep: string;
  email: string;
  logradouro: string;
  numeroEndereco: string;
  complemento?: string;
  bairro: string;
  cidade: string;
  estado: string;
  coordenadas?: any; // Pode ser ajustado para um tipo mais específico, como { latitude: number; longitude: number }
}