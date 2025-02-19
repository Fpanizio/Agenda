import { Component, EventEmitter, Output } from '@angular/core';
import { PfisicaService } from '../../service/PessoaFisica/pfisica.service';
import { PjuridicaService } from '../../service/PessoaJuridica/pjuridica.service';
import { PessoaFisica } from '../../models/pessoa-fisica.model';
import { PessoaJuridica } from '../../models/pessoa-juridica.model';
import { forkJoin, of } from 'rxjs';

@Component({
  selector: 'app-search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.css'],
  standalone: false
})
export class SearchBarComponent {
  searchQuery: string = '';
  @Output() searchResults = new EventEmitter<(PessoaFisica | PessoaJuridica)[]>();
  @Output() searchQueryChange = new EventEmitter<string>();

  constructor(
    private pfisicaService: PfisicaService,
    private pjuridicaService: PjuridicaService
  ) {}

  onInputChange(): void {
    this.searchQueryChange.emit(this.searchQuery);
    if (!this.searchQuery.trim()) {
      this.searchResults.emit([]);
      return;
    }

    const query = this.searchQuery.trim();
    
    if (query.length === 11) {
      this.buscarPorCpf(query);
    } else if (query.length === 14) {
      this.buscarPorCnpj(query);
    } else {
      this.buscarPorPrefixo(query);
    }
  }

  private buscarPorCpf(cpf: string): void {
    this.pfisicaService.getContactByCpf(cpf).subscribe({
      next: (data) => this.emitirResultados([data]),
      error: () => this.emitirResultados([])
    });
  }

  private buscarPorCnpj(cnpj: string): void {
    this.pjuridicaService.getContactByCnpj(cnpj).subscribe({
      next: (data) => this.emitirResultados([data]),
      error: () => this.emitirResultados([])
    });
  }

  private buscarPorPrefixo(query: string): void {
    const isNumerico = /^\d+$/.test(query);
    
    forkJoin([
      this.pfisicaService.filterContactsByCpfPrefix(query),
      isNumerico 
        ? this.pjuridicaService.filterContactsByCnpjPrefix(query)
        : of([])
    ]).subscribe(([pf, pj]) => {
      this.emitirResultados([...pf, ...pj]);
    });
  }

  private emitirResultados(resultados: (PessoaFisica | PessoaJuridica)[]): void {
    this.searchResults.emit(resultados);
  }
}