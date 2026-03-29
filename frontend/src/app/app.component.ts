import { Component } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'Beneficiários - Desafio Fullstack';
  navAberta = false;

  constructor(public router: Router) {}

  toggleNav(): void {
    this.navAberta = !this.navAberta;
  }

  fecharNav(): void {
    this.navAberta = false;
  }

  isActive(route: string): boolean {
    return this.router.url.includes(route);
  }
}
