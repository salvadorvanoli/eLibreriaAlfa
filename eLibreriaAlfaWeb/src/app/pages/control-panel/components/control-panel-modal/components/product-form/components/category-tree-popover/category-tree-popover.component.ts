import { Component, computed, EventEmitter, Input, Output, signal } from '@angular/core';
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

  selectedCategories: number[] = [];

  @Input() productCategories: number[] = [];
  @Input() formSubmitted = signal(false);

  @Output() categorySelection = new EventEmitter<number[]>();
  @Output() isInputInvalid = new EventEmitter<boolean>();

  showErrorMessage = computed(() => {
    return this.validateSelection() && this.formSubmitted();
  });

  validateSelection() {
    const isInvalid = this.selectedCategories.length === 0;
    this.categorySelection.emit(isInvalid ? [] : this.selectedCategories);
    this.isInputInvalid.emit(isInvalid);
    return isInvalid;
  }

  onCategorySelection(categories: number | number[]) {
    this.selectedCategories = categories as number[];
    this.categorySelection.emit(this.selectedCategories);
  }
}
