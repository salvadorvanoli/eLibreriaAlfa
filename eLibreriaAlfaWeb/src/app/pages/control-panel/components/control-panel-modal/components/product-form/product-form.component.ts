import { Component, computed, Input, signal, SimpleChanges } from '@angular/core';
import { Toast } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { ProductService } from '../../../../../../core/services/product.service';
import { ProductoDto, ProductoRequestDto } from '../../../../../../core/models/producto';
import { ViewChild } from '@angular/core';

import { FormTextInputComponent } from '../../../../../../shared/components/inputs/form-text-input/form-text-input.component';
import { FormNumberInputComponent } from '../../../../../../shared/components/inputs/form-number-input/form-number-input.component';
import { FormTextareaInputComponent } from '../../../../../../shared/components/inputs/form-textarea-input/form-textarea-input.component';
import { PrimaryButtonComponent } from '../../../../../../shared/components/buttons/primary-button/primary-button.component';

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [
    Toast,
    FormTextInputComponent,
    FormNumberInputComponent,
    FormTextareaInputComponent,
    PrimaryButtonComponent
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

  @Input() product: ProductoDto | null = null;

  name: string = '';
  price: number | null = null;
  description: string = '';
  images: any[] = [];
  categories: number[] = [];

  formSubmitted = signal(false);

  isNameInvalid: boolean = false;
  isPriceInvalid: boolean = false;
  isDescriptionInvalid: boolean = false;

  namePattern = /^.{1,200}$/;
  pricePattern = /^\d+(\.\d{1,2})?$/;
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
    
    if (!this.validateForm()) {
      switch (this.product) {
        case null:
          this.create();
          break;
        default:
          this.update();
          break;
      }
    } else {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: "Datos ingresados inválidos", life: 4000 });
    }
  }
  
  create() {
    const producto: ProductoRequestDto = {
      nombre: this.name.trim(),
      precio: this.price!,
      descripcion: this.description.trim(),
      imagenes: this.images,
      categoriasIds: this.categories
    };

    this.productService.post(producto).subscribe({
      next: (response: ProductoDto) => {
        console.log('Server response:', response);
        this.messageService.add({ severity: 'success', summary: 'Operación exitosa', detail: "¡Producto creado exitosamente!", life: 4000 });
        this.resetForm();
      },
      error: (err) => {
        let errorMessage = "No fue posible conectar con el servidor";
        
        if (err.error && typeof err.error === 'string') {
          errorMessage = err.error;
        } else if (err.message) {
          errorMessage = err.message;
        }
        
        this.messageService.add({ severity: 'error', summary: 'Error', detail: errorMessage, life: 4000 });
      }
    });
  }

  update() {
    const producto: ProductoRequestDto = {
      nombre: this.name.trim(),
      precio: this.price!,
      descripcion: this.description.trim(),
      imagenes: this.images,
      categoriasIds: this.categories
    };

    this.productService.put(this.product?.id!, producto).subscribe({
      next: (response: ProductoDto) => {
        this.messageService.add({ severity: 'success', summary: 'Operación exitosa', detail: "¡Producto actualizado exitosamente!", life: 4000 });
        this.resetForm();
      },
      error: (err) => {
        console.log('Error details:', err);
        let errorMessage = "No fue posible conectar con el servidor";
        
        if (err.error && typeof err.error === 'string') {
          errorMessage = err.error;
        } else if (err.message) {
          errorMessage = err.message;
        }
        
        this.messageService.add({ severity: 'error', summary: 'Error', detail: errorMessage, life: 4000 });
      }
    });
  }

  validateForm() {
    return this.isNameInvalid || this.isPriceInvalid || this.isDescriptionInvalid || this.price === null || this.price <= 0 || this.categories.length === 0;
  }
  
  loadForm() {
    if (this.product) {
      this.name = this.product.nombre;
      this.price = this.product.precio;
      this.description = this.product.descripcion;
      this.images = this.product.imagenes || [];

      const categoriasIds = this.product.categorias.map(categoria => categoria.id);

      this.categories = categoriasIds || [];

      this.isNameInvalid = false;
      this.isPriceInvalid = false;
      this.isDescriptionInvalid = false;

      this.nameInput?.setValue(this.name);
      this.priceInput?.setValue(this.price);
      this.descriptionInput?.setValue(this.description);
      this.imagesInput?.setValue(this.images);
      this.categoriesInput?.setValue(this.categories);
    }
  }

  resetForm() {
    this.formSubmitted.set(false);

    this.name = '';
    this.price = null;
    this.description = '';
    this.images = [];
    this.categories = [];

    this.isNameInvalid = false;
    this.isPriceInvalid = false;
    this.isDescriptionInvalid = false;

    this.nameInput?.reset();
    this.priceInput?.reset();
    this.descriptionInput?.reset();
    this.imagesInput?.reset();
    this.categoriesInput?.reset();
  }
}
