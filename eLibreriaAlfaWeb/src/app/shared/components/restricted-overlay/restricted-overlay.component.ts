import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-restricted-overlay',
  standalone: true,
  imports: [CommonModule, ButtonModule],
  templateUrl: './restricted-overlay.component.html',
  styleUrls: ['./restricted-overlay.component.scss']
})
export class RestrictedOverlayComponent {
  @Input() registrationPath: string = '/registro'; 

  constructor(private router: Router) {}

  navigateToRegister(): void {
    this.router.navigate([this.registrationPath]);
  }
}
