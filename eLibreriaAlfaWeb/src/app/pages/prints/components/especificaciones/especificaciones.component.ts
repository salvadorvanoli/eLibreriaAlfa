import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PreviewComponent } from '../preview/preview.component';
import { DropdownModule } from 'primeng/dropdown';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { Textarea } from 'primeng/inputtextarea'; 
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

import { SecurityService } from '../../../../core/services/security.service';
import { ImpresionService } from '../../../../core/services/impresion.service';
import { ImpresionRequest } from '../../../../core/models/impresion';


declare var pdfjsLib: any;

interface ColorOption {
  label: string;
  value: string;
}

@Component({
  selector: 'app-especificaciones',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, 
    PreviewComponent, 
    DropdownModule,
    DialogModule,
    ButtonModule,
    Textarea, 
    ToastModule
  ],
  providers: [MessageService],
  templateUrl: './especificaciones.component.html',
  styleUrl: './especificaciones.component.scss'
})
export class EspecificacionesComponent {
  selectedFile: File | null = null;
  selectedColor: ColorOption = { label: 'Color', value: 'color' };
  pdfPageCount: number = 0;
  isCountingPages: boolean = false;
  

  showConfirmDialog: boolean = false;
  comentarioAdicional: string = '';
  isSubmitting: boolean = false;
  
  constructor(
    private cdr: ChangeDetectorRef,
    private securityService: SecurityService,
    private impresionService: ImpresionService,
    private messageService: MessageService
  ) {

    this.loadPdfJs();
  }
  
  colors: ColorOption[] = [
    { label: 'Color', value: 'color' },
    { label: 'Blanco y Negro', value: 'grayscale' }
  ];
  
 
  get estimatedPrice(): number {
    console.log('Calculando precio. pdfPageCount:', this.pdfPageCount, 'selectedColor:', this.selectedColor.value);
    if (!this.selectedFile) return 0;
    

    const pricePerPage = this.selectedColor.value === 'color' ? 2 : 1;
    return this.pdfPageCount * pricePerPage;
  }
  
  get pricePerPage(): number {
    return this.selectedColor.value === 'color' ? 2 : 1;
  }
  
  get totalPages(): number {
    return this.pdfPageCount;
  }
  
  private loadPdfJs(): void {

    if (typeof pdfjsLib !== 'undefined') return;
    
    const script = document.createElement('script');
    script.src = 'https://cdnjs.cloudflare.com/ajax/libs/pdf.js/3.11.174/pdf.min.js';
    script.onload = () => {
      pdfjsLib.GlobalWorkerOptions.workerSrc = 'https://cdnjs.cloudflare.com/ajax/libs/pdf.js/3.11.174/pdf.worker.min.js';
    };
    document.head.appendChild(script);
  }
  
  async onFileSelected(event: any): Promise<void> {
    if (event.target.files && event.target.files.length > 0) {
      const file = event.target.files[0];
      

      if (file.type !== 'application/pdf') {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Por favor, selecciona solo archivos PDF.'
        });
        return;
      }
      
      this.selectedFile = file;
      this.pdfPageCount = 0; 
      console.log('PDF seleccionado:', this.selectedFile);
      

