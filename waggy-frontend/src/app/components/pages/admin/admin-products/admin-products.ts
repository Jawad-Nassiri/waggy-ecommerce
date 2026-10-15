import { Component, signal } from '@angular/core';
import { ProductService } from '../../../../services/product.service';
import { Product } from '../../../../models/product';
import { FormsModule, NgForm } from '@angular/forms';
import { Toast } from '../../../toast/toast';
import { CategoryService } from '../../../../services/category.service';
import { Category } from '../../../../models/category';
import { ProductRequest } from '../../../../models/product-request';
import { ImageUrlService } from '../../../../services/image-url.service';

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
    private imageUrlService: ImageUrlService,
  ) {}

  products = signal<Product[]>([]);
  isEditOpen = signal(false);
  selectedProduct = signal<Product | null>(null);
  categories = signal<Category[]>([]);
  previewUrl = signal<string | null>(null);
  selectedFile: File | null = null;
  isAddOpen = signal(false);
  selectedImage = signal<File | null>(null);

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
    this.selectedFile = null;
  }

  closeEdit() {
    this.selectedFile = null;
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

  getImageUrl(image: string): string {
    return this.imageUrlService.getImageUrl(image);
  }

  deleteProduct(id: number) {
    this.confirmMessage = 'Are you sure you want to delete this product?';

    this.confirmAction = () => {
      this.productService.deleteProduct(id).subscribe({
        next: () => {
          this.products.update((products) => products.filter((product) => product.id !== id));

          this.showConfirm.set(false);
          this.confirmAction = null;

          this.showToast.set(true);
          this.toastTitle = 'Success';
          this.toastMessage = 'Product deleted successfully';
          this.toastType = 'success';
        },
        error: (err) => {
          this.showConfirm.set(false);
          this.confirmAction = null;

          this.showToast.set(true);
          this.toastTitle = 'Error';
          this.toastMessage = err.error;
          this.toastType = 'error';
        },
      });
    };
  }

  openAddProductModal() {
    this.isAddOpen.set(true);
  }

  closeAddProductModal() {
    this.isAddOpen.set(false);
  }

  addProduct(product: ProductRequest, productForm: NgForm) {
    const formData = new FormData();

    formData.append('product', new Blob([JSON.stringify(product)], { type: 'application/json' }));

    if (this.selectedImage()) {
      formData.append('image', this.selectedImage()!);
    }

    this.productService.addProduct(formData).subscribe({
      next: (res) => {
        this.products.update((products) => [...products, res]);
        productForm.resetForm();
        this.selectedImage.set(null);

        this.showToast.set(true);
        this.toastTitle = 'Success';
        this.toastMessage = 'Product added successfully';
        this.toastType = 'success';

        this.closeAddProductModal();
      },
      error: (err) => {
        this.showToast.set(true);
        this.toastTitle = 'Error';
        this.toastMessage = err.error;
        this.toastType = 'error';
      },
    });
  }

  onAddImageSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    this.selectedImage.set(input.files?.[0] ?? null);
  }

  ngOnInit() {
    this.getAllProducts();
    this.getCategories();
  }
}
