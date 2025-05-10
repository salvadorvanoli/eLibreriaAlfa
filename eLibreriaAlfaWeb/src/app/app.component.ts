import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SeguridadService } from './core/services/seguridad.service';
import { NavbarComponent } from "./shared/components/navbar/navbar.component";
import { FooterComponent } from "./shared/components/footer/footer.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    NavbarComponent,
    FooterComponent
],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'eLibreriaAlfaWeb';

  constructor(
    private seguridadService: SeguridadService
  ) {}

  ngOnInit() {
    // Inicializa el estado del usuario al cargar la aplicación
    this.seguridadService.getActualUser().subscribe(user => {
      console.log('Usuario inicializado:', user);
    });
  }

}
