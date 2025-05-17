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
  selectedCategories: CategoriaNodo[] = [];
  
  @Output() selection = new EventEmitter<CategoriaNodo[]>();

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

  selectCategory(event: any) {
    this.selectedCategories.push(event.node.data);
    this.selection.emit(this.selectedCategories);
  }

  unselectCategory(event: any) {
    this.selectedCategories = this.selectedCategories.filter((element) => element.id !== event.node.data.id);
    this.selection.emit(this.selectedCategories);
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
