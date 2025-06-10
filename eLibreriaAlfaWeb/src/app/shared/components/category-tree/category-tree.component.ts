import { Component, Output, EventEmitter } from '@angular/core';
import { TreeNode } from 'primeng/api';
import { Tree } from 'primeng/tree';
import { CategoryService } from '../../../core/services/category.service';
import { CategoriaNodo } from '../../../core/models/categoria';

@Component({
  selector: 'app-category-tree',
  standalone: true,
  imports: [
    Tree
  ],
  templateUrl: './category-tree.component.html',
  styleUrl: './category-tree.component.scss'
})
export class CategoryTreeComponent {

  categories!: TreeNode[];
  selectedCategory!: TreeNode;
  private lastEmittedId: number = 0;
  
  @Output() selection = new EventEmitter<number>();

  constructor(
    private categoryService: CategoryService
  ) {}

  ngOnInit() {
    this.categories = [];
    this.categoryService.getAllCategoriesTree().subscribe((data) => {
      data.forEach((category) => {
        const node: TreeNode = this.createTreeNode(category);
        this.categories.push(node);
      });
    });
  }

  changedSelectedCategories() {
    const newSelectedId: number = this.selectedCategory?.data.id ? this.selectedCategory.data.id : 0;
  
    if (this.lastEmittedId !== newSelectedId) {
      this.lastEmittedId = newSelectedId;
      this.selection.emit(newSelectedId);
    }
  }

  private createTreeNode(category: CategoriaNodo): TreeNode {
    return {
      key: category.id.toString(),
      data: category,
      label: category.nombre,
      children: category.hijos.map((child) => ({
        data: child,
        label: child.nombre,
        children: []
      }))
    };
  }
}
