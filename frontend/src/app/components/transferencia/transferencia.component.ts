import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Beneficio, TransferRequest } from '../../models/beneficio.model';
import { BeneficioService } from '../../services/beneficio.service';

@Component({
  selector: 'app-transferencia',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './transferencia.component.html',
  styleUrls: ['./transferencia.component.scss']
})
export class TransferenciaComponent implements OnInit {
  beneficiarios: Beneficio[] = [];
  fromId: number | null = null;
  toId: number | null = null;
  amount: number | null = null;
  loading: boolean = false;
  erro: string | null = null;
  sucesso: string | null = null;
  saldoOrigem: number | null = null;
  saldoDestino: number | null = null;

  constructor(private beneficioService: BeneficioService) {}

  ngOnInit(): void {
    this.carregarBeneficiarios();
  }

  carregarBeneficiarios(): void {
    this.beneficioService.getAll().subscribe({
      next: (data) => {
        this.beneficiarios = data.filter(b => b.ativo);
      },
      error: (error) => {
        this.erro = 'Erro ao carregar beneficiários';
        console.error(error);
      }
    });
  }

  atualizarSaldoOrigem(): void {
    if (this.fromId) {
      this.beneficioService.getBalance(this.fromId).subscribe({
        next: (data) => {
          this.saldoOrigem = data.saldo;
        },
        error: () => {
          this.saldoOrigem = null;
        }
      });
    }
  }

  atualizarSaldoDestino(): void {
    if (this.toId) {
      this.beneficioService.getBalance(this.toId).subscribe({
        next: (data) => {
          this.saldoDestino = data.saldo;
        },
        error: () => {
          this.saldoDestino = null;
        }
      });
    }
  }

  processarTransferencia(): void {
    if (!this.fromId || !this.toId || !this.amount) {
      this.erro = 'Todos os campos são obrigatórios';
      return;
    }

    if (this.amount <= 0) {
      this.erro = 'Valor deve ser maior que zero';
      return;
    }

    if (this.fromId === this.toId) {
      this.erro = 'Origem e destino não podem ser iguais';
      return;
    }

    const request: TransferRequest = {
      fromId: this.fromId,
      toId: this.toId,
      amount: this.amount
    };

    this.loading = true;
    this.erro = null;
    this.sucesso = null;

    this.beneficioService.transfer(request).subscribe({
      next: () => {
        this.sucesso = 'Transferência realizada com sucesso!';
        this.limparFormulario();
        this.loading = false;
        this.carregarBeneficiarios();
        setTimeout(() => this.sucesso = null, 5000);
      },
      error: (error) => {
        this.erro = error.error || 'Erro ao realizar transferência';
        this.loading = false;
        console.error(error);
      }
    });
  }

  limparFormulario(): void {
    this.fromId = null;
    this.toId = null;
    this.amount = null;
    this.saldoOrigem = null;
    this.saldoDestino = null;
  }

  getNomeBeneficiarioOrigem(): string {
    if (!this.fromId) return '';
    const beneficiario = this.beneficiarios.find(b => b.id === this.fromId);
    return beneficiario?.nome || '';
  }

  getNomeBeneficiarioDestino(): string {
    if (!this.toId) return '';
    const beneficiario = this.beneficiarios.find(b => b.id === this.toId);
    return beneficiario?.nome || '';
  }

  onFromChange(event: any): void {
    this.fromId = this.fromId ? Number(this.fromId) : null;
    this.atualizarSaldoOrigem();
  }

  onToChange(event: any): void {
    this.toId = this.toId ? Number(this.toId) : null;
    this.atualizarSaldoDestino();
  }
}
