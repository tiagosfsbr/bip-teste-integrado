import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Beneficio } from '../../models/beneficio.model';
import { BeneficioService } from '../../services/beneficio.service';

@Component({
  selector: 'app-lista-beneficiarios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './lista-beneficiarios.component.html',
  styleUrls: ['./lista-beneficiarios.component.scss']
})
export class ListaBeneficiariosComponent implements OnInit {
  beneficiarios: Beneficio[] = [];
  filtroNome: string = '';
  loading: boolean = false;
  erro: string | null = null;
  sucesso: string | null = null;
  
  // Formulário de criação
  mostrarFormulario: boolean = false;
  novobeneficio: Partial<Beneficio> = {
    nome: '',
    descricao: '',
    valor: 0,
    ativo: true
  };
  carregandoFormulario: boolean = false;

  constructor(private beneficioService: BeneficioService) {}

  ngOnInit(): void {
    this.carregarBeneficiarios();
  }

  carregarBeneficiarios(): void {
    this.loading = true;
    this.erro = null;

    this.beneficioService.getAll(this.filtroNome).subscribe({
      next: (data) => {
        this.beneficiarios = data;
        this.loading = false;
      },
      error: (error) => {
        this.erro = 'Erro ao carregar beneficiários';
        this.loading = false;
        console.error(error);
      }
    });
  }

  buscarPorNome(): void {
    this.carregarBeneficiarios();
  }

  limparFiltro(): void {
    this.filtroNome = '';
    this.carregarBeneficiarios();
  }

  deletar(id: number): void {
    if (confirm('Tem certeza que deseja deletar este beneficiário?')) {
      this.beneficioService.delete(id).subscribe({
        next: () => {
          this.sucesso = 'Beneficiário deletado com sucesso';
          this.carregarBeneficiarios();
          setTimeout(() => this.sucesso = null, 3000);
        },
        error: (error) => {
          this.erro = 'Erro ao deletar beneficiário';
          console.error(error);
        }
      });
    }
  }

  abrirDetalhes(id: number): void {
    // Implementar navegação para detalhes
    console.log('Abrir detalhes do beneficiário:', id);
  }

  abrirFormulario(): void {
    this.mostrarFormulario = true;
    this.novobeneficio = {
      nome: '',
      descricao: '',
      valor: 0,
      ativo: true
    };
    this.erro = null;
  }

  fecharFormulario(): void {
    this.mostrarFormulario = false;
    this.novobeneficio = {};
    this.erro = null;
  }

  salvarBeneficiario(): void {
    if (!this.novobeneficio.nome || !this.novobeneficio.descricao) {
      this.erro = 'Nome e Descrição são obrigatórios';
      return;
    }

    if (!this.novobeneficio.valor || this.novobeneficio.valor <= 0) {
      this.erro = 'Valor deve ser maior que zero';
      return;
    }

    this.carregandoFormulario = true;
    this.erro = null;

    const beneficio: Beneficio = {
      id: 0,
      nome: this.novobeneficio.nome!,
      descricao: this.novobeneficio.descricao!,
      valor: this.novobeneficio.valor!,
      ativo: this.novobeneficio.ativo ?? true,
      version: 0
    };

    this.beneficioService.create(beneficio).subscribe({
      next: () => {
        this.sucesso = 'Beneficiário criado com sucesso!';
        this.carregarBeneficiarios();
        this.fecharFormulario();
        setTimeout(() => this.sucesso = null, 3000);
      },
      error: (error) => {
        this.erro = error.error?.message || 'Erro ao criar beneficiário';
        console.error(error);
      },
      complete: () => {
        this.carregandoFormulario = false;
      }
    });
  }
}
