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
import { TooltipModule } from 'primeng/tooltip';

import { SecurityService } from '../../../../core/services/security.service';
import { ImpresionService } from '../../../../core/services/impresion.service';
import { ImpresionRequest } from '../../../../core/models/impresion';

declare var pdfjsLib: any;

interface FormatOption {
  label: string;
  value: string;
}

interface PaperTypeOption {
  label: string;
  value: string;
  availableForA3?: boolean;
}

interface ColorOption {
  label: string;
  value: string;
}

interface SideOption {
  label: string;
  value: string;
}

interface OrientationOption {
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
    ToastModule,
    TooltipModule
  ],
  providers: [MessageService],
  templateUrl: './especificaciones.component.html',
  styleUrl: './especificaciones.component.scss'
})
export class EspecificacionesComponent {
  selectedFile: File | null = null;
  selectedFormat: FormatOption = { label: 'A4', value: 'A4' };
  selectedPaperType: PaperTypeOption = { label: 'Papel común', value: 'common' };
  selectedColor: ColorOption = { label: 'Color', value: 'color' };
  selectedSide: SideOption = { label: 'Simple', value: 'simple' };
  selectedOrientation: OrientationOption = { label: 'Vertical', value: 'vertical' };
  
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
  
  formats: FormatOption[] = [
    { label: 'A4', value: 'A4' },
    { label: 'A3', value: 'A3' }
  ];

  paperTypes: PaperTypeOption[] = [
    { label: 'Papel común', value: 'common', availableForA3: true },
    { label: 'Papel foto', value: 'photo', availableForA3: false },
    { label: 'Papel foto adhesivo', value: 'photo_adhesive', availableForA3: false },
    { label: 'Papel mate adhesivo', value: 'matte_adhesive', availableForA3: false },
    { label: 'Papel tarjeta liso', value: 'card_smooth', availableForA3: false },
    { label: 'Papel tarjeta texturado', value: 'card_textured', availableForA3: false }
  ];

  colors: ColorOption[] = [
    { label: 'Color', value: 'color' },
    { label: 'Blanco y Negro', value: 'grayscale' }
  ];

  sides: SideOption[] = [
    { label: 'Simple', value: 'simple' },
    { label: 'Doble cara', value: 'double' }
  ];

  orientations: OrientationOption[] = [
    { label: 'Vertical', value: 'vertical' },
    { label: 'Horizontal', value: 'horizontal' }
  ];

  get availablePaperTypes(): PaperTypeOption[] {
    if (this.selectedFormat.value === 'A3') {
      return this.paperTypes.filter(type => type.availableForA3);
    }
    return this.paperTypes;
  }

  get availableColors(): ColorOption[] {
    if (this.selectedFormat.value === 'A3') {
      return this.colors.filter(color => color.value === 'grayscale');
    }
    return this.colors;
  }

  get availableSides(): SideOption[] {
    if (this.selectedPaperType.value !== 'common') {
      return this.sides.filter(side => side.value === 'simple');
    }
    return this.sides;
  }

  onFormatChange(): void {
    if (this.selectedFormat.value === 'A3') {
      this.selectedPaperType = this.paperTypes.find(type => type.value === 'common')!;
      this.selectedColor = this.colors.find(color => color.value === 'grayscale')!;
      this.selectedSide = this.sides.find(side => side.value === 'simple')!;
    }
    this.cdr.detectChanges();
  }

  onPaperTypeChange(): void {
    if (this.selectedPaperType.value !== 'common') {
      this.selectedSide = this.sides.find(side => side.value === 'simple')!;
    }
    this.cdr.detectChanges();
  }

  getPaperMultiplierText(paperType: string): string {
    const paperMultipliers: { [key: string]: number } = {
      'common': 1,
      'photo': 3,
      'photo_adhesive': 4,
      'matte_adhesive': 3.5,
      'card_smooth': 2,
      'card_textured': 2.5
    };
    
    const multiplier = paperMultipliers[paperType] || 1;
    
    if (multiplier === 1) {
      return 'Precio base';
    } else {
      return `${multiplier}x precio`;
    }
  }

  get estimatedPrice(): number {
    if (!this.selectedFile) return 0;
    
    let pricePerPage = 0;
    
    if (this.selectedFormat.value === 'A4') {
      pricePerPage = this.selectedColor.value === 'color' ? 2 : 1;
    } else { 
      pricePerPage = 3;
    }

    const paperMultipliers: { [key: string]: number } = {
      'common': 1,
      'photo': 3,
      'photo_adhesive': 4,
      'matte_adhesive': 3.5,
      'card_smooth': 2,
      'card_textured': 2.5
    };

    pricePerPage *= paperMultipliers[this.selectedPaperType.value] || 1;

    if (this.selectedSide.value === 'double') {
      pricePerPage *= 1.5;
    }

    return Math.ceil(this.pdfPageCount * pricePerPage);
  }
  
