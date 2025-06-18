import { Component, Output, EventEmitter, Input } from '@angular/core';
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

  categories!: TreeNode[];
  selectedCategory!: TreeNode | TreeNode[];
  private lastEmittedId: number = 0;
  private lastEmittedIds: number[] = [];

  @Input() selectionMode: 'single' | 'multiple' = 'single';
  @Input() selectedCategories: number | number[] | null = null;

  @Output() selection = new EventEmitter<number | number[]>();

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

    if (this.selectedCategories) {
      if (this.selectionMode === 'single') {
        const selectedId = Array.isArray(this.selectedCategories) ? this.selectedCategories[0] : this.selectedCategories;
        this.selectedCategory = this.categories.find(node => node.data.id === selectedId) || {};
      } else {
        this.selectedCategory = this.categories.filter(node => 
          (this.selectedCategories as number[]).includes(node.data.id)
        );
      }
    }
  }

  changedSelectedCategories() {
    if (this.selectionMode === 'single') {
      this.handleSingleSelection();
    } else {
      this.handleMultipleSelection();
    }
  }

  private handleSingleSelection() {
    const selectedNode = this.selectedCategory as TreeNode;
    const newSelectedId: number = selectedNode?.data?.id ? selectedNode.data.id : 0;
  
    if (this.lastEmittedId !== newSelectedId) {
      this.lastEmittedId = newSelectedId;
      this.selection.emit(newSelectedId);
    }
  }

  private handleMultipleSelection() {
    const selectedNodes = this.selectedCategory as TreeNode[];
    const newSelectedIds: number[] = selectedNodes ? selectedNodes.map(node => node.data?.id).filter(id => id) : [];
    
    if (!this.arraysEqual(this.lastEmittedIds, newSelectedIds)) {
      this.lastEmittedIds = [...newSelectedIds];
      this.selection.emit(newSelectedIds);
    }
  }

  private arraysEqual(a: number[], b: number[]): boolean {
    if (a.length !== b.length) return false;
    const sortedA = [...a].sort();
    const sortedB = [...b].sort();
    return sortedA.every((val, index) => val === sortedB[index]);
  }

  getSelectedCategoryId(): number {
    if (this.selectionMode === 'single') {
      const selectedNode = this.selectedCategory as TreeNode;
      return selectedNode?.data?.id || 0;
    }
    return 0;
  }

  getSelectedCategoryIds(): number[] {
    if (this.selectionMode === 'multiple') {
      const selectedNodes = this.selectedCategory as TreeNode[];
      return selectedNodes ? selectedNodes.map(node => node.data?.id).filter(id => id) : [];
    }
    return [];
  }

  reset() {
    this.selectedCategory = this.selectionMode === 'single' ? {} as TreeNode : [];
    this.lastEmittedId = 0;
    this.lastEmittedIds = [];
  }

  private createTreeNode(category: CategoriaNodoDto): TreeNode {
    return {
      key: category.id.toString(),
      data: category,
      label: category.nombre,
      children: category.hijos.map((child) => this.createTreeNode(child))
    };
  }
}
