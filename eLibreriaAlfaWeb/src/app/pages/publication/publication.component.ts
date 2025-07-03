import { Component, OnInit, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PublicationService } from '../../core/services/publication.service';
import { SecurityService } from '../../core/services/security.service';
import { CommentCardComponent } from './components/comment-card/comment-card.component';
import { NewCommentFormComponent } from './components/new-comment-form/new-comment-form.component';
import { Comentario, Publicacion } from '../../core/models/publicacion';
import { PaginatorModule } from 'primeng/paginator';
import { ImageService } from '../../core/services/image.service';

@Component({
  selector: 'app-publication',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    CommentCardComponent,
    NewCommentFormComponent,
    PaginatorModule
  ],
  templateUrl: './publication.component.html',
  styleUrls: ['./publication.component.scss']
})
export class PublicationComponent implements OnInit {
  
    publicacion: Publicacion | null = null;
    loading: boolean = false;
    error: string | null = null;
    
    newComment: string = '';
    submittingComment: boolean = false;
    isLoggedIn: boolean = false;

    currentPage: number = 0;
    commentsPerPage: number = 5;
    totalComments: number = 0;
    paginatedComments: Comentario[] = [];

    defaultImageUrl: string = 'https://www.shutterstock.com/image-vector/default-ui-image-placeholder-wireframes-600nw-1037719192.jpg';
    publicacionImageUrl: string | undefined = undefined;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private publicationService: PublicationService,
        private securityService: SecurityService,
        private imageService: ImageService
    ) {}

    ngOnInit(): void {
        this.checkAuthStatus();
        this.loadPublication();
    }

    checkAuthStatus(): boolean {
        this.isLoggedIn = this.securityService.isAuthenticated();
        return this.isLoggedIn;
    }

    loadPublication(): void {
        const id = this.route.snapshot.paramMap.get('id');
        if (!id) {
            this.error = 'ID de publicación no válido';
            return;
        }

        this.loading = true;
        this.error = null;
        
        this.publicationService.getById(+id).subscribe({
        next: (publication) => {
            let publicacionData = Array.isArray(publication) ? publication : Object.values(publication);
            
            if (publicacionData.length > 0 && Array.isArray(publicacionData[0])) {
                this.publicacion = this.mapToPublicacion(publicacionData[0][0]);
            } else if (publicacionData.length > 0) {
                this.publicacion = this.mapToPublicacion(publicacionData[0]);
            } else {
                this.publicacion = null;
                this.error = 'Publicación no encontrada';
            }
            
            // Configurar paginación después de asignar la publicación
            if (this.publicacion) {
                this.setupPagination(this.publicacion.comentarios || []);
                if (this.publicacion.imagenUrl)
                    this.publicacionImageUrl = this.imageService.getImageUrl(this.publicacion.imagenUrl)
            }
            
            this.loading = false;
        },
        error: (error) => {
            this.error = 'No se pudo cargar la publicación';
            this.loading = false;
        }
        });
    }

    private mapToPublicacion(data: any): Publicacion {
        const publicacion = {
            id: data.id || 0,
            titulo: data.titulo || 'Sin título',
            contenido: data.contenido || '',
            fechaCreacion: data.fechaCreacion ? new Date(data.fechaCreacion) : new Date(),
            imagenUrl: data.imagenUrl || undefined,
            comentarios: data.comentarios ? data.comentarios.map((comentario: any) => this.mapToComentario(comentario)) : []
        };

        this.setupPagination(publicacion.comentarios || []);

        return publicacion;
    }
    
    private setupPagination(comentarios: Comentario[]): void {
        this.totalComments = comentarios.length;
        this.currentPage = 0;
        this.updatePaginatedComments(comentarios);
    }

    private updatePaginatedComments(comentarios?: Comentario[]): void {
        // Usar los comentarios pasados como parámetro o los de la publicación
        const commentsToUse = comentarios || this.publicacion?.comentarios || [];
        
        if (commentsToUse.length === 0) {
            this.paginatedComments = [];
            return;
        }

        const startIndex = this.currentPage * this.commentsPerPage;
        const endIndex = startIndex + this.commentsPerPage;
        this.paginatedComments = commentsToUse.slice(startIndex, endIndex);
    }

    private mapToComentario(data: any): Comentario {
        return {
            id: data.id || 0,
            texto: data.texto || '',
            fechaCreacion: data.fechaCreacion ? new Date(data.fechaCreacion) : new Date(),
            usuario: data.usuario ? this.mapToUsuario(data.usuario) : null,
            publicacion: data.publicacion || 0,
            titulo: data.titulo || 'Sin título'
        };
    }

    // Método para mapear usuario (si tienes un modelo Usuario)
    private mapToUsuario(data: any): any {
        return {
            id: data.id || 0,
            email: data.email || '',
            nombre: data.nombre || data.email?.split('@')[0] || 'Usuario'
        };
    }

    goBack(): void {
        this.router.navigate(['/foro']);
    }

    formatDate(date: Date): string {
        return new Date(date).toLocaleDateString('es-ES', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    onPageChange(event: any): void {
        this.currentPage = event.page;
        this.commentsPerPage = event.rows;
        this.updatePaginatedComments();
    }

    onCommentDeleted(commentId: number): void {
        if (this.publicacion?.comentarios) {
            this.publicacion.comentarios = this.publicacion.comentarios.filter(
                comentario => comentario.id !== commentId
            );
            
            this.setupPagination(this.publicacion.comentarios);
            
            const maxPage = Math.ceil(this.totalComments / this.commentsPerPage) - 1;
            if (this.currentPage > maxPage && maxPage >= 0) {
                this.currentPage = maxPage;
                this.updatePaginatedComments();
            }
        }
    }
}
