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
  fileType: 'pdf' | 'other' = 'other';
  
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
    
    if (this.file.type === 'application/pdf') {
      this.fileType = 'pdf';
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
}