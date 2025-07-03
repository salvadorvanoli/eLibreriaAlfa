import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Comentario } from '../../../../core/models/publicacion';
import { SecurityService } from '../../../../core/services/security.service';
import { PublicationService } from '../../../../core/services/publication.service';

@Component({
  selector: 'app-comment-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './comment-card.component.html',
  styleUrls: ['./comment-card.component.scss']
})
export class CommentCardComponent {
  
  @Input() comentario!: Comentario;
  @Output() commentDeleted = new EventEmitter<number>();

  constructor(
    private securityService: SecurityService,
    private publicationService: PublicationService
  ) {}

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

  canDeleteComment(): boolean {
    const currentUser = this.securityService.actualUser;
    if (!currentUser || !this.comentario?.usuario) {
      return false;
    }
    
    return currentUser.id === this.comentario.usuario.id;
  }

  deleteComment(): void {
    if (!this.canDeleteComment()) {
      return;
    }

    if (confirm('¿Estás seguro de que deseas eliminar este comentario?')) {
      this.publicationService.deleteComment(this.comentario.id).subscribe({
        next: () => {
          this.commentDeleted.emit(this.comentario.id);
        },
        error: (error) => {
          console.error('Error al eliminar comentario:', error);
          alert('Error al eliminar el comentario. Por favor, intenta de nuevo.');
        }
      });
    }
  }
}
