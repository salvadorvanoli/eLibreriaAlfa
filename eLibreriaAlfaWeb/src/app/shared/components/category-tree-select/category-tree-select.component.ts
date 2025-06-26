import { Component, Output, EventEmitter, Input, computed, signal } from '@angular/core';
import { TreeNode } from 'primeng/api';
import { FormsModule } from '@angular/forms';
import { TreeSelect } from 'primeng/treeselect';
import { FloatLabel } from 'primeng/floatlabel';
import { Message } from 'primeng/message';
import { CategoryService } from '../../../core/services/category.service';
import { CategoriaNodoDto } from '../../../core/models/categoria';

@Component({
  selector: 'app-category-tree-select',
  standalone: true,
  imports: [
    FormsModule,
    TreeSelect,
    FloatLabel,
    Message
  ],
  templateUrl: './category-tree-select.component.html',
  styleUrl: './category-tree-select.component.scss'
})
export class CategoryTreeSelectComponent {
  categories: TreeNode[] = [];
  selectedCategory: TreeNode | TreeNode[] | null = null;
  selectedDataNodes: CategoriaNodoDto[] = [];
  private categoriesLoaded = false;

  @Input() selectionMode: 'single' | 'multiple' = 'multiple';
  @Input() placeholder: string = 'Selecciona categorías';
  @Input() formSubmitted = signal(false);
  @Input() required: boolean = true;

  @Output() categorySelection = new EventEmitter<number[]>();
  @Output() categorySelectionDataNode = new EventEmitter<CategoriaNodoDto[]>();
  @Output() isInputInvalid = new EventEmitter<boolean>();

  constructor(
    private categoryService: CategoryService
  ) {}

  ngOnInit() {
    this.loadCategories();
  }

  showErrorMessage = computed(() => {
    return this.validateInput() && this.formSubmitted() && this.required;
  });

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

  onNodeSelect(event: any) {
    console.log('Node selected:', event);
    this.addNodeToSelection(event.node);
    this.emitSelection();
    this.isInputInvalid.emit(this.validateInput());
  }

  onNodeUnselect(event: any) {
    console.log('Node unselected:', event);
    this.removeNodeFromSelection(event.node);
    this.emitSelection();
    this.isInputInvalid.emit(this.validateInput());
  }

  private addNodeToSelection(node: TreeNode) {
    if (this.selectionMode === 'single') {
      this.selectedCategory = node;
      this.selectedDataNodes = [node.data];
    } else {
      if (!this.selectedCategory) {
        this.selectedCategory = [];
      }
      
      const selectedNodes = this.selectedCategory as TreeNode[];
      const existingIndex = selectedNodes.findIndex(n => n.key === node.key);
      
      if (existingIndex === -1) {
        selectedNodes.push(node);
        this.selectedDataNodes = selectedNodes.map(n => n.data);
      }
    }
  }

  private removeNodeFromSelection(node: TreeNode) {
    if (this.selectionMode === 'single') {
      this.selectedCategory = null;
      this.selectedDataNodes = [];
    } else {
      if (Array.isArray(this.selectedCategory)) {
        const selectedNodes = this.selectedCategory as TreeNode[];
        const filteredNodes = selectedNodes.filter(n => n.key !== node.key);
        
        this.selectedCategory = filteredNodes;
        this.selectedDataNodes = filteredNodes.map(n => n.data);
      }
    }
  }

  private emitSelection() {
    if (this.selectionMode === 'single') {
      const selectedNode = this.selectedCategory as TreeNode;
      const selectedId = selectedNode?.data?.id || 0;
      this.selectedDataNodes = selectedNode ? [selectedNode.data] : [];
      const selectedIds = selectedId ? [selectedId] : [];
      this.categorySelection.emit(selectedIds);
      this.categorySelectionDataNode.emit(this.selectedDataNodes);
    } else {
      const selectedNodes = this.selectedCategory as TreeNode[];
      const selectedIds = selectedNodes 
        ? selectedNodes.map(node => node.data?.id).filter(id => id !== undefined && id !== null)
        : [];
      this.selectedDataNodes = selectedNodes ? selectedNodes.map(node => node.data) : [];
      this.categorySelection.emit(selectedIds);
      this.categorySelectionDataNode.emit(this.selectedDataNodes);
    }
  }

  getSelectedCategoryIds(): number[] {
    if (this.selectionMode === 'single' && this.selectedCategory) {
      const id = (this.selectedCategory as TreeNode).data?.id;
      return id ? [id] : [];
    }
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
      this.selectedDataNodes = [];
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
        (this.selectedCategory as TreeNode[]).map(node => node.data) : [];
    }
  }

  reset() {
    this.selectedCategory = this.selectionMode === 'single' ? null : [];
    this.selectedDataNodes = [];
    this.categorySelection.emit([]);
    this.categorySelectionDataNode.emit([]);
    this.isInputInvalid.emit(false);
  }

  private validateInput(): boolean {
    if (!this.required) return false;
    
    if (this.selectionMode === 'single') {
      return !this.selectedCategory;
    } else {
      return !this.selectedCategory || (Array.isArray(this.selectedCategory) && this.selectedCategory.length === 0);
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
