import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { EMPTY, Observable } from 'rxjs';
import { PessoaJuridica } from '../../models/pessoa-juridica.model';

@Injectable({
  providedIn: 'root',
})
export class PjuridicaService {
  private apiUrl = 'http://localhost:8080/api/pjuridica';

  constructor(private http: HttpClient) {}

  getContacts(): Observable<PessoaJuridica[]> {
    return this.http.get<PessoaJuridica[]>(this.apiUrl);
  }

  getContactByCnpj(cnpj: string): Observable<PessoaJuridica> {
    const cleanedCnpj = cnpj.replace(/\D/g, '');
    if (cleanedCnpj.length !== 14) {
      return EMPTY; 
    }
    return this.http.get<PessoaJuridica>(`${this.apiUrl}/${cleanedCnpj}`);
  }

  filterContactsByCnpjPrefix(prefixo: string): Observable<PessoaJuridica[]> {
    return this.http.get<PessoaJuridica[]>(
      `${this.apiUrl}/filtrar-por-cnpj?prefixo=${prefixo}`
    );
  }

  addContact(pessoa: PessoaJuridica): Observable<PessoaJuridica> {
    return this.http.post<PessoaJuridica>(this.apiUrl, pessoa);
  }

  updateContact(
    cnpj: string,
    pessoa: PessoaJuridica
  ): Observable<PessoaJuridica> {
    return this.http.put<PessoaJuridica>(`${this.apiUrl}/${cnpj}`, pessoa, {
      headers: { 'Content-Type': 'application/json' },
    });
  }

  deleteContact(cnpj: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${cnpj}`);
  }
}
