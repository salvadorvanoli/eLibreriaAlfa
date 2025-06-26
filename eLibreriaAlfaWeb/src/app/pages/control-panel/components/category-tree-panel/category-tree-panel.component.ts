import { Component, computed, EventEmitter, Input, Output, signal, ViewChild } from '@angular/core';
import { CategoryTreeComponent } from '../../../../shared/components/category-tree/category-tree.component';
import { CategoriaNodoDto, CategoriaSimpleDto } from '../../../../core/models/categoria';
import { CategoryService } from '../../../../core/services/category.service';

@Component({
  selector: 'app-category-tree-panel',
  standalone: true,
  imports: [
    CategoryTreeComponent
  ],
  templateUrl: './category-tree-panel.component.html',
  styleUrl: './category-tree-panel.component.scss'
})
export class CategoryTreePanelComponent {
  @ViewChild('categoryTree') categoryTree!: CategoryTreeComponent;

  selectedCategory: number | null = null;

  @Output() categorySelection = new EventEmitter<CategoriaSimpleDto>();

  constructor(
    private categoryService: CategoryService
  ) {}

  onCategorySelection(category: number | number[]) {
    this.selectedCategory = Array.isArray(category) && category.length > 0
      ? category[0]
      : typeof category === 'number' 
        ? category
        : null;

    if (this.selectedCategory !== null && this.selectedCategory !== 0) {
      this.categoryService.getById(this.selectedCategory).subscribe(data => {
        if (data)
          this.categorySelection.emit(data);
      });
    }
  }
}
