import { Component, Input, Output, EventEmitter, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Publicacion } from '../../../../core/models/publicacion';
import { ImageService } from '../../../../core/services/image.service';

@Component({
  selector: 'app-publication-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './publication-card.component.html',
  styleUrls: ['./publication-card.component.scss']
})
export class PublicationCardComponent {
  
    publicacionImageUrl: string | undefined = undefined;

    @Input() publicacion!: Publicacion;
    @Input() maxContentLength: number = 150;
    defaultImageUrl: string = 'https://www.shutterstock.com/image-vector/default-ui-image-placeholder-wireframes-600nw-1037719192.jpg';
    @Output() cardClicked = new EventEmitter<number>();
    
    constructor(
        private imageService: ImageService
    ) {}

    ngOnChanges(changes: SimpleChanges) {
        if (changes['publicacion']) {
            if (this.publicacion && this.publicacion.imagenUrl) {
                this.publicacionImageUrl = this.imageService.getImageUrl(this.publicacion.imagenUrl);
            }
        }
    }

    onImageError(event: any): void {
        event.target.onerror = null;
        event.target.src = this.defaultImageUrl;
    }

    hasImage(): boolean {
        return !!this.publicacion.imagenUrl;
    }

    onCardClick(): void {
        if (this.publicacion?.id) {
            this.cardClicked.emit(this.publicacion.id);
        }
    }

    formatDate(dateString: string | Date): string {
        
        if (dateString instanceof Date) {
            return dateString.toLocaleDateString('es-ES', {
                year: 'numeric',
                month: 'long',
                day: 'numeric'
            });
        }
        
        let date: Date;
        
        if (typeof dateString === 'string') {
            const dateStr = dateString.includes('Z') || dateString.includes('+') || dateString.includes('-', 10) 
                ? dateString 
                : dateString + 'Z';
            
            date = new Date(dateStr);
            
            if (isNaN(date.getTime())) {
                date = new Date(dateString);
            }
        } else {
            date = new Date(dateString);
        }
        
        if (isNaN(date.getTime())) {
            return 'Fecha inválida';
        }
        
        return date.toLocaleDateString('es-ES', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    }

    truncateContent(content: string, maxLength: number = 150): string {

        if (!content || typeof content !== 'string') {
            return 'Sin contenido disponible';
        }
        
        if (content.length <= maxLength) {
            return content;
        }

        return content.substring(0, maxLength) + '...';
    }
}
