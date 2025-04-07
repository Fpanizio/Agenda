import { Component, OnInit } from '@angular/core';
import { PfisicaService } from '../../service/PessoaFisica/pfisica.service';
import { PjuridicaService } from '../../service/PessoaJuridica/pjuridica.service';
import { PessoaFisica } from '../../models/pessoa-fisica.model';
import { PessoaJuridica } from '../../models/pessoa-juridica.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-contact-list',
  templateUrl: './contact-list.component.html',
  styleUrls: ['./contact-list.component.css'],
  standalone: false,
})
export class ContactListComponent implements OnInit {
  tipoPessoa: 'fisica' | 'juridica' = 'fisica';
  contatosFisicos: PessoaFisica[] = [];
  contatosJuridicos: PessoaJuridica[] = [];
  resultadosPesquisa: (PessoaFisica | PessoaJuridica)[] = [];
  exibirResultadosPesquisa: boolean = false;
  nenhumResultadoEncontrado: boolean = false;
  searchQuery: string = '';
  mostrarModal: boolean = false;
  contatoIdParaExcluir: string | null = null;

  constructor(
    public pfService: PfisicaService,
    public pjService: PjuridicaService,
    public router: Router
  ) {}

  ngOnInit(): void {
    this.carregarContatos();
  }

  carregarContatos(): void {
    this.pfService.getContacts().subscribe({
      next: (data) => (this.contatosFisicos = data),
      error: (erro) => console.error('Erro ao carregar PF:', erro),
    });

    this.pjService.getContacts().subscribe({
      next: (data) => (this.contatosJuridicos = data),
      error: (erro) => console.error('Erro ao carregar PJ:', erro),
    });
  }

  onSearchQueryChange(query: string): void {
    const apenasNumeros = query.replace(/\D/g, '');
    this.searchQuery = apenasNumeros;
    this.exibirResultadosPesquisa = !!apenasNumeros;
    this.nenhumResultadoEncontrado = false;

    if (apenasNumeros) {
      this.filtrarResultados(apenasNumeros);
    } else {
      this.resultadosPesquisa = [];
    }
  }

  onSearchResults(results: (PessoaFisica | PessoaJuridica)[]): void {
    this.resultadosPesquisa = results.filter((contato) =>
      this.tipoPessoa === 'fisica'
        ? (contato as PessoaFisica).cpf !== undefined
        : (contato as PessoaJuridica).cnpj !== undefined
    );
    this.nenhumResultadoEncontrado = this.resultadosPesquisa.length === 0;
  }

  public filtrarResultados(query: string): void {
    const apenasNumeros = query.replace(/\D/g, ''); // Remove não numéricos
    const termo = apenasNumeros.toLowerCase();

    if (this.tipoPessoa === 'fisica') {
      if (termo.length >= 1 && termo.length <= 10) {
        this.pfService.filterContactsByCpfPrefix(termo).subscribe({
          next: (data) => {
            this.resultadosPesquisa = data;
            this.nenhumResultadoEncontrado = this.resultadosPesquisa.length === 0;
          },
          error: (erro) => console.error('Erro na busca por CPF:', erro),
        });
      } else if (termo.length === 11) {
        this.pfService.getContactByCpf(termo).subscribe({
          next: (data) => {
            this.resultadosPesquisa = [data]; // Converte para array
            this.nenhumResultadoEncontrado = this.resultadosPesquisa.length === 0;
          },
          error: (erro) => {
            console.error('Erro na busca exata por CPF:', erro);
            this.resultadosPesquisa = [];
            this.nenhumResultadoEncontrado = true;
          },
        });
      }
    } else {
      if (termo.length >= 1 && termo.length <= 13) {
        this.pjService.filterContactsByCnpjPrefix(termo).subscribe({
          next: (data) => {
            this.resultadosPesquisa = data;
            this.nenhumResultadoEncontrado = this.resultadosPesquisa.length === 0;
          },
          error: (erro) => console.error('Erro na busca por CNPJ:', erro),
        });
      } else if (termo.length === 14) {
        this.pjService.getContactByCnpj(termo).subscribe({
          next: (data) => {
            this.resultadosPesquisa = [data]; // Converte para array
            this.nenhumResultadoEncontrado = this.resultadosPesquisa.length === 0;
          },
          error: (erro) => {
            console.error('Erro na busca exata por CNPJ:', erro);
            this.resultadosPesquisa = [];
            this.nenhumResultadoEncontrado = true;
          },
        });
      }
    }
  }

  get contatosExibidos(): (PessoaFisica | PessoaJuridica)[] {
    return this.tipoPessoa === 'fisica'
      ? this.contatosFisicos
      : this.contatosJuridicos;
  }

  public editarContato(id: string): void {
    const rota = `/${
      this.tipoPessoa === 'fisica' ? 'editar-pf' : 'editar-pj'
    }/${id}`;
    this.router.navigate([rota]);
  }

  public abrirModalExclusao(id: string): void {
    this.contatoIdParaExcluir = id;
    this.mostrarModal = true;
  }

  public confirmarExclusao(): void {
    if (!this.contatoIdParaExcluir) return;

    const service =
      this.tipoPessoa === 'fisica' ? this.pfService : this.pjService;

    service.deleteContact(this.contatoIdParaExcluir).subscribe({
      next: () => {
        this.carregarContatos();
        this.fecharModal();
      },
      error: (erro) => console.error('Erro ao excluir:', erro),
    });
  }

  public fecharModal(): void {
    this.mostrarModal = false;
    this.contatoIdParaExcluir = null;
  }
}
