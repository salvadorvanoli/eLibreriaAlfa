import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})

export class ImageService {
  private apiUrl = 'http://localhost:8080/elibreriaalfa/image';
  private placeholderUrl = 'https://developers.elementor.com/docs/assets/img/elementor-placeholder-image.png';

  constructor(private http: HttpClient) {}

  getImageUrl(filename: string | null | undefined): string {
    // Si el nombre del archivo es null o undefined, devuelve la imagen placeholder
    if (!filename) {
      return this.placeholderUrl;
    }
    
    const encodedFilename = encodeURIComponent(filename).replace(/%2F/g, '/');
    const fullUrl = `${this.apiUrl}/${encodedFilename}`;
    
  
    return fullUrl;
  }

  validateImageFile(file: File): { isValid: boolean; error?: string } {
    const allowedExtensions = ['jpg', 'jpeg', 'png', 'webp'];
    const fileExtension = file.name.toLowerCase().split('.').pop();
    
    if (!fileExtension || !allowedExtensions.includes(fileExtension)) {
      return {
        isValid: false,
        error: 'Extensión de archivo no válida. Solo se permiten: JPG, JPEG, PNG, WEBP'
      };
    }

    const allowedTypes = ['image/jpeg', 'image/png', 'image/jpg', 'image/webp'];
    if (!allowedTypes.includes(file.type)) {
      return {
        isValid: false,
        error: 'Tipo de archivo no válido. Solo se permiten: JPEG, PNG, JPG, WEBP'
      };
    }

    const maxSize = 5 * 1024 * 1024;
    if (file.size > maxSize) {
      return {
        isValid: false,
        error: 'El archivo es demasiado grande. Tamaño máximo: 5MB'
      };
    }

    if (file.name.length > 255) {
      return {
        isValid: false,
        error: 'El nombre del archivo es demasiado largo'
      };
    }

    return { isValid: true };
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';

    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));

    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  generateImagePreview(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = (e) => {
        resolve(e.target?.result as string);
      };
      reader.onerror = () => {
        reject('Error al generar preview');
      };
      reader.readAsDataURL(file);
    });
  }
}
