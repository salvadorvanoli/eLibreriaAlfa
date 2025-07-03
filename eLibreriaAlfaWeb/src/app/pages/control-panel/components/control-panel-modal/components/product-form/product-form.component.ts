import { Component, EventEmitter, Input, Output, signal, SimpleChanges } from '@angular/core';
import { ViewChild } from '@angular/core';
import { Toast } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { ProductService } from '../../../../../../core/services/product.service';
import { ProductoDto, ProductoConImagenesDto } from '../../../../../../core/models/producto';
import { ImageDto } from '../../../../../../core/models/image';

import { FormTextInputComponent } from '../../../../../../shared/components/inputs/form-text-input/form-text-input.component';
import { FormNumberInputComponent } from '../../../../../../shared/components/inputs/form-number-input/form-number-input.component';
import { FormTextareaInputComponent } from '../../../../../../shared/components/inputs/form-textarea-input/form-textarea-input.component';
import { ImageUploadInputComponent } from '../../../../../../shared/components/inputs/image-upload-input/image-upload-input.component';
import { PrimaryButtonComponent } from '../../../../../../shared/components/buttons/primary-button/primary-button.component';
import { CategoryTreeSelectComponent } from "../../../../../../shared/components/category-tree-select/category-tree-select.component";

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
    CategoryTreeSelectComponent
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

  @Output() reloadData = new EventEmitter<void>();

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

  namePattern = /^.{1,50}$/;
  descriptionPattern = /^.{1,200}$/;

  constructor(
    private messageService: MessageService,
    private productService: ProductService
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

    const isInvalid = this.validateForm();

    if (isInvalid) {
      this.messageService.clear();
      this.messageService.add({ 
        severity: 'error', 
        summary: 'Error', 
        detail: "Datos ingresados inválidos", 
        life: 4000
      });
    } else {
      if (this.product) {
        this.update();
      } else {
        this.create();
      }
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
        this.existingImages = this.product.imagenes;
        
        setTimeout(() => {
          this.imagesInput?.setValue(this.existingImages);
        }, 0);
      }

      this.loadFormFields();
    }

    this.resetValidationState();
  }

  resetForm() {
    this.formSubmitted.set(false);

    this.resetFormData();
    this.resetValidationState();
    this.resetFormComponents();
  }

  onRemoveImage(event: { image: any, index: number } | null) {
    if (!event?.image) return;
    
    const { image } = event;
    
    if (this.isExistingImage(image)) {
      this.imagesToDelete.push(image.relativePath);
      this.existingImages = this.existingImages.filter(img => 
        img.relativePath !== image.relativePath
      );
    } else if (this.isNewImage(image)) {
      this.newImages = this.newImages.filter(file => file !== image.file);
    }
  }

  private createFormData(): FormData {
    const formData = new FormData();
    
    formData.append('nombre', this.name.trim());
    formData.append('precio', this.price!.toString());
    formData.append('descripcion', this.htmlToPlainText(this.description).trim());
    
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

  private htmlToPlainText(html: string): string {
    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = html;
    
    return tempDiv.textContent || tempDiv.innerText || '';
  } 

  private handleSuccess(action: string, response: any) {
    this.messageService.clear();
    this.messageService.add({ 
      severity: 'success', 
      summary: 'Éxito', 
      detail: `¡Producto ${action} exitosamente!`, 
      life: 4000 
    });
    
    this.resetForm();
    this.onDataReloaded();
  }

  private handleError(error: any) {
    const errorMessage = error?.error.error || error?.error.message || "No fue posible conectar con el servidor";
    this.messageService.clear();
    this.messageService.add({ 
      severity: 'error', 
      summary: 'Error', 
      detail: errorMessage, 
      life: 4000 
    });
  }

  private loadFormFields() {
    setTimeout(() => {
      this.nameInput?.setValue(this.name);
      this.priceInput?.setValue(this.price);
      this.descriptionInput?.setValue(this.description);
      this.categoriesInput?.setValue(this.categories);
    }, 0);
  }

  private resetValidationState() {
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

  private resetFormComponents() {
    this.nameInput?.reset();
    this.priceInput?.reset();
    this.descriptionInput?.reset();
    this.imagesInput?.reset();
    this.categoriesInput?.reset();
  }

  private isExistingImage(image: { relativePath: string; isExisting: true }) {
    return image && 'relativePath' in image && image.isExisting === true;
  }

  private isNewImage(image: { file: File; isExisting: false }) {
    return image && 'file' in image && image.isExisting === false;
  }

  private onDataReloaded() {
    this.reloadData.emit();
  }
}
