import { Component, signal } from '@angular/core';
import { ProductService } from '../../../../services/product.service';
import { Product } from '../../../../models/product';
import { FormsModule } from '@angular/forms';
import { Toast } from '../../../toast/toast';
import { CategoryService } from '../../../../services/category.service';
import { Category } from '../../../../models/category';

@Component({
  selector: 'app-admin-products',
  imports: [FormsModule, Toast],
  templateUrl: './admin-products.html',
  styleUrl: './admin-products.css',
})
export class AdminProducts {
  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
  ) {}

  products = signal<Product[]>([]);
  isEditOpen = signal(false);
  selectedProduct = signal<Product | null>(null);
  categories = signal<Category[]>([]);
  previewUrl = signal<string | null>(null);
  selectedFile: File | null = null;

  // toast
  showToast = signal(false);
  toastTitle = '';
  toastMessage = '';
  toastType = '';

  showConfirm = signal(false);
  confirmMessage = '';
  confirmAction: (() => void) | null = null;

  onConfirmYes() {
    this.showConfirm.set(false);
    this.confirmAction?.();
    this.confirmAction = null;
  }

  getAllProducts() {
    this.productService.getProducts().subscribe((res) => {
      this.products.set(res);
    });
  }

  getCategories() {
    this.categoryService.getCategories().subscribe((res) => {
      this.categories.set(res);
    });
  }

  openEdit(product: Product) {
    this.selectedProduct.set({ ...product });
    this.isEditOpen.set(true);
  }

  closeEdit() {
    this.isEditOpen.set(false);
    this.selectedProduct.set(null);
  }

  updateProduct(id: number, data: Product) {
    const formData = new FormData();

    formData.append(
      'product',
      new Blob(
        [
          JSON.stringify({
            name: data.name,
            description: data.description,
            price: data.price,
            stock: data.stock,
            image: data.image,
            categoryId: data.category.id,
          }),
        ],
        { type: 'application/json' },
      ),
    );

    if (this.selectedFile) {
      formData.append('image', this.selectedFile);
    }

    this.productService.updateProduct(id, formData).subscribe({
      next: (res) => {
        this.products.update((products) =>
          products.map((product) => (product.id === id ? res : product)),
      );
      
      console.log('Updated product from backend:', res);
        this.showToast.set(true);
        this.toastTitle = 'Success';
        this.toastMessage = 'Product updated successfully';
        this.toastType = 'success';

        this.closeEdit();
      },
      error: (err) => {
        this.showToast.set(true);
        this.toastTitle = 'Error';
        this.toastMessage = err.error;
        this.toastType = 'error';
      },
    });
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (file) {
      this.selectedFile = file;
      this.previewUrl.set(URL.createObjectURL(file));
    }
  }

  getProductImage(image: string): string {
    if (image.startsWith('uploads/')) {
      return `http://localhost:8080/${image}`;
    }

    if (image.includes('/')) {
      return `/images/products/${image}`;
    }

    return `/images/products/${image}`;
  }

  ngOnInit() {
    this.getAllProducts();
    this.getCategories();
  }
}
