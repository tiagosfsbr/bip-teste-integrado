import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Beneficio, TransferRequest } from '../models/beneficio.model';

@Injectable({
  providedIn: 'root'
})
export class BeneficioService {
  private apiUrl = 'http://localhost:8080/api/v1/beneficios';

  constructor(private http: HttpClient) {}

  /**
   * Obter todos os beneficiários
   */
  getAll(nome?: string): Observable<Beneficio[]> {
    let params = new HttpParams();
    if (nome) {
      params = params.set('nome', nome);
    }
    return this.http.get<Beneficio[]>(this.apiUrl, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Obter beneficiários ativos
   */
  getAtivos(): Observable<Beneficio[]> {
    return this.http.get<Beneficio[]>(`${this.apiUrl}/ativos`)
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Obter beneficiário por ID
   */
  getById(id: number): Observable<Beneficio> {
    return this.http.get<Beneficio>(`${this.apiUrl}/${id}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Criar novo beneficiário
   */
  create(beneficio: Beneficio): Observable<Beneficio> {
    return this.http.post<Beneficio>(this.apiUrl, beneficio)
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Atualizar beneficiário
   */
  update(id: number, beneficio: Beneficio): Observable<Beneficio> {
    return this.http.put<Beneficio>(`${this.apiUrl}/${id}`, beneficio)
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Deletar beneficiário
   */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Realizar transferência
   */
  transfer(transferRequest: TransferRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/transfer`, transferRequest, { responseType: 'text' })
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Obter saldo de um beneficiário
   */
  getBalance(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}/saldo`)
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Tratamento de erros
   */
  private handleError(error: any) {
    console.error('Erro na API:', error);
    if (error.status === 0) {
      console.error('Erro de conexão - servidor indisponível');
    } else if (error.status >= 400) {
      console.error(`Erro HTTP ${error.status}:`, error.error);
    }
    return throwError(() => error);
  }
}
