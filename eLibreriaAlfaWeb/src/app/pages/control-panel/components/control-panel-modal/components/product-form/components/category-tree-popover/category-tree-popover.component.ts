import { Component, computed, EventEmitter, Input, Output, signal, ViewChild } from '@angular/core';
import { Popover } from 'primeng/popover';
import { Message } from 'primeng/message';
import { PrimaryButtonComponent } from '../../../../../../../../shared/components/buttons/primary-button/primary-button.component';
import { CategoryTreeComponent } from "../../../../../../../../shared/components/category-tree/category-tree.component";

@Component({
  selector: 'app-category-tree-popover',
  standalone: true,
  imports: [
    PrimaryButtonComponent,
    CategoryTreeComponent,
    Message,
    Popover
  ],
  templateUrl: './category-tree-popover.component.html',
  styleUrl: './category-tree-popover.component.scss'
})
export class CategoryTreePopoverComponent {
  @ViewChild('categoryTree') categoryTree!: CategoryTreeComponent;

  selectedCategories: number[] = [];

  @Input() formSubmitted = signal(false);
  @Output() categorySelection = new EventEmitter<number[]>();
  @Output() isInputInvalid = new EventEmitter<boolean>();

  showErrorMessage = computed(() => {
    return this.selectedCategories.length === 0 && this.formSubmitted();
  });

  onCategorySelection(categories: number | number[]) {
    this.selectedCategories = Array.isArray(categories) 
      ? categories 
      : typeof categories === 'number' 
        ? [categories] 
        : [];

    this.categorySelection.emit(this.selectedCategories);
    this.isInputInvalid.emit(this.selectedCategories.length === 0);
  }

  setValue(selectedCategories: number[]) {
    this.selectedCategories = selectedCategories || [];
    this.categoryTree?.setValue(this.selectedCategories);
    this.isInputInvalid.emit(this.selectedCategories.length === 0);
  }

  reset() {
    this.selectedCategories = [];
    this.categoryTree?.reset();
    this.isInputInvalid.emit(false);
    this.categorySelection.emit([]);
  }
}
