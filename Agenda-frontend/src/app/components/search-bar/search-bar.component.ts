import { Component, EventEmitter, Input, Output } from '@angular/core';
import { PfisicaService } from '../../service/PessoaFisica/pfisica.service';
import { PjuridicaService } from '../../service/PessoaJuridica/pjuridica.service';
import { PessoaFisica } from '../../models/pessoa-fisica.model';
import { PessoaJuridica } from '../../models/pessoa-juridica.model';
import { forkJoin, of } from 'rxjs';

@Component({
  selector: 'app-search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.css'],
  standalone: false,
})
export class SearchBarComponent {
  @Input() tipoPessoa: 'fisica' | 'juridica' = 'fisica'; // Recebe o tipo de pessoa
  @Output() searchResults = new EventEmitter<
    (PessoaFisica | PessoaJuridica)[]
  >();
  @Output() searchQueryChange = new EventEmitter<string>();

  searchQuery: string = '';

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

    if (this.tipoPessoa === 'fisica') {
      this.buscarPorCpf(query);
    } else if (this.tipoPessoa === 'juridica') {
      this.buscarPorCnpj(query);
    }
  }

  private buscarPorCpf(cpf: string): void {
    this.pfisicaService.getContactByCpf(cpf).subscribe({
      next: (data) => this.searchResults.emit([data]),
      error: () => this.searchResults.emit([]),
    });
  }

  private buscarPorCnpj(cnpj: string): void {
    this.pjuridicaService.getContactByCnpj(cnpj).subscribe({
      next: (data) => this.searchResults.emit([data]),
      error: () => this.searchResults.emit([]),
    });
  }
}
