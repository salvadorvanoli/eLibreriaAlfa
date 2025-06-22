import { Component, Output, EventEmitter, Input, SimpleChanges } from '@angular/core';
import { TreeNode } from 'primeng/api';
import { Tree } from 'primeng/tree';
import { CategoryService } from '../../../core/services/category.service';
import { CategoriaNodoDto } from '../../../core/models/categoria';

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
  categories: TreeNode[] = [];
  selectedCategory: TreeNode | TreeNode[] | null = null;
  private categoriesLoaded = false;

  @Input() selectionMode: 'single' | 'multiple' = 'single';
  @Output() selection = new EventEmitter<number | number[]>();

  constructor(private categoryService: CategoryService) {}

  ngOnInit() {
    this.loadCategories();
  }

  private loadCategories() {
    this.categoryService.getAllCategoriesTree().subscribe(data => {
      this.categories = data.map(category => this.createTreeNode(category));
      this.categoriesLoaded = true;
    });
  }

  private createTreeNode(category: CategoriaNodoDto): TreeNode {
    return {
      key: category.id.toString(),
      data: category,
      label: category.nombre,
      children: category.hijos.map(child => this.createTreeNode(child))
    };
  }

  // Este método se llama cuando hay cualquier cambio en la selección
  onSelectionChange() {
    this.emitSelection();
  }

  private emitSelection() {
    if (this.selectionMode === 'single') {
      const selectedNode = this.selectedCategory as TreeNode;
      const selectedId = selectedNode?.data?.id || 0;
      this.selection.emit(selectedId);
    } else {
      const selectedNodes = this.selectedCategory as TreeNode[];
      const selectedIds = selectedNodes 
        ? selectedNodes.map(node => node.data?.id).filter(id => id !== undefined && id !== null)
        : [];
      this.selection.emit(selectedIds);
    }
  }

  // Métodos públicos para obtener selección actual
  getSelectedCategoryId(): number {
    if (this.selectionMode === 'single' && this.selectedCategory) {
      return (this.selectedCategory as TreeNode).data?.id || 0;
    }
    return 0;
  }

  getSelectedCategoryIds(): number[] {
    if (this.selectionMode === 'multiple' && Array.isArray(this.selectedCategory)) {
      return this.selectedCategory
        .map(node => node.data?.id)
        .filter(id => id !== undefined && id !== null);
    }
    return [];
  }

  // Método para establecer selección programáticamente
  setValue(selectedCategories?: number | number[] | null) {
    if (!this.categoriesLoaded) {
      return;
    }

    if (!selectedCategories || (Array.isArray(selectedCategories) && selectedCategories.length === 0)) {
      this.selectedCategory = this.selectionMode === 'single' ? null : [];
      return;
    }

    if (this.selectionMode === 'single') {
      const selectedId = Array.isArray(selectedCategories) ? selectedCategories[0] : selectedCategories;
      this.selectedCategory = this.findNodeById(selectedId);
    } else {
      const selectedIds = Array.isArray(selectedCategories) ? selectedCategories : [selectedCategories];
      this.selectedCategory = this.findNodesByIds(selectedIds);
    }
  }

  // Método para resetear selección
  reset() {
    this.selectedCategory = this.selectionMode === 'single' ? null : [];
  }

  // Métodos auxiliares privados
  private findNodeById(id: number): TreeNode | null {
    return this.findNodeByIdRecursive(this.categories, id);
  }

  private findNodesByIds(ids: number[]): TreeNode[] {
    const result: TreeNode[] = [];
    for (const id of ids) {
      const node = this.findNodeByIdRecursive(this.categories, id);
      if (node) result.push(node);
    }
    return result;
  }

  private findNodeByIdRecursive(nodes: TreeNode[], id: number): TreeNode | null {
    for (const node of nodes) {
      if (node.data?.id === id) return node;
      if (node.children) {
        const found = this.findNodeByIdRecursive(node.children, id);
        if (found) return found;
      }
    }
    return null;
  }
}
