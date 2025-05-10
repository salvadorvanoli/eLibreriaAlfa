import { Component } from '@angular/core';
import { TreeNode } from 'primeng/api';
// import { NodeService } from '@/service/nodeservice';
import { Tree } from 'primeng/tree';

@Component({
  selector: 'app-category-tree',
  standalone: true,
  imports: [
    Tree
  ],
  providers: [
    // NodeService
  ],
  templateUrl: './category-tree.component.html',
  styleUrl: './category-tree.component.scss'
})
export class CategoryTreeComponent {

  files!: TreeNode[];

  selectedFiles!: TreeNode[];

  /*
  constructor(private nodeService: NodeService) {}

  ngOnInit() {
      this.nodeService.getFiles().then((data) => (this.files = data));
  }
  */

}
