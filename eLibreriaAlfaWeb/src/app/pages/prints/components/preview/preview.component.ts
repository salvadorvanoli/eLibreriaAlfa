import { Component, Input, OnChanges, SimpleChanges, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SafeResourceUrlPipe } from '../../../../shared/pipes/safe-resource-url.pipe';

@Component({
  selector: 'app-preview',
  standalone: true,
  imports: [CommonModule, SafeResourceUrlPipe],
  templateUrl: './preview.component.html',
  styleUrl: './preview.component.scss'
})
export class PreviewComponent implements OnChanges, OnDestroy {
  @Input() file: File | null = null;
  @Input() selectedColor: string = 'color';
  
  previewUrl: string | null = null;
  fileType: 'pdf' | 'image' | 'other' = 'other';
  
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['file'] && this.file) {
      this.createPreview();
    }
  }
  
  createPreview(): void {
    if (!this.file) return;
    
    if (this.previewUrl) {
      URL.revokeObjectURL(this.previewUrl);
    }
    
    this.previewUrl = URL.createObjectURL(this.file);
    
    // Determinar el tipo de archivo
    const fileName = this.file.name.toLowerCase();
    const fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
    
    if (this.file.type === 'application/pdf' || fileExtension === 'pdf') {
      this.fileType = 'pdf';
    } else if (['png', 'jpg', 'jpeg'].includes(fileExtension)) {
      this.fileType = 'image';
    } else {
      this.fileType = 'other';
    }
  }
  
  getPdfUrlWithoutToolbar(): string {
    if (!this.previewUrl) return '';
    return `${this.previewUrl}#toolbar=0&navpanes=0&scrollbar=0`;
  }
  

  getColorFilterClass(): string {
    return this.selectedColor === 'grayscale' ? 'filter-grayscale' : '';
  }
  
  ngOnDestroy(): void {
    if (this.previewUrl) {
      URL.revokeObjectURL(this.previewUrl);
    }
  }

  getFileIcon(): string {
    if (!this.file) return 'pi pi-file';
    
    const fileName = this.file.name.toLowerCase();
    const fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
    
    switch (fileExtension) {
      case 'pdf':
        return 'pi pi-file-pdf';
      case 'doc':
      case 'docx':
        return 'pi pi-file-word';
      case 'xls':
      case 'xlsx':
        return 'pi pi-file-excel';
      case 'png':
      case 'jpg':
      case 'jpeg':
        return 'pi pi-image';
      case 'cdr':
        return 'pi pi-palette';
      default:
        return 'pi pi-file';
    }
  }

  getFileTypeName(): string {
    if (!this.file) return '';
    
    const fileName = this.file.name.toLowerCase();
    const fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
    
    switch (fileExtension) {
      case 'pdf':
        return 'Documento PDF';
      case 'doc':
      case 'docx':
        return 'Documento Word';
      case 'xls':
      case 'xlsx':
        return 'Hoja de Excel';
      case 'png':
        return 'Imagen PNG';
      case 'jpg':
      case 'jpeg':
        return 'Imagen JPEG';
      case 'cdr':
        return 'Archivo CorelDRAW';
      default:
        return 'Archivo';
    }
  }
}