      if (this.selectedFile) {
        await this.countPdfPages(this.selectedFile);
      }
    }
  }
  
  async countPdfPages(file: File): Promise<void> {
    try {
      console.log('Iniciando conteo de páginas para:', file.name);
      this.isCountingPages = true;
      this.cdr.detectChanges();
      
   
      await this.waitForPdfJs();
      
 
      const arrayBuffer = await file.arrayBuffer();
      console.log('ArrayBuffer creado, tamaño:', arrayBuffer.byteLength);
      

      const loadingTask = pdfjsLib.getDocument({
        data: arrayBuffer
      });
      
      const pdf = await loadingTask.promise;
      this.pdfPageCount = pdf.numPages;
      
      console.log(`PDF procesado. Páginas encontradas: ${this.pdfPageCount}`);
      console.log('Precio estimado:', this.estimatedPrice);
      
      this.cdr.detectChanges();
      
    } catch (error) {
      console.error('Error al procesar PDF con PDF.js:', error);
      

      const fileSizeInKB = file.size / 1024;
      console.log(`Usando fallback. Tamaño del archivo: ${fileSizeInKB.toFixed(2)} KB`);
      
      if (fileSizeInKB < 100) {
        this.pdfPageCount = 1;
      } else if (fileSizeInKB < 500) {
        this.pdfPageCount = Math.ceil(fileSizeInKB / 100);
      } else if (fileSizeInKB < 2000) {
        this.pdfPageCount = Math.ceil(fileSizeInKB / 200);
      } else {
        this.pdfPageCount = Math.ceil(fileSizeInKB / 300);
      }
      

      this.pdfPageCount = Math.max(1, Math.min(500, this.pdfPageCount));
      
      console.log(`Páginas estimadas por tamaño: ${this.pdfPageCount}`);
      this.cdr.detectChanges();
      
    } finally {
      this.isCountingPages = false;
      this.cdr.detectChanges();
      console.log('Proceso finalizado. pdfPageCount:', this.pdfPageCount);
      console.log('Precio final:', this.estimatedPrice);
    }
  }
  
  private async waitForPdfJs(): Promise<void> {
    return new Promise((resolve, reject) => {
      let attempts = 0;
      const maxAttempts = 50;
      
      const checkPdfJs = () => {
        attempts++;
        if (typeof pdfjsLib !== 'undefined') {
          resolve();
        } else if (attempts >= maxAttempts) {
          reject(new Error('PDF.js no se pudo cargar'));
        } else {
          setTimeout(checkPdfJs, 100);
        }
      };
      
      checkPdfJs();
    });
  }
  
  cancelPreview(): void {
    this.selectedFile = null;
    this.pdfPageCount = 0;
    this.cdr.detectChanges();
  }
  
  proceedWithPrint(): void {
    if (!this.selectedFile || this.estimatedPrice === 0) {
      return;
    }

    if (!this.securityService.isAuthenticated()) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Acceso requerido',
        detail: 'Debe estar registrado para solicitar impresiones.'
      });
      return;
    }

    this.comentarioAdicional = '';
    this.showConfirmDialog = true;
  }

  confirmPrintRequest(): void {
    if (!this.selectedFile) {
      return;
    }

    this.isSubmitting = true;

    this.securityService.getActualUser().subscribe({
      next: (currentUser) => {
        if (!currentUser || !currentUser.id) {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'No se pudo obtener la información del usuario actual.'
          });
          this.isSubmitting = false;
          return;
        }

        const request: ImpresionRequest = {
          color: this.selectedColor.value === 'color',
          comentarioAdicional: this.comentarioAdicional || '',
          usuario: currentUser,
          nombreArchivo: this.selectedFile!.name 
        };

        console.log('=== DATOS QUE SE ENVÍAN AL BACKEND ===');
        console.log('request:', request);
        console.log('currentUser:', currentUser);
        console.log('selectedColor.value:', this.selectedColor.value);
        console.log('color (boolean):', this.selectedColor.value === 'color');
        console.log('comentarioAdicional:', this.comentarioAdicional);
        console.log('nombreArchivo:', this.selectedFile!.name);
        console.log('usuario completo:', currentUser);
        console.log('========================================');

        this.impresionService.createPrintRequest(request).subscribe({
          next: (response) => {
            console.log('Solicitud de impresión creada:', response);
            this.messageService.add({
              severity: 'success',
              summary: 'Éxito',
              detail: 'Solicitud de impresión enviada correctamente.'
            });
            
            // Limpiar formulario
            this.selectedFile = null;
            this.pdfPageCount = 0;
            this.comentarioAdicional = '';
            this.showConfirmDialog = false;
            this.cdr.detectChanges();
          },
          error: (error) => {
            console.error('Error al crear solicitud de impresión:', error);
            console.error('Detalles completos del error:', error.error);
            this.messageService.add({
              severity: 'error',
              summary: 'Error',
              detail: 'Error al enviar la solicitud de impresión. Intente nuevamente.'
            });
            this.isSubmitting = false;
          },
          complete: () => {
            this.isSubmitting = false;
          }
        });
      },
      error: (error) => {
        console.error('Error al obtener usuario actual:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo verificar la autenticación del usuario.'
        });
        this.isSubmitting = false;
      }
    });
  }

  cancelPrintRequest(): void {
    this.showConfirmDialog = false;
    this.comentarioAdicional = '';
  }
}
