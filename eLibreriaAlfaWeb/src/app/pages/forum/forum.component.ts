import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { PublicationService } from '../../core/services/publication.service';
import { Publicacion } from '../../core/models/publicacion';
import { PublicationCardComponent } from './components/publication-card/publication-card.component';
import { TitleAndDescriptionComponent } from '../../shared/components/title-and-description/title-and-description.component';
import { PaginatorModule } from 'primeng/paginator';

@Component({
  selector: 'app-forum',
  standalone: true,
  imports: [
    CommonModule, 
    PublicationCardComponent, 
    TitleAndDescriptionComponent, 
    PaginatorModule
],
  templateUrl: './forum.component.html',
  styleUrls: ['./forum.component.scss']
})
export class ForumComponent implements OnInit {
  
    publicaciones: Publicacion[] = [];
    loading: boolean = false;
    error: string | null = null;
    
    currentPage: number = 0;
    pageSize: number = 5;
    totalPublications: number = 0;

    constructor(
        private publicationService: PublicationService,
        private router: Router
    ) {}

    ngOnInit(): void {
        this.loadPublications();
    }
  
    loadPublications(): void {
        this.loading = true;
        this.error = null;
            
        this.publicationService.getPublicationsPageByDate(this.currentPage, this.pageSize).subscribe({
        next: (publications) => {

            this.totalPublications = (publications as any).totalElements! || 0;

            let publicacionesArray = Array.isArray(publications) ? publications : Object.values(publications);

            if (publicacionesArray.length > 0 && Array.isArray(publicacionesArray[0])) {
                this.publicaciones = publicacionesArray[0] as Publicacion[];
            } else {
                this.publicaciones = publicacionesArray as Publicacion[];
            }
            
            this.loading = false;
        },
        error: (error) => {
            this.error = 'Error al cargar las publicaciones';
            this.loading = false;
        }
        });
    }
    
    goToPublication(id: number): void {
        this.router.navigate(['/publicacion', id]);
    }

    onPageChange(event: any): void {
        this.currentPage = event.page;

        this.loadPublications();
    }
}
