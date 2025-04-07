import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { EMPTY, Observable } from 'rxjs';
import { PessoaFisica } from '../../models/pessoa-fisica.model';

@Injectable({
  providedIn: 'root',
})
export class PfisicaService {
  private apiUrl = 'http://localhost:8080/api/pfisica';

  constructor(private http: HttpClient) {}

  getContacts(): Observable<PessoaFisica[]> {
    return this.http.get<PessoaFisica[]>(this.apiUrl);
  }

  getContactByCpf(cpf: string): Observable<PessoaFisica> {
    const cleanedCpf = cpf.replace(/\D/g, '');
    if (cleanedCpf.length !== 11) {
      return EMPTY; 
    }
    return this.http.get<PessoaFisica>(`${this.apiUrl}/${cleanedCpf}`);
  }

  filterContactsByCpfPrefix(prefixo: string): Observable<PessoaFisica[]> {
    return this.http.get<PessoaFisica[]>(
      `${this.apiUrl}/filtrar-por-cpf?prefixo=${prefixo}`
    );
  }

  addContact(pessoa: PessoaFisica): Observable<PessoaFisica> {
    return this.http.post<PessoaFisica>(this.apiUrl, pessoa);
  }

  updateContact(cpf: string, pessoa: PessoaFisica): Observable<PessoaFisica> {
    return this.http.put<PessoaFisica>(`${this.apiUrl}/${cpf}`, pessoa, {
      headers: { 'Content-Type': 'application/json' },
    });
  }

  deleteContact(cpf: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${cpf}`);
  }
}
