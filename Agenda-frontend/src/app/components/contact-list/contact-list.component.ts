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
  contatosFisicos: PessoaFisica[] = [];
  contatosJuridicos: PessoaJuridica[] = [];
  resultadosPesquisa: (PessoaFisica | PessoaJuridica)[] = [];
  exibirResultadosPesquisa: boolean = false;
  nenhumResultadoEncontrado: boolean = false;
  searchQuery: string = '';
  mostrarModal: boolean = false;
  contatoIdParaExcluir: string | null = null;
  tipoPessoa: 'fisica' | 'juridica' = 'fisica';

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
    this.searchQuery = query;
    this.exibirResultadosPesquisa = !!query.trim();
    this.nenhumResultadoEncontrado = false;
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
    const termo = query.toLowerCase();
    const dados =
      this.tipoPessoa === 'fisica'
        ? this.contatosFisicos
        : this.contatosJuridicos;

    this.resultadosPesquisa = dados.filter((contato) =>
      this.tipoPessoa === 'fisica'
        ? this.filtrarPessoaFisica(contato as PessoaFisica, termo)
        : this.filtrarPessoaJuridica(contato as PessoaJuridica, termo)
    );

    this.nenhumResultadoEncontrado = this.resultadosPesquisa.length === 0;
  }

  public filtrarPessoaFisica(contato: PessoaFisica, termo: string): boolean {
    return (
      contato.nome.toLowerCase().includes(termo) ||
      contato.cpf.includes(termo) ||
      contato.email.toLowerCase().includes(termo) ||
      contato.telefone.includes(termo) ||
      contato.cep.includes(termo)
    );
  }

  public filtrarPessoaJuridica(
    contato: PessoaJuridica,
    termo: string
  ): boolean {
    return (
      contato.razaoSocial.toLowerCase().includes(termo) ||
      contato.cnpj.includes(termo) ||
      contato.nomeFantasia.toLowerCase().includes(termo) ||
      contato.email.toLowerCase().includes(termo) ||
      contato.telefone.includes(termo) ||
      contato.cep.includes(termo)
    );
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
