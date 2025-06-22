import { Component, computed, Input, signal, SimpleChanges } from '@angular/core';
import { Toast } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { ProductService } from '../../../../../../core/services/product.service';
import { ImageService } from '../../../../../../core/services/image.service';
import { ProductoDto, ProductoConImagenesDto, ProductoRequestDto } from '../../../../../../core/models/producto';
import { ViewChild } from '@angular/core';

import { FormTextInputComponent } from '../../../../../../shared/components/inputs/form-text-input/form-text-input.component';
import { FormNumberInputComponent } from '../../../../../../shared/components/inputs/form-number-input/form-number-input.component';
import { ImageUploadInputComponent } from '../../../../../../shared/components/inputs/image-upload-input/image-upload-input.component';
import { FormTextareaInputComponent } from '../../../../../../shared/components/inputs/form-textarea-input/form-textarea-input.component';
import { PrimaryButtonComponent } from '../../../../../../shared/components/buttons/primary-button/primary-button.component';
import { ImageDto } from '../../../../../../core/models/image';
import { CategoryTreePopoverComponent } from "./components/category-tree-popover/category-tree-popover.component";

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [
    Toast,
    FormTextInputComponent,
    FormNumberInputComponent,
    FormTextareaInputComponent,
    ImageUploadInputComponent,
    PrimaryButtonComponent,
    CategoryTreePopoverComponent
],
  providers: [
    MessageService
  ],
  templateUrl: './product-form.component.html',
  styleUrl: './product-form.component.scss'
})
export class ProductFormComponent {
  @ViewChild('nameInput') nameInput: any;
  @ViewChild('priceInput') priceInput: any;
  @ViewChild('descriptionInput') descriptionInput: any;
  @ViewChild('imagesInput') imagesInput: any;
  @ViewChild('categoriesInput') categoriesInput: any;

  @Input() product: ProductoConImagenesDto | null = null;

  name: string = '';
  price: number | null = null;
  description: string = '';
  existingImages: ImageDto[] = [];
  newImages: File[] = [];
  imagesToDelete: string[] = [];
  categories: number[] = [];

  formSubmitted = signal(false);

  isNameInvalid: boolean = false;
  isPriceInvalid: boolean = false;
  isDescriptionInvalid: boolean = false;
  areImagesInvalid: boolean = false;
  areCategoriesInvalid: boolean = false;

  namePattern = /^.{1,200}$/;
  pricePattern = /^\d+(\.\d{1,2})?$/;
  descriptionPattern = /^.{1,200}$/;

  constructor(
    private messageService: MessageService,
    private productService: ProductService,
    private imageService: ImageService
  ) {}

  ngOnInit() {
    this.resetForm();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['product']) {
      this.manageForm();
    }
  }

  manageForm() {
    if (this.product != null) {
      this.loadForm();
    } else {
      this.resetForm();
    }
  }

  confirm() {
    this.formSubmitted.set(true);
    
    if (this.validateForm()) {
      this.messageService.add({ 
        severity: 'error', 
        summary: 'Error', 
        detail: "Datos ingresados inválidos", 
        life: 4000
      });
    } else {
      this.product ? this.update() : this.create();
    }
  }
  
  create() {
    const producto = this.createFormData();

    this.productService.post(producto).subscribe({
      next: (response: ProductoDto) => this.handleSuccess('creado', response),
      error: (error) => this.handleError(error)
    });
  }

  update() {
    const producto = this.createFormData();

    this.productService.put(this.product?.id!, producto).subscribe({
      next: (response: ProductoDto) => this.handleSuccess('actualizado', response),
      error: (error) => this.handleError(error)
    });
  }

  validateForm() {
    return this.isNameInvalid ||
      this.isPriceInvalid ||
      this.isDescriptionInvalid ||
      this.areImagesInvalid ||
      this.areCategoriesInvalid;
  }
  
  loadForm() {
    if (this.product) {
      this.name = this.product.nombre;
      this.price = this.product.precio;
      this.description = this.product.descripcion;
      
      const categoriasIds = this.product.categorias.map(categoria => categoria.id);
      this.categories = categoriasIds || [];

      if (this.product.imagenes && this.product.imagenes.length > 0) {
        const imagesDtos: ImageDto[] = this.product.imagenes.map(file => ({
          filename: file.url,
          originalName: file.originalName,
          size: 0,
          url: this.imageService.getImageUrl(file.url)
        }));
        
        this.existingImages = imagesDtos;
        
        setTimeout(() => {
          this.imagesInput?.setValue(imagesDtos);
        }, 0);
      }

      this.loadFormFields();
    }

    this.resetValidationFlags();
  }

  resetForm() {
    this.formSubmitted.set(false);

    this.resetFormData();
    this.resetValidationFlags();
    this.resetFormFields();
  }

  onRemoveImage(event: { image: any, index: number } | null) {
    if (!event?.image) return;
    
    const { image } = event;
    
    if (this.isExistingImage(image)) {
      this.imagesToDelete.push(image.filename);
      this.existingImages = this.existingImages.filter(img => 
        img.filename !== image.filename
      );
    } else if (this.isNewImage(image)) {
      this.newImages = this.newImages.filter(file => file !== image.file);
    }
  }

  private createFormData(): FormData {
    const formData = new FormData();
    
    formData.append('nombre', this.name.trim());
    formData.append('precio', this.price!.toString());
    formData.append('descripcion', this.description.trim());
    
    this.categories.forEach(categoryId => {
      formData.append('categoriasIds', categoryId.toString());
    });

    this.newImages.forEach(image => {
      formData.append('imagenes', image, image.name);
    });
    
    if (this.product && this.imagesToDelete.length > 0) {
      this.imagesToDelete.forEach(imagePath => {
        formData.append('imagenesAEliminar', imagePath);
      });
    }
    
    return formData;
  }

  private handleSuccess(action: string, response: any) {
    this.messageService.add({ 
      severity: 'success', 
      summary: 'Éxito', 
      detail: `¡Producto ${action} exitosamente!`, 
      life: 4000 
    });
    this.resetForm();
  }

  private handleError(error: any) {
    const errorMessage = error?.error || error?.message || "No fue posible conectar con el servidor";
    this.messageService.add({ 
      severity: 'error', 
      summary: 'Error', 
      detail: errorMessage, 
      life: 4000 
    });
  }

  private isExistingImage(image: { filename: string; isExisting: true }) {
    return image && 'filename' in image && image.isExisting === true;
  }

  private isNewImage(image: { file: File; isExisting: false }) {
    return image && 'file' in image && image.isExisting === false;
  }

  private loadFormFields() {
    setTimeout(() => {
      this.nameInput?.setValue(this.name);
      this.priceInput?.setValue(this.price);
      this.descriptionInput?.setValue(this.description);
      this.categoriesInput?.setValue(this.categories);
    }, 0);
  }

  private resetValidationFlags() {
    this.isNameInvalid = false;
    this.isPriceInvalid = false;
    this.isDescriptionInvalid = false;
    this.areImagesInvalid = false;
    this.areCategoriesInvalid = false;
  }

  private resetFormData() {
    this.name = '';
    this.price = null;
    this.description = '';
    this.newImages = [];
    this.existingImages = [];
    this.imagesToDelete = [];
    this.categories = [];
  }

  private resetFormFields() {
    this.nameInput?.reset();
    this.priceInput?.reset();
    this.descriptionInput?.reset();
    this.imagesInput?.reset();
    this.categoriesInput?.reset();
  }
}