  get pricePerPage(): number {
    if (!this.selectedFile) return 0;
    return Math.ceil(this.estimatedPrice / this.pdfPageCount);
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
      
      const maxSizeInBytes = 10 * 1024 * 1024;
      if (file.size > maxSizeInBytes) {
        this.messageService.clear();
        this.messageService.add({
          severity: 'warn',
          summary: 'Archivo excede tamaño permitido',
          detail: 'Archivo excede tamaño permitido. Comunicarse por medio telefónico para concretar solicitud.',
          life: 8000
        });
        
        event.target.value = '';
        return;
      }
      
      const allowedTypes = [
        'application/pdf',
        'application/msword', 
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 
        'application/vnd.ms-excel', 
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 
        'image/png',
        'image/jpeg',
        'image/jpg',
        'application/x-coreldraw', 
        'image/x-coreldraw' 
      ];
      
      const fileName = file.name.toLowerCase();
      const fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
      const allowedExtensions = ['pdf', 'doc', 'docx', 'xls', 'xlsx', 'png', 'jpg', 'jpeg', 'cdr'];
      
      if (!allowedTypes.includes(file.type) && !allowedExtensions.includes(fileExtension)) {
        this.messageService.clear();
        this.messageService.add({
          severity: 'error',
          summary: 'Formato no válido',
          detail: 'Por favor, selecciona archivos PDF, Word, Excel, CorelDRAW, PNG, JPG o JPEG.'
        });
        
        event.target.value = '';
        return;
      }
      
      this.selectedFile = file;
      this.pdfPageCount = 0;
      
      if (this.selectedFile) {
        await this.estimateFilePages(this.selectedFile);
      }
    }
  }
  
  async estimateFilePages(file: File): Promise<void> {
    try {
      this.isCountingPages = true;
      this.cdr.detectChanges();
      
      const fileName = file.name.toLowerCase();
      const fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
      
      if (file.type === 'application/pdf' || fileExtension === 'pdf') {
        await this.countPdfPages(file);
        return;
      }
      
      this.estimatePagesByFileSize(file, fileExtension);
      
    } catch (error) {
      this.estimatePagesByFileSize(file, file.name.toLowerCase().split('.').pop() || '');
    } finally {
      this.isCountingPages = false;
      this.cdr.detectChanges();
    }
  }
  
  private estimatePagesByFileSize(file: File, extension: string): void {
    const fileSizeInKB = file.size / 1024;
    const fileSizeInMB = fileSizeInKB / 1024;
    
    let estimatedPages = 1;
    
    switch (extension) {
      case 'doc':
      case 'docx':
        estimatedPages = Math.max(1, Math.ceil(fileSizeInKB / 100));
        break;
        
      case 'xls':
      case 'xlsx':
        estimatedPages = Math.max(1, Math.ceil(fileSizeInKB / 50));
        break;
        
      case 'png':
      case 'jpg':
      case 'jpeg':
        estimatedPages = 1;
        break;
        
      case 'cdr':
        if (fileSizeInMB < 5) {
          estimatedPages = 1;
        } else if (fileSizeInMB < 20) {
          estimatedPages = Math.ceil(fileSizeInMB / 5);
        } else {
          estimatedPages = Math.ceil(fileSizeInMB / 10);
        }
        break;
        
      default:
        estimatedPages = Math.max(1, Math.ceil(fileSizeInKB / 200));
        break;
    }
    
    this.pdfPageCount = Math.max(1, Math.min(500, estimatedPages));
    this.cdr.detectChanges();
  }
  
  async countPdfPages(file: File): Promise<void> {
    try {
      this.isCountingPages = true;
      this.cdr.detectChanges();
      
      await this.waitForPdfJs();
      
      const arrayBuffer = await file.arrayBuffer();
      
      const loadingTask = pdfjsLib.getDocument({
        data: arrayBuffer
      });
      
      const pdf = await loadingTask.promise;
      this.pdfPageCount = pdf.numPages;
      
      this.cdr.detectChanges();
      
    } catch (error) {
      const fileSizeInKB = file.size / 1024;
      
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
      this.cdr.detectChanges();
      
    } finally {
      this.isCountingPages = false;
      this.cdr.detectChanges();
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
      this.messageService.clear();
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
          this.messageService.clear();
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
          nombreArchivo: this.selectedFile!.name,
          formato: this.selectedFormat.value,
          tipoPapel: this.selectedPaperType.label, 
          dobleCara: this.selectedSide.value === 'double', 
          orientacion: this.selectedOrientation.value
        };

        this.impresionService.createPrintRequest(request, this.selectedFile!).subscribe({
          next: (response) => {
            this.messageService.clear();
            this.messageService.add({
              severity: 'success',
              summary: 'Éxito',
              detail: 'Solicitud de impresión enviada correctamente.'
            });
            
            this.selectedFile = null;
            this.pdfPageCount = 0;
            this.comentarioAdicional = '';
            this.showConfirmDialog = false;
            this.cdr.detectChanges();
          },
          error: (error) => {
            this.messageService.clear();
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
        this.messageService.clear();
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
