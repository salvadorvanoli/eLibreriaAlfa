import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Comentario } from '../../../../core/models/publicacion';

@Component({
  selector: 'app-comment-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './comment-card.component.html',
  styleUrls: ['./comment-card.component.scss']
})
export class CommentCardComponent {
  
  @Input() comentario!: Comentario;

  formatDate(date: Date): string {
    return new Date(date).toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getUserDisplayName(usuario: any): string {
    if (!usuario) {
      return 'Usuario Anónimo';
    }
    
    if (usuario.nombre) {
      return usuario.nombre;
    }
    
    if (usuario.email) {
      return usuario.email.split('@')[0];
    }
    
    if (usuario.username) {
      return usuario.username;
    }
    
    if (typeof usuario === 'number' || typeof usuario === 'string') {
      return `Usuario ${usuario}`;
    }
    
    return 'Usuario';
  }
}
