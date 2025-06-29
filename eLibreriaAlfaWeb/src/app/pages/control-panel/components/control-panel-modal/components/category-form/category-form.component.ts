import { Component, Input, signal, SimpleChanges, ViewChild, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Toast } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { ConfirmDialog, ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService } from 'primeng/api';
import { CategoryService } from '../../../../../../core/services/category.service';
import { CategoriaNodoDto, CategoriaRequestDto, CategoriaSimpleDto } from '../../../../../../core/models/categoria';

import { FormTextInputComponent } from '../../../../../../shared/components/inputs/form-text-input/form-text-input.component';
import { PrimaryButtonComponent } from '../../../../../../shared/components/buttons/primary-button/primary-button.component';
import { CategoryTreeSelectComponent } from "../../../../../../shared/components/category-tree-select/category-tree-select.component";
import { ConfirmDialogButtonComponent } from '../../../../../../shared/components/confirm-dialog-button/confirm-dialog-button.component';

@Component({
  selector: 'app-category-form',
  standalone: true,
  imports: [
    CommonModule,
    Toast,
    FormTextInputComponent,
    PrimaryButtonComponent,
    CategoryTreeSelectComponent,
    ButtonModule,
    ConfirmDialogModule
  ],
  providers: [
    MessageService,
    ConfirmationService
  ],
  templateUrl: './category-form.component.html',
  styleUrl: './category-form.component.scss'
})
export class CategoryFormComponent {
  @ViewChild('nameInput') nameInput: any;
  @ViewChild('parentCategoryInput') parentCategoryInput: any;

  @Input() category: CategoriaSimpleDto | null = null;

  @Output() reloadData = new EventEmitter<void>();
  @Output() itemDeleted = new EventEmitter<void>();
  @Output() categoryDeleted = new EventEmitter<number>();

  name: string = '';
  parentCategory: number[] | null = null;

  formSubmitted = signal(false);

  isNameInvalid: boolean = false;

  namePattern = /^.{1,50}$/;

  constructor(
    private messageService: MessageService,
    private categoryService: CategoryService,
    private confirmationService: ConfirmationService
  ) {}

  ngOnInit() {
    this.resetForm();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['category']) {
      this.manageForm();
    }
  }

  manageForm() {
    if (this.category != null) {
      this.loadForm();
    } else {
      this.resetForm();
    }
  }

  confirm() {
    this.formSubmitted.set(true);
    
    if (this.validateForm()) {
      this.messageService.clear();
      this.messageService.add({ 
        severity: 'error', 
        summary: 'Error', 
        detail: "Datos ingresados inválidos", 
        life: 4000
      });
    } else {
      this.category ? this.update() : this.create();
    }
  }
  
  create() {
    const categoria = this.createRequestDto();

    this.categoryService.post(categoria).subscribe({
      next: (response: CategoriaSimpleDto) => this.handleSuccess('creada', response),
      error: (error) => this.handleError(error)
    });
  }

  update() {
    const categoria = this.createRequestDto();

    this.categoryService.put(this.category?.id!, categoria).subscribe({
      next: (response: CategoriaSimpleDto) => this.handleSuccess('actualizada', response),
      error: (error) => this.handleError(error)
    });
  }

  showDeleteConfirmation() {
    this.confirmationService.confirm({
      message: '¡ADVERTENCIA! Al eliminar esta categoría, todas las subcategorías asociadas también serán eliminadas. ¿Desea continuar?',
      header: 'Confirmar eliminación',
      icon: 'pi pi-exclamation-triangle',
      acceptButtonStyleClass: 'p-button-danger',
      acceptLabel: 'Sí, eliminar',
      rejectLabel: 'Cancelar',
      accept: () => {
        this.deleteCategory();
      }
    });
  }

  deleteCategory() {
    if (this.category && this.category.id) {
      this.categoryService.deleteCategory(this.category.id).subscribe({
        next: () => {
          this.messageService.clear();
          this.messageService.add({ 
            severity: 'success', 
            summary: 'Éxito', 
            detail: 'Categoría eliminada exitosamente', 
            life: 4000 
          });
          this.categoryDeleted.emit(this.category!.id);
          this.onDataReloaded();
          this.onItemDeleted();
          this.resetForm();
        },
        error: (error) => {
          this.handleError(error);
        }
      });
    }
  }

  validateForm() {
    return this.isNameInvalid
  }
  
  loadForm() {
    if (this.category) {
      this.name = this.category.nombre;
      this.parentCategory = this.category.padreId ? [this.category.padreId] : null;

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

  private createRequestDto(): CategoriaRequestDto {
    const categoria: CategoriaRequestDto = {
      nombre: this.name.trim(),
      padreId: this.parentCategory && this.parentCategory.length > 0 ? this.parentCategory[0] : undefined
    };
    
    return categoria;
  }

  private handleSuccess(action: string, response: any) {
    this.messageService.clear();
    this.messageService.add({ 
      severity: 'success', 
      summary: 'Éxito', 
      detail: `¡Categoría ${action} exitosamente!`, 
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
      this.parentCategoryInput?.setValue(this.category!.padreId);
    }, 0);
  }

  private resetValidationState() {
    this.isNameInvalid = false;
  }

  private resetFormData() {
    this.name = '';
    this.parentCategory = null;
  }

  private resetFormComponents() {
    this.nameInput?.reset();
    this.parentCategoryInput?.reset();
  }

  private onDataReloaded() {
    this.reloadData.emit();
  }

  private onItemDeleted() {
    console.log('Item deleted');
    this.itemDeleted.emit();
  }
}
