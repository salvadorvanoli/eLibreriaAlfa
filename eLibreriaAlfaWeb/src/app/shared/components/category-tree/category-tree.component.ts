import { Component, Output, EventEmitter, Input, SimpleChanges } from '@angular/core';
import { TreeNode } from 'primeng/api';
import { Tree } from 'primeng/tree';
import { MessageComponent } from '../../../shared/components/message/message.component';
import { CategoryService } from '../../../core/services/category.service';
import { CategoriaNodoDto } from '../../../core/models/categoria';

@Component({
  selector: 'app-category-tree',
  standalone: true,
  imports: [
    Tree,
    MessageComponent
  ],
  templateUrl: './category-tree.component.html',
  styleUrl: './category-tree.component.scss'
})
export class CategoryTreeComponent {
  categories: TreeNode[] = [];
  selectedCategory: TreeNode | TreeNode[] | null = null;
  selectedDataNodes: CategoriaNodoDto[] = [];
  categoriesLoaded = false;

  @Input() selectionMode: 'single' | 'multiple' = 'single';
  @Input() showMessage: boolean = false;
  @Output() selection = new EventEmitter<number | number[]>();
  @Output() selectionDataNode = new EventEmitter<CategoriaNodoDto[]>();

  constructor(private categoryService: CategoryService) {}

  ngOnInit() {
    this.loadCategories();
  }

  loadCategories() {
    this.categoryService.getAllCategoriesTree().subscribe(data => {
      this.categories = data.map(category => this.createTreeNode(category));
      this.categoriesLoaded = true;
    });
  }

  onSelectionChange() {
    this.emitSelection();
  }

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
      this.selectedDataNodes = this.selectedCategory ? [this.selectedCategory.data] : [];
    } else {
      const selectedIds = Array.isArray(selectedCategories) ? selectedCategories : [selectedCategories];
      this.selectedCategory = this.findNodesByIds(selectedIds);
      this.selectedDataNodes = this.selectedCategory ? 
        (this.selectedCategory).map(node => node.data) : [];
    }
  }

  reset() {
    this.selectedCategory = this.selectionMode === 'single' ? null : [];
    this.selectedDataNodes = [];
  }

  private createTreeNode(category: CategoriaNodoDto): TreeNode {
    return {
      key: category.id.toString(),
      data: category,
      label: category.nombre,
      children: category.hijos.map(child => this.createTreeNode(child))
    };
  }

  private emitSelection() {
    if (this.selectionMode === 'single') {
      const selectedNode = this.selectedCategory as TreeNode;
      const selectedId = selectedNode?.data?.id || 0;
      this.selectedDataNodes = selectedNode ? [selectedNode.data] : [];
      this.selection.emit(selectedId);
      this.selectionDataNode.emit(selectedNode ? [selectedNode.data] : []);
    } else {
      const selectedNodes = this.selectedCategory as TreeNode[];
      const selectedIds = selectedNodes 
        ? selectedNodes.map(node => node.data?.id).filter(id => id !== undefined && id !== null)
        : [];
      this.selectedDataNodes = selectedNodes ? selectedNodes.map(node => node.data) : [];
      this.selection.emit(selectedIds);
      this.selectionDataNode.emit(selectedNodes ? selectedNodes.map(node => node.data) : []);
    }
  }

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